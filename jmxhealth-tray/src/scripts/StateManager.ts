namespace jmxhealth {
    var pubsub = require('pubsub-js'),
        stateManager: StateManager,
        config: TrayConfig = require('./config.json'),
        pollTimeInSeconds = 10,
        stateWeight = {
            'ALERT': 30,
            'WARN': 20,
            'OK': 10
        };

    class StateManager {
        private requests: StateRequest[];

        constructor(private $http: angular.IHttpService, private $interval: angular.IIntervalService, private $q: angular.IQService) {

        }

        public start(): void {
            if (!config.servers || config.servers.length === 0) {
                pubsub.publish(NO_SERVERS, 'No Servers configured');
            }
            else {
                this.requests = this.prepareConfig();
                this.poll();
                this.$interval(() => {
                    this.poll();
                }, pollTimeInSeconds * 1000, 0, false);
            }
        }

        private poll(): void {
            var responses: angular.IPromise<api.StateResponse[]>[] = [];

            this.requests.forEach((request) => {
                responses.push(this.$http.get(request.serverUrl, {
                    data: null
                }).then((stateResponse: api.StateResponse[]) => {
                    //TODO: replace SELF with reuqest.serverName
                    return stateResponse;
                }, (error: any) => {
                    var errorState: api.StateResponse[] = [{
                        application: request.serverName,
                        environment: 'Monitoring Server',
                        overallState: 'ALERT',
                        unsuccessfulAttributes: [{
                            attributeName: 'Connection',
                            state: 'ALERT',
                            timestamp: new Date().toISOString(),
                            message: this.errorToMessage(error)
                        }]
                    }];

                    return this.$q.resolve<api.StateResponse[]>(errorState);
                }));
            });

            this.$q.all(responses).then((states: api.StateResponse[][]) => {
                var allStates: api.StateResponse[] = [];

                states.forEach((state: api.StateResponse[]) => {
                    allStates = allStates.concat(state);
                });

                var overallState = this.getOverallStates(allStates);
                pubsub.publish(STATES, allStates);
                pubsub.publish(OVERALL_STATE, overallState);
            });
        }

        private errorToMessage(error: any): string {
            if (error.status < 0) {
                return 'An Error occured while communicating with the server. Check if the server is alive and a connection can be established.';
            }
            else {
                return `Status: ${error.status} - Message: ${error.statusText}`;
            }
        }

        private getOverallStates(states: api.StateResponse[]): string {
            var overallState;

            for (let state of states) {
                if (!overallState || stateWeight[state.overallState] > stateWeight[overallState]) {
                    overallState = state.overallState;
                }
            }

            return overallState;
        }

        private randomState(rand: number): string {
            if (rand < 0.3) {
                return 'ALERT';
            }

            if (rand < 0.6) {
                return 'WARN';
            }

            return 'OK';
        }

        private prepareConfig(): StateRequest[] {
            var requests: StateRequest[] = [];

            config.servers.forEach((server) => {
                requests.push({
                    serverUrl: server.url,
                    serverName: server.name
                });
            });

            return requests;
        }
    }

    interface StateRequest {
        serverName: string;
        serverUrl: string;
    }

    angular.module('jmxhealth.state', [])
        .run(['$http', '$interval', '$q', function($http: angular.IHttpService, $interval: angular.IIntervalService, $q: angular.IQService) {
            console.log('creating state manager');
            stateManager = new StateManager($http, $interval, $q);

            pubsub.subscribe(START, () => {
                stateManager.start();
            });

            pubsub.publish(INITIALIZED, 'StateManager');
        }]);
}

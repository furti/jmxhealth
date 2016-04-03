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
                pubsub.publish(topic.NO_SERVERS, 'No Servers configured');
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

                var filterResult = this.filterFailedStates(allStates);

                pubsub.publish(topic.FAILED_STATES, filterResult.failedStates);
                pubsub.publish(topic.STATES, allStates);
                pubsub.publish(topic.OVERALL_STATE, filterResult.overallState);
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

        private filterFailedStates(states: api.StateResponse[]): FailedStateFilter {
            var filterResult: FailedStateFilter = {
                overallState: null,
                failedStates: []
            };

            for (let state of states) {
                if (!filterResult.overallState || stateWeight[state.overallState] > stateWeight[filterResult.overallState]) {
                    filterResult.overallState = state.overallState;
                }

                if (state.overallState !== 'OK') {
                    filterResult.failedStates.push(state);
                }
            }

            return filterResult;
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

    interface FailedStateFilter {
        overallState: string;
        failedStates: api.StateResponse[];
    }

    angular.module('jmxhealth.state', [])
        .run(['$http', '$interval', '$q', function($http: angular.IHttpService, $interval: angular.IIntervalService, $q: angular.IQService) {
            console.log('creating state manager');
            stateManager = new StateManager($http, $interval, $q);

            pubsub.subscribe(topic.START, () => {
                stateManager.start();
            });

            pubsub.publish(topic.INITIALIZED, 'StateManager');
        }]);
}

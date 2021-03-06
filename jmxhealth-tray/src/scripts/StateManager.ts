namespace jmxhealth {
    var pubsub: pubsub.PubSub = require('pubsub-js'),
        open: (path: string) => void = require('open'),
        stateManager: StateManager,
        config: TrayConfig = require('./config.json'),
        pollTimeInSeconds = 30,
        stateWeight = {
            'ALERT': 30,
            'WARN': 20,
            'OK': 10
        };

    class StateManager {
        private requests: StateRequest[];
        private pausedStates: PausedApplication[];

        constructor(private $http: angular.IHttpService, private $interval: angular.IIntervalService, private $q: angular.IQService) {
            this.pausedStates = [];

            pubsub.subscribe(topic.PAUSE, (message: string, state: api.StateResponse) => {
                this.pausedStates.push({
                    application: state.application,
                    environment: state.environment
                });
            });

            pubsub.subscribe(topic.RESUME, (message: string, state: api.StateResponse) => {
                this.pausedStates = this.pausedStates.filter((pausedState) => {
                    if (pausedState.application === state.application && pausedState.environment === state.environment) {
                        return false;
                    }

                    return true;
                });
            });
        }

        public start(): void {
            if (!config.servers || config.servers.length === 0) {
                pubsub.publish(topic.NO_SERVERS, 'No Servers configured');
                Notify.warn('No servers configured. Add some servers in the config.json and restart the application.').then(() => {
                    open(process.cwd());
                });
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
                responses.push(this.$http({
                    url: request.serverUrl,
                    method: 'POST',
                    data: request.filter
                }).then((response: angular.IHttpPromiseCallbackArg<api.StateResponse[]>) => {
                    if (response.data) {
                        response.data.forEach((entry) => {
                            if (entry.application === HealthUtils.SELF_KEYWOARD) {
                                entry.application = request.serverName;
                            }
                        });
                    }

                    return response.data;
                }, (error: any) => {
                    var errorState: api.StateResponse[] = [{
                        application: request.serverName,
                        environment: 'Monitoring Server',
                        server: null,
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

                var filterResult = this.prepareStates(allStates);

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

        private prepareStates(states: api.StateResponse[]): FailedStateFilter {
            var filterResult: FailedStateFilter = {
                overallState: null,
                failedStates: []
            };

            for (var state of states) {
                if (!filterResult.overallState || stateWeight[state.overallState] > stateWeight[filterResult.overallState]) {
                    filterResult.overallState = state.overallState;
                }

                if (state.overallState !== 'OK') {
                    filterResult.failedStates.push(state);
                }

                //If the application is paused --> set the state to paused
                if (this.pausedStates.filter((pausedState) => {
                    if (state.application === pausedState.application && state.environment === pausedState.environment) {
                        return true;
                    }

                    return false;
                }).length > 0) {
                    state.paused = true;
                }
            }

            return filterResult;
        }

        private prepareConfig(): StateRequest[] {
            var requests: StateRequest[] = [];

            config.servers.forEach((server) => {
                var request: StateRequest = {
                    serverUrl: server.url + '/states',
                    serverName: server.name,
                    filter: server.applications
                };

                //Add self to the filters. We always want to know the state of the monitoring server itself.
                if (request.filter) {
                    request.filter.push({
                        application: HealthUtils.SELF_KEYWOARD,
                        environment: null
                    });
                }

                requests.push(request);
            });

            return requests;
        }
    }

    interface StateRequest {
        serverName: string;
        serverUrl: string;
        filter: api.ApplicationFilter[];
    }

    interface FailedStateFilter {
        overallState: string;
        failedStates: api.StateResponse[];
    }

    interface PausedApplication {
        application: string;
        environment: string;
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

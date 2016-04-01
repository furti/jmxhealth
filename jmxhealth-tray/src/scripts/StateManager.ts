namespace jmxhealth {
    var pubsub = require('pubsub-js'),
        stateManager: StateManager,
        config: TrayConfig = require('./config.json'),
        stateWeight = {
            'ALERT': 30,
            'WARN': 20,
            'OK': 10
        };

    class StateManager {
        constructor(private $http: angular.IHttpService, private $interval: angular.IIntervalService) {

        }

        public start(): void {
            console.log('start called');
            if (!config.servers || config.servers.length === 0) {
                pubsub.publish(NO_SERVERS, 'No Servers configured');
            }
            else {
                this.$interval(() => {
                    this.poll();
                }, 10000, 0, false);
            }
        }

        private poll(): void {
            // var states: api.StateResponse[] = [
            //     {
            //         application: 'prod' + Math.random(),
            //         environment: 'PROD',
            //         overallState: this.randomState(Math.random())
            //     },
            //     {
            //         application: 'qa' + Math.random(),
            //         environment: 'QA',
            //         overallState: this.randomState(Math.random())
            //     },
            //     {
            //         application: 'test' + Math.random(),
            //         environment: 'TEST',
            //         overallState: this.randomState(Math.random())
            //     }];
            //
            // var overallState = this.getOverallStates(states);
            // pubsub.publish(STATES, states);
            // pubsub.publish(OVERALL_STATE, overallState);
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
    }

    angular.module('jmxhealth.state', [])
        .run(['$http', '$interval', function($http: angular.IHttpService, $interval: angular.IIntervalService) {
            console.log('creating state manager');
            stateManager = new StateManager($http, $interval);

            pubsub.subscribe(START, () => {
                stateManager.start();
            });

            pubsub.publish(INITIALIZED, 'StateManager');
        }]);
}

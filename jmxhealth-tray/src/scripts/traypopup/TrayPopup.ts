namespace jmxhealth {
    var pubsub: pubsub.PubSub = require('pubsub-js');

    class StateListController {
        public statesByEnvironment: { [environment: string]: api.StateResponse[] };
        public messageTitle: string;
        public message: string;

        public static $injects = ['$scope'];

        constructor($scope: angular.IScope) {
            pubsub.subscribe(topic.NO_SERVERS, () => {
                this.messageTitle = 'No Servers';
                this.message = `There are no servers configured in ${process.cwd()}/config.json. Please configure at least one server and restart the application.`
                $scope.$apply();
            });

            pubsub.subscribe(topic.STATES, (message: string, states: api.StateResponse[]) => {
                this.statesByEnvironment = this.prepareStates(states);
                $scope.$apply();
            });

            pubsub.subscribe(topic.PAUSE, () => {
                $scope.$apply();
            });

            pubsub.subscribe(topic.RESUME, () => {
                $scope.$apply();
            });
        }

        public showDetail(state: api.StateResponse): void {
            pubsub.publish(topic.SHOW_DETAIL, state);
        }

        public stateIcon(state: api.StateResponse): string {
            return HealthUtils.stateIcon(state.overallState);
        }

        public stateIconClass(state: api.StateResponse): string {
            return HealthUtils.stateIconClass(state.overallState);
        }

        public togglePauseState(state: api.StateResponse): void {
            if (state.paused) {
                state.paused = false;

                pubsub.publish(topic.RESUME, state);
            }
            else {
                state.paused = true;

                pubsub.publish(topic.PAUSE, state);
            }

        }

        public pauseIcon(state: api.StateResponse): string {
            if (state.paused) {
                return 'play_arrow';
            }
            else {
                return 'pause';
            }
        }

        private prepareStates(states: api.StateResponse[]): { [environment: string]: api.StateResponse[] } {
            var grouped: { [environment: string]: api.StateResponse[] } = {};

            states.forEach(function(state) {
                if (!grouped[state.environment]) {
                    grouped[state.environment] = [];
                }

                grouped[state.environment].push(state);
            });

            return grouped;
        }
    }

    angular.module('jmxhealth.popup', ['jmxhealth.common'])
        .component('stateList', {
            controller: StateListController,
            templateUrl: './target/templates/state-list.html'
        });
}

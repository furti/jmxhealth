namespace jmxhealth {
    var pubsub = require('pubsub-js');

    class StateListController {
        public statesByEnvironment: { [environment: string]: api.StateResponse[] };
        public static $injects = ['$scope'];

        constructor($scope: angular.IScope) {
            pubsub.subscribe(STATES, (message: string, states: api.StateResponse[]) => {
                this.statesByEnvironment = this.prepareStates(states);
                $scope.$apply();
            });
        }

        public stateIcon(state: api.StateResponse): string {
            var icon = '../../icons/';

            if (state.overallState === 'ALERT') {
                icon += 'error';
            }
            else if (state.overallState === 'WARN') {
                icon += 'warning';
            }
            else if (state.overallState === 'OK') {
                icon += 'check';
            }

            return icon + '.png';
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
            templateUrl: './state-list.html'
        });
}

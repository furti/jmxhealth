namespace jmxhealth {

    class StateListController {
        public statesByEnvironment: { [environment: string]: api.StateResponse[] };

        constructor() {
            this.statesByEnvironment = {
                'PROD': [{
                    application: 'test',
                    environment: 'PROD',
                    overallState: 'OK'
                }, {
                        application: 'other',
                        environment: 'PROD',
                        overallState: 'ALERT'
                    }],
                'QA': [{
                    application: 'third',
                    environment: 'QA',
                    overallState: 'WARN'
                }]
            };
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
    }

    angular.module('jmxhealth.popup', ['ngMaterial'])
        .config(['$mdThemingProvider', function($mdThemingProvider) {
            $mdThemingProvider.theme('default')
                .primaryPalette('pink')
                .accentPalette('orange');
        }])
        .component('stateList', {
            controller: StateListController,
            templateUrl: './state-list.html'
        });
}

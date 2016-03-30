namespace jmxhealth {
    export const OVERALL_STATE = 'jmxhealth.overallstate';

    angular.module('jmxhealth.common', ['ngMaterial'])
        .config(['$mdThemingProvider', function($mdThemingProvider) {
            //There seems to be a bug with the background palette. So we use this workaraound (https://github.com/angular/material/issues/1450)
            var background = $mdThemingProvider.extendPalette('grey', {
                'A100': 'f2f2f2'
            });
            $mdThemingProvider.definePalette('background', background);

            $mdThemingProvider.theme('default')
                .backgroundPalette('background');
        }]);
}

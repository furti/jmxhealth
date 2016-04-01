namespace jmxhealth {
    export const NO_SERVERS = 'jmxhealth.noservers';
    export const OVERALL_STATE = 'jmxhealth.overallstate';
    export const STATES = 'jmxhealth.states';
    export const START = 'jmxhealth.start';
    export const INITIALIZED = 'jmxhealth.initialized';

    angular.module('jmxhealth.common', ['ngMaterial'])
        .config(['$mdThemingProvider', '$compileProvider', function($mdThemingProvider, $compileProvider: angular.ICompileProvider) {
            //There seems to be a bug with the background palette. So we use this workaraound (https://github.com/angular/material/issues/1450)
            var background = $mdThemingProvider.extendPalette('grey', {
                'A100': 'f2f2f2'
            });
            $mdThemingProvider.definePalette('background', background);

            $mdThemingProvider.theme('default')
                .backgroundPalette('background');

            //We need this. Otherwise angular adds a unsafe in front of the scheme to prevent malicious attacks.
            //http://nwjs.readthedocs.org/en/v0.13.0-beta5/For%20Users/FAQ/#images-are-broken-in-anugarjs-and-receive-failed-to-load-resource-xxx-neterr_unknown_url_scheme-in-devtools
            $compileProvider.imgSrcSanitizationWhitelist(/^\s*((https?|ftp|file|blob|chrome-extension):|data:image\/)/);
            $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|tel|file:chrome-extension):/);
        }]);
}

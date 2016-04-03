namespace jmxhealth {
    export namespace topic {
        export const NO_SERVERS = 'jmxhealth.noservers';
        export const OVERALL_STATE = 'jmxhealth.overallstate';
        export const STATES = 'jmxhealth.states';
        export const FAILED_STATES = 'jmxhealth.failedstates';
        export const START = 'jmxhealth.start';
        export const INITIALIZED = 'jmxhealth.initialized';
        export const SHOW_DETAIL = 'jmxhealth.showdetail';
    }

    export class HealthUtils {
        public static stateIcon(state: api.StateResponse): string {
            var icon = './icons/';

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

    angular.module('jmxhealth.common', ['ngMaterial'])
        .config(['$mdThemingProvider', '$compileProvider', function($mdThemingProvider, $compileProvider: angular.ICompileProvider) {
            $mdThemingProvider.theme('default');

            //We need this. Otherwise angular adds a unsafe in front of the scheme to prevent malicious attacks.
            //http://nwjs.readthedocs.org/en/v0.13.0-beta5/For%20Users/FAQ/#images-are-broken-in-anugarjs-and-receive-failed-to-load-resource-xxx-neterr_unknown_url_scheme-in-devtools
            $compileProvider.imgSrcSanitizationWhitelist(/^\s*((https?|ftp|file|blob|chrome-extension):|data:image\/)/);
            $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|tel|file:chrome-extension):/);
        }]);
}

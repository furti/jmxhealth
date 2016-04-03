namespace jmxhealth {
    export namespace topic {
        export const NO_SERVERS = 'jmxhealth.noservers';
        export const OVERALL_STATE = 'jmxhealth.overallstate';
        export const STATES = 'jmxhealth.states';
        export const FAILED_STATES = 'jmxhealth.failedstates';
        export const START = 'jmxhealth.start';
        export const INITIALIZED = 'jmxhealth.initialized';
        export const SHOW_DETAIL = 'jmxhealth.showdetail';
        export const PAUSE = 'jmxhealth.pausestate';
        export const RESUME = 'jmxhealth.resumestate';
    }

    export class HealthUtils {
        static SELF_KEYWOARD = 'jmxhealth.self';

        public static stateIconPath(state: string): string {
            var icon = './icons/';

            if (state === 'ALERT') {
                icon += 'error';
            }
            else if (state === 'WARN') {
                icon += 'warning';
            }
            else if (state === 'OK') {
                icon += 'check';
            }

            return icon + '.png';
        }

        public static stateIcon(state: string): string {
            if (state === 'ALERT') {
                return 'error';
            }
            else if (state === 'WARN') {
                return 'warning';
            }
            else if (state === 'OK') {
                return 'check_circle';
            }
        }

        public static stateIconClass(state: string): string {
            if (state === 'ALERT') {
                return 'alert';
            }
            else if (state === 'WARN') {
                return 'warn';
            }
            else if (state === 'OK') {
                return 'success';
            }
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

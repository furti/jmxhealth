namespace jmxhealth {
    var notifier: any = require('node-notifier'),
        path: any = require('path'),
        Q: angular.IQService;

    export class Notify {
        public static success(message: string): angular.IPromise<void> {
            return Notify.notify(message, 'check.png');
        }

        public static warn(message: string): angular.IPromise<void> {
            return Notify.notify(message, 'warning.png');
        }

        public static error(message: string): angular.IPromise<void> {
            return Notify.notify(message, 'error.png');
        }

        private static notify(message: string, icon: string): angular.IPromise<void> {
            notifier.notify({
                title: 'jmxhealth',
                message: message,
                icon: path.join(process.cwd(), 'icons', icon),
                sound: true,
                wait: true
            });

            var defered = Q.defer<void>();

            notifier.on('click', function(notifierObject, options) {
                console.log(notifierObject);
                console.log(options);

                defered.resolve();
            });

            notifier.on('timeout', function(notifierObject, options) {
                console.log(notifierObject);
                console.log(options);

                defered.reject();
            });


            return defered.promise;
        }
    }

    angular.module('jmxhealth.notify', [])
        .run(['$q', function($q: angular.IQService) {
            Q = $q;
        }]);
}

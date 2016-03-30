namespace jmxhealth {
    var pubsub = require('pubsub-js');

    angular.module('jmxhealth', ['jmxhealth.common', 'jmxhealth.tray'])
        .controller('TestController', function() {
            this.publish = function(state: string): void {
                pubsub.publish(OVERALL_STATE, state);
            };
        });
}

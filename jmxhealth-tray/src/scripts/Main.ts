namespace jmxhealth {
    var pubsub = require('pubsub-js'),
        initializedServices = 0;

    //When all services are initialized we sent the start event
    pubsub.subscribe(topic.INITIALIZED, (message: string, data: string) => {
        initializedServices += 1;

        if (initializedServices === 2) {
            pubsub.publish(topic.START, 'Let`s get this party started :)');
        }
    });

    angular.module('jmxhealth', ['jmxhealth.common', 'jmxhealth.state', 'jmxhealth.tray', 'jmxhealth.state', 'jmxhealth.detail', 'jmxhealth.notify'])
        .controller('TestController', function() {
            this.publish = function(state: string): void {
                pubsub.publish(topic.OVERALL_STATE, state);
            };
        });
}

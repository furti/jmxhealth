namespace jmxhealth {
    var pubsub = require('pubsub-js'),
        initializedServices = 0;

    //When all services are initialized we sent the start event
    pubsub.subscribe(INITIALIZED, (message: string, data: string) => {
        console.log('INITIALIZED: ' + data);
        initializedServices += 1;

        if (initializedServices === 2) {
            pubsub.publish(START, 'Let`s get this party started :)');
        }
    });


    angular.module('jmxhealth', ['jmxhealth.common', 'jmxhealth.state', 'jmxhealth.tray', 'jmxhealth.state'])
        .controller('TestController', function() {
            this.publish = function(state: string): void {
                pubsub.publish(OVERALL_STATE, state);
            };
        });
}

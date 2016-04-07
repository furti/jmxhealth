namespace jmxhealth {
    var log = require('simple-node-logger').createSimpleFileLogger('jmxhealth.log');
    var util = require('util');

    log.setLevel('debug');

    function formatMessage(message?: any, optionalParams?: any[]) {
        if (!message) {
            return;
        }

        if (!optionalParams || optionalParams.length === 0) {
            return message;
        }

        var prepared = message.replace('%i', '%d')
            .replace('%f', '%d')
            .replace('%o', '%j')
            .replace('%O', '%j')
            .replace('%c', '%j');

        return util.format.apply(this, [prepared].concat(optionalParams));
    }

    console.trace = function(message?: any, ...optionalParams: any[]) {
        log.trace(formatMessage(message, optionalParams));
    };

    console.log = function(message?: any, ...optionalParams: any[]) {
        log.debug(formatMessage(message, optionalParams));
    };

    console.debug = function(message?: any, ...optionalParams: any[]) {
        log.debug(formatMessage(message, optionalParams));
    };

    console.info = function(message?: any, ...optionalParams: any[]) {
        log.info(formatMessage(message, optionalParams));
    };

    console.warn = function(message?: any, ...optionalParams: any[]) {
        log.warn(formatMessage(message, optionalParams));
    };

    console.error = function(message?: any, ...optionalParams: any[]) {
        log.error(formatMessage(message, optionalParams));
    };

    console.error('test1');
    console.log('test object %o', {
        value: 1
    });
}

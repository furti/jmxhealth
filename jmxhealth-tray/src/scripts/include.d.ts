/// <reference path="../../typings/tsd.d.ts"/>

declare var require: (moduleName: string) => any;
declare var process: any;
declare var nw: any;

declare namespace jmxhealth.api {
    interface StateResponse {
        application: string;
        environment: string;
        overallState: string;
        unsuccessfulAttributes?: AttributeState[];
    }

    interface AttributeState {
        attributeName: string;
        state: string;
        message: string;
        timestamp: string;
    }
}

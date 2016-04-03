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

declare namespace pubsub {
    export type MessageHandler = (message: string, data: any) => void;

    export class PubSub {
        subscribe(topic: string, handler: MessageHandler): void;
        publish(topic: string, data: any): void;
    }
}

namespace jmxhealth {
    export interface DetailStateResponse extends api.StateResponse {
        unsuccessfulAttributes: DetailAttributeState[];
    }

    export interface DetailAttributeState extends api.AttributeState {
        lines: string[];
    }
}

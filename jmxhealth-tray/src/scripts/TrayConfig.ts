namespace jmxhealth {
    export interface TrayConfig {
        servers: Server[];
    }

    export interface Server {
        name: string;
        url: string;
        applications: api.ApplicationFilter[];
    }
}

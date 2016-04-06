# jmxhealth-tray

## Installation
Download the jmxhealth-standalone.7z and extract it to a folder of your choice.
The folder contains a config.json file that holds the configuration for the tray icon.

## Config

### name
The name of the jmxhealth server.

### url
The url for the jmxhealth server.

### applications
A list of applications to watch.

{
  "servers": [{
    "name": "local",
    "url": "http://localhost:8080",
    "applications": [{
      "application": "important-app",
      "environment": "PROD"
    }]
  }]
}

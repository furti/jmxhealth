#jmxhealth-server

##Installation
Download the jmxhealth-server.war and deploy it to a Servlet Container of your choice.
The war is currently not available in the Maven Central Repository. But it might be available in the future.
For now see the Downloads section or release the latest tag to your own Maven Repository.

Set the path to the directory jmxhealth-server should use either by setting the System Property or by adding a Parameter to the Servlet Context Configuration.

#### System Property
```
-Dio.github.furti.jmxhealth.data-location=path/to/data/folder
```

#### Servlet Context
```xml
<Context>
  ...
  <Parameter name="io.github.furti.jmxhealth.data-location" value="path/to/data/folder" override="false"/>
  ...
</Context>
```

*The data folder must contain a jmxhealth.json file that contains the Remote configuration.*

## jmxhealth.json

The config contains a JSON object with the following properties.

### servers
A list of server configuration objects. See the _Server subfields_ section below.

### Server subfields

#### application
The name of the monitored application.

#### environment
The environment of the monitored application. E.g. "PROD", "TEST",...

#### host
The hostname or IP address of the monitored application.

#### port
The port of the JMX registry of the monitored application.

#### username (optional)
The username for the JMX Agent. If the Agent can be accessed without authentication this field is not required.

#### password (optional)
The encrypted password for the JMX Agent. If the Agent can be accessed without authentication this field is not required.
If the password is used the ```-Dio.github.furti.jmxhealth.crypt-key=<secretkey>``` System Property must be set.
See [jmxhealth-crypto](../jmxhealth-crypto/README.md) for further details.

#### watchers
A list of beans and attributes to check for this application.
See _Watcher subfields_ below.

### Watcher subfields

#### beanName
The name of the JMX Bean to monitor.

#### checks
A list of checks to perform on this bean.
See _Check subfields_ below.

### Check subfields

#### displayName
A string that should be displayed when this check fails.

#### attributeName (optional)
The name of the attribute that should be validated. If not set the bean itself will be passed to the validator.

#### type
The type of validation to perform on the bean or the attributes of the bean.
See _Validators_ below for allowed values.

#### validationConfig
The config for the validator used. The available fields depend on the validator used.

## Validators

### PERCENTAGE
Calculates the percentage occupied of the attribute to watch.

```javascript
"validationConfig": {
  "max": "The attribute name for the max value",
  "actual": "The attribute name for the current value",
  "warnOn": <Percentage when a warning should be emitted>,
  "alertOn": <Percentage when a alert should be emitted>
}
```

### EQUALS
Checks tat the attribute has a given value. If the value is different a alert will be emitted.

```javascript
"validationConfig": {
  "value": "The value you expect for the given attribute"
}
```

### LOWER
Checks if the attributes value is lower than the given values.

```javascript
"validationConfig": {
  "warnOn": <When the attribute value drops under this value a warning is emitted. Must be a Number>,
  "alertOn": <When the attribute value drops under this value a alert is emitted. Must be a Number>
}
```

### GREATER
Checks tat the attributes value is greater than the given value.

```javascript
"validationConfig": {
  "warnOn": <When the attribute value rises over this value a warning is emitted. Must be a Number>,
  "alertOn": <When the attribute value rises over this value a alert is emitted. Must be a Number>
}```

## Example configuration

```javascript
{
  "servers": [{
    "application": "important-app",
    "environment": "PROD",
    "host": "localhost",
    "port": 10001,
    "username": "jmxhealth",
    "password": "4LNlGIet0QJZ/BJllUefvg==",
    "watchers": [{
      "beanName": "java.lang:type=GarbageCollector,name=PS MarkSweep",
      "checks": [{
        "displayName": "Heap Memory Usage After GC",
        "attributeName": "LastGcInfo",
        "type": "PERCENTAGE",
        "validationConfig": {
          "max": "memoryUsageAfterGc.PS Old Gen.max",
          "actual": "memoryUsageAfterGc.PS Old Gen.used",
          "warnOn": 50,
          "alertOn": 70
        }
      }]
    }, {
      "beanName": "Catalina:j2eeType=WebModule,name=//localhost/examples,J2EEApplication=none,J2EEServer=none",
      "checks": [{
        "displayName": "State",
        "attributeName": "stateName",
        "type": "EQUALS",
        "validationConfig": {
          "value": "STARTED"
        }
      }]
    }, {
      "beanName": "Catalina:type=ThreadPool,name=\"http-apr-9080\"",
      "checks": [{
        "displayName": "HTTP Threads",
        "type": "PERCENTAGE",
        "validationConfig": {
          "max": "maxThreads",
          "actual": "currentThreadsBusy",
          "warnOn": 50,
          "alertOn": 60
        }
      }]
    }]
  }]
}

```

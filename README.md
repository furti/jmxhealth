# jmxhealth
Remote JMX Health Check

## Installation
Download the jmxhealth-server.war and deploy it to a Servlet Container of your choice.
*TODO: Make WAR available*

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

```javascript
{
  "servers": [{
    "application": "important-app",
    "environment": "PROD",
    "host": "localhost",
    "port": 9999,
    "watchers": [{
      "beanName": "java.lang:type=GarbageCollector,name=PS MarkSweep",
      "attributes": {
        "LastGcInfo": {
          "displayName": "Heap Memory Usage After GC",
          "type": "PERCENTAGE",
          "validationConfig": {
            "max": "memoryUsageAfterGc.PS Old Gen.max",
            "actual": "memoryUsageAfterGc.PS Old Gen.used",
            "warnOn": 50,
            "alertOn": 70
          }
        }
      }
    }]
  }]
}
```
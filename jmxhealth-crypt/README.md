#jmxhealth-crypt

A utility to help you with the encryption for jmxhealth-server.

## Generating keys
To generate a key use ```java -jar jmxhealth-crypt.jar genkey```.

This will print a string containing the java system property that needs to be set on the server for encrypting passwords stored in the config file.

## Encrypting passwords
To encrypt a cleartext password use ```java -jar -Dio.github.furti.jmxhealth.crypt-key=<secretkey> jmxhealth-crypt.jar encrypt <cleartext password>```.

This will output the string you can use as password value in the jmxhealth.json config file.

* *<secretkey>* The secret key you got from the genkey command. Should be the same as used on the server.
* *<cleartext password>* The password to encrypt. 
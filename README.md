<p align="center">
    <a href="https://cloud.ibm.com">
        <img src="https://landscape.cncf.io/logos/ibm-cloud-kcsp.svg" height="100" alt="IBM Cloud">
    </a>
</p>

<p align="center">
    <a href="https://cloud.ibm.com">
    <img src="https://img.shields.io/badge/IBM%20Cloud-powered-blue.svg" alt="IBM Cloud">
    </a>
    <a href="https://www.ibm.com/developerworks/learn/java/">
    <img src="https://img.shields.io/badge/platform-java-lightgrey.svg?style=flat" alt="platform">
    </a>
    <img src="https://img.shields.io/badge/license-Apache2-blue.svg?style=flat" alt="Apache 2">
</p>


# MQ Client Java Spring Boot microservice

Sample MQ Client Java Spring Boot application. It contains no default application code, but comes with standard best practices, including a health check and application metric monitoring.
More


## Run the app locally - no security

### Create and start MQ manager

Pull the latest MQ docker image from docker hub:

```
docker pull ibmcom/mq:latest
```

Start MQ docker container
```
docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume qm1data:/mnt/mqm --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```

Access the MQ Console:

```
https://localhost:9443/ibmmq/console
```


Ensure IBM MQ is configured as follows:
* queue manager name is: QM1
* queue name is:  DEV.QUEUE.1
* channel name is: DEV.APP.SVRCONN
* disable queue manager channel authentication CHLAUTH

### (Optional) Start Jaeger for capturing traces

In ./local/jaeger folder:
```
docker-compose up
```

From a browser, access the jaeger UI:
```
http://localhost:16686/search
```

### Start spring boot client app

```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DCONNECTION_NAME='localhost(1414)' -DCHANNEL=DEV.APP.SVRCONN -DQM=QM1 -DQUEUE_NAME='DEV.QUEUE.1' -DUSER=app -DPASSWORD=passw0rd"
```

Or if you are using a ccdt then:
```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DQUEUE_NAME='DEV.QUEUE.1' -DCCDT_URL=https://ccdt-no-security.free.beeceptor.com/ -DUSER=app -DPASSWORD=passw0rd -Dspring.profiles.active=ccdt"
```

### test the app

To send a message to the mq queue:
```
 curl --location --request GET 'http://localhost:8080/api/send-hello-world'
 ```
 You should receive a json response as follows:
 ```
 {"status":"OK","statusMessage":"Successfully sent record to MQ","data":"Hello World!"}
 ```

 To receive a message from the mq queue:
 ```
 curl --location --request GET 'http://localhost:8080/api/recv'
 ```

You should receive a json response:
```
{"status":"OK","statusMessage":"Successfully received record from MQ","data":"Hello World!"}
```

## (NOT COMPLETE YET) Run the app locally - with security

### Create and start LDAP server

In ./local/ldap folder:
```
docker-compose up
```

### Create and start MQ manager

In ./local/mq folder:
```
docker-compose up
```

### (Optional) Start Jaeger for capturing traces

In ./local/jaeger folder:
```
docker-compose up
```
From a browser, access the jaeger UI:
```
http://localhost:16686/search
```

### Start spring boot client app

```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DCONNECTION_NAME='localhost(1414)' -DCHANNEL=IBM.APP.SVRCONN -DQM=QM1 -DUSER=mqapp -DPASSWORD=mqapp -DQUEUE_NAME='IBM.DEMO.Q' -DCLIENT_SSL_KEY_STORE='ibm-client.jks' -DCLIENT_SSL_KEY_STORE_PASSWORD=passw0rd -DCLIENT_SSL_TRUST_STORE='ibm-ca.jks' -DCLIENT_SSL_TRUST_STORE_PASSWORD=passw0rd -Dspring.profiles.active=securemq"
```

Or if you are using a ccdt then:
```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DCCDT_URL=http://ccdt-security-local.free.beeceptor.com/ -DUSER=mqapp -DPASSWORD=mqapp -DQUEUE_NAME='IBM.DEMO.Q' -DCLIENT_SSL_KEY_STORE='ibm-client.jks' -DCLIENT_SSL_KEY_STORE_PASSWORD=passw0rd -DCLIENT_SSL_TRUST_STORE='ibm-ca.jks' -DCLIENT_SSL_TRUST_STORE_PASSWORD=passw0rd -Dspring.profiles.active=securemq,ccdt"
```

### test the app

Same steps as above.


## Configure the app to communicate with the MQ manager

### Spring Boot properties (default)

The app is configured via the application.yml file where properties get injected as follows:
```
ibm:
  mq:
    queue-manager: ${QM:QM1}
    channel: ${CHANNEL:DEV.ADMIN.SVRCONN}
    conn-name: ${CONNECTION_NAME:localhost(1414)}
```

The helm chart creates a configMap and the deployment defines environment variables from the configMap.

Note: ensure helm value `ccdt.enabled` is set to `false`.

### CCDT referencing an HTTP url

In helm `values.yaml`, set the `ccdt.enabled` value to `true` and provide a value for `ccdt.CCDT_URL` prefixed with `http://` or `https://`.

Note: ensure `ccdt.volumeName` is blank as we do not want to create a volume for this scenario.

Example config:
```
ccdt:
  enabled: true
  CCDT_URL: https://ccdt-no-security-dev.free.beeceptor.com/
  volumeName:
```

### CCDT referencing a file mounted from configMap

In helm `values.yaml`, set the `ccdt.enabled` value to `true` and provide a value for `ccdt.CCDT_URL` prefixed with `file:///`.
Also, ensure the `ccdt.volumeName` and `ccdt.volumeMountPath` have values.

Example config:
```
ccdt:
  enabled: true
  CCDT_URL: file:///ccdt/ccdt.json
  volumeName: ccdt
  volumeMountPath: "/ccdt"
  configMapName: mq-spring-app-ccdt
```

The helm chart will create the configMap with a name of `mq-spring-app-ccdt`.  See: `./chart/base/templates/configmap-ccdt.yaml`.
You can modify the configMap located at `./chart/base/templates/configmap-ccdt.yaml` to set the ccdt values for your environment.


## Sealed Secrets

The app gets deployed using a helm chart which is included in this repo.
The app depends on a secret called `mq-spring-app` that contains two key value pairs
called `USER` and `PASSWORD` which contain the info to authenticate the client app with the MQ server.
We are using Sealed Secrets to create the secret (https://github.com/bitnami-labs/sealed-secrets).
The way sealed secrets work is, you create the sealed secret resource in the target kube/openshift namespace
and the operator will generate the actual secret.

Create a kube secret file with the unencrypted values as follows:

```
oc create secret generic mq-spring-app --from-literal=USER=<user-name> --from-literal=PASSWORD=<password> --dry-run=true -o yaml > mq-spring-app.yaml
```

Then generate the encrypted values using the kubeseal cli as follows:

```
kubeseal --scope cluster-wide --controller-name=sealedsecretcontroller-sealed-secrets --controller-namespace=sealed-secrets -o yaml < mq-spring-app.yaml > mq-spring-app-enc.yaml
```
The file `mq-spring-app-enc.yaml`  will contain the encrypted values to modify  USER and PASSWORD in  `chart/base/values.yaml`.

In this particular case, the sealed secret created has a cluster-wide scope.
To further lock down the setup and enhance security, you can create the sealed secret with a namespace scope.
See kubeseal docs to better understand this.




## License

This sample application is licensed under the Apache License, Version 2. Separate third-party code objects invoked within this code pattern are licensed by their respective providers pursuant to their own separate licenses. Contributions are subject to the [Developer Certificate of Origin, Version 1.1](https://developercertificate.org/) and the [Apache License, Version 2](https://www.apache.org/licenses/LICENSE-2.0.txt).
.
[Apache License FAQ](https://www.apache.org/foundation/license-faq.html#WhatDoesItMEAN)

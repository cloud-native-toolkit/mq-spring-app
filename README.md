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

Capabilities are provided through dependencies in the `pom.xml` file. The ports are set to the defaults of `8080` for http and `8443` for https and are exposed to the CLI in the `cli-config.yml` file. The ports are set in the `pom.xml` file and exposed to the CLI in the `cli-config.yml` file.

## Steps

You can [deploy this application to IBM Cloud](https://cloud.ibm.com/developer/appservice/create-app?starterKit=1298bc4e-4764-390b-a9eb-e4dcf3cc03ad) or [build it locally](#building-locally) by cloning this repo first. Once your app is live, you can access the `/health` endpoint to build out your cloud native application.

### Deploying 

After you have created a new git repo from this git template, remember to rename the project.
Edit `settings.gradle` and change the `rootProject.name` from the default name to the name you used to create the template.

Make sure you are logged into the IBM Cloud using the IBM Cloud CLI and have access 
to you development cluster. If you are using OpenShift make sure you have logged into OpenShift CLI on the command line.

```$bash
npm i -g @garage-catalyst/ibm-garage-cloud-cli
```

Use the IBM Garage for Cloud CLI to register the GIT Repo with Jenkins 
```$bash
igc pipeline -n dev
```

### Building Locally

To get started building this application locally, you can either run the application natively or use the [IBM Cloud Developer Tools](https://cloud.ibm.com/docs/cli?topic=cloud-cli-getting-started) for containerization and easy deployment to IBM Cloud.

#### Native Application Development

* [Maven](https://maven.apache.org/install.html)
* Java 11: Any compliant JVM should work.
  * [Java 11 JDK from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
    or [Download a Liberty server package](https://developer.ibm.com/assets/wasdev/#filter/assetTypeFilters=PRODUCT)
    that contains the IBM JDK (Windows, Linux)
    
To build and run an application:
1. ./mvnw package
2. ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DCONNECTION_NAME='localhost(1414)' -DCHANNEL=IBM.APP.SVRCONN -DQM=QM1 -DUSER=mqapp -DPASSWORD=mqapp -DQUEUE_NAME='IBM.DEMO.Q' -DCLIENT_SSL_KEY_STORE='ibm-client.jks' -DCLIENT_SSL_KEY_STORE_PASSWORD=passw0rd -DCLIENT_SSL_TRUST_STORE='ibm-ca.jks' -DCLIENT_SSL_TRUST_STORE_PASSWORD=passw0rd"


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



## Run MQ docker image locally



Start the MQ docker image:

```
docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume qm1data:/mnt/mqm --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```

MQ Console:

```
https://localhost:9443/ibmmq/console
```


## More Details

For more details on how to use this Starter Kit Template please review the [IBM Garage for Cloud Developer Tools Developer Guide](https://ibm-garage-cloud.github.io/ibm-garage-developer-guide/)

## Next Steps
* Learn more about augmenting your Java applications on IBM Cloud with the [Java Programming Guide](https://cloud.ibm.com/docs/java?topic=java-getting-started).
* Explore other [sample applications](https://cloud.ibm.com/developer/appservice/starter-kits) on IBM Cloud.


## Local setup - no security

### MQ 

Pull the latest MQ docker image from docker hub:

```
docker pull ibmcom/mq:latest
```

Start MQ docker container
```
docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume qm1data:/mnt/mqm --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```

Additional manual steps:
* create the queue DEV.QUEUE.q 
* creatte channel DEV.APP.SVRCON 
* disable queue manager channel authentication CHLAUTH

### client app

To start the spring boot client app
```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DCONNECTION_NAME='localhost(1414)' -DCHANNEL=DEV.APP.SVRCONN -DQM=QM1 -DQUEUE_NAME='DEV.QUEUE.1' -DUSER=app -DPASSWORD=passw0rd
```

## Local setup with security

## License

This sample application is licensed under the Apache License, Version 2. Separate third-party code objects invoked within this code pattern are licensed by their respective providers pursuant to their own separate licenses. Contributions are subject to the [Developer Certificate of Origin, Version 1.1](https://developercertificate.org/) and the [Apache License, Version 2](https://www.apache.org/licenses/LICENSE-2.0.txt).

[Apache License FAQ](https://www.apache.org/foundation/license-faq.html#WhatDoesItMEAN)

### Periodically update from the template

Finally, the template components can be periodically updated by running the following:

```bash
./update-template.sh
```

webhook test

# File Based Spring Cloud Config

This SDK artifact provides Spring Cloud configuration functionality driven by local properties files instead of a remote configuration server. A project that pulls in this artifact as a dependency may use all standard Spring Cloud functionality in its implementation. To switch between using a local file based source and the standard Spring Cloud Service, a project only needs to switch this dependency with the standard Spring Cloud Artifacts and modify the bootstrap configuration appropriately.

# Adding the Functionality to a Project
To add this feature to a project, add this dependency to the pom.xml
```xml
<dependency>
    <groupId>com.att.ajsc</groupId>
    <artifactId>sdk-java-spring-cloud-file</artifactId>
    <version>0.0.8</version>
</dependency>
```
Next, configure the feature by modifying the properties in bootstrap.properties located in src/main/resources
```sh
# Activate Spring Cloud Config Server functionality, these should remain false while using the file based implementation to prevent conflicts with properties pulled from a remote server.
spring.cloud.config.discovery.enabled=false
spring.cloud.config.enabled=false

# Allow Spring Cloud properties configuration to override properties set from System Properties and ENV Properties
spring.cloud.config.overrideSystemProperties=true

# Path to the file containing the dynamic properties
com.att.ajsc.dynamic.properties.path=/etc/config/dynamic/dynamic.properties

# Path to the file containing the dynamic logger levels
com.att.ajsc.dynamic.logging.path=/etc/config/logging/logging.properties

# File watcher polling frequency in milliseconds
com.att.ajsc.dynamic.watcher.delay=5000
```
A few notes on the above settings properties:
  - Leave the first two properties false. Conflicts in property order between Spring Cloud Config Server and properties obtained from local files will otherwise result.
  - The overrideSystemProperties property indicates whether properties obtained from this feature should override equivalent properties values set in System properties or the container ENV.
  - The logging and dynamic properties files should be the only files in their respective directories.

# Using the Functionality
Including and configuring this dependency gives access to the Spring Cloud configuration functionality. Specifically, the following features are triggered when a change to the dynamic logging or properties files is detected.
  - Dynamically set the logger levels for any properties specified as logging.level.*
  - Re-bind any @ConfigurationProperties beans in the context to automatically pick up any properties changes
  - Invalidate the cache of any beans annotated with @RefreshScope. This will force the bean to be re-initialized on the next method call.

For more details, refer to the [Spring Cloud Environment Changes] documentation.

# Use in Docker Containers Managed by Kubernetes
Follow these steps in order to use dynamic configuration and log level setting with a microservice running in a Docker container managed by Kubernetes.

Create two yaml files defining two Kubernetes ConfigMaps; one to contain any dynamic properties and one to contain logger levels to dynamically modify. More indepth information can be found here on [using ConfigMap] in Kubernetes.
```sh
$ cat myservice-dynamic-properties.yaml
apiVersion: v1
data:
  dynamic.properties: |
    dynamic.sample=SOME_VALUE
kind: ConfigMap
metadata:
  name: myservice-dynamic-properties
  namespace: default

$ cat myservice-logging-properties.yaml
apiVersion: v1
data:
  logging.properties: |
    logging.level.com.att.ajsc.mynamespace.myservice=error
kind: ConfigMap
metadata:
  name: myservice-logging-properties
  namespace: default
```
Create two corresponding ConfigMaps in the Kubernetes cluster.
```sh
$ kubectl create configmap myservice-dynamic-properties
configmap "myservice-dynamic-properties" created

$ kubectl create configmap myservice-logging-properties
configmap "myservice-logging-properties" created
```
Populate the newly created, empty ConfigMaps in the Kubernetes cluster by replacing their contents with the ConfigMap yaml files.
```sh
$ kubectl replace -f myservice-dynamic-properties.yaml
configmap "myservice-dynamic-properties" replaced

$ kubectl replace -f myservice-logging-properties.yaml
configmap "myservice-logging-properties" replaced
```
Now, update the replication controller yaml file to include volume mounts for the ConfigMap files. Each ConfigMap should have a separate volume mount and the resulting path and file name combination should match the com.att.ajsc.dynamic.properties.path and com.att.ajsc.logging.properties.path properties in bootstrap.properties.

- Create volume mounts at spec.template.spec.containers.volumeMounts
```sh
...
    spec:
      containers:
      - name: myservice
        volumeMounts:
        - name: myservice-logging-volume
          mountPath: /etc/config/logging
        - name: myservice-properties-volume
          mountPath: /etc/config/dynamic
```
- Create volumes and populate contents at spec.template.spec.volumes
```sh
...
    spec:
      containers:
      - name: myservice
      ...
      volumes:
      - name: myservice-logging-volume
        configMap:
          name: myservice-logging-properties
      - name: myservice-properties-volume
        configMap:
          name: myservice-dynamic-properties
```

When a pod for a microservice is deployed by Kubernetes using the updated replication controller, it will internally contain two dynamic volumes. From this example, the first volume will be mounted at /etc/config/logging and contain the file logging.properties, which has the contents as defined by myservice-logging-properties.yaml. The second volume will be mounted at /etc/config/dynamic and contain the file dynamic.properties, which has the contents as defined by myservice-dynamic-properties.yaml.

The microservice can access any properties from dynamic.properties in the same way it would properties defined in application.properties. The properties are compliant with the [Spring Externalized Configuration] Environment. Where they are placed in the order depends on the spring.cloud.config.overrideSystemProperties property setting from bootstrap.properties. If it is set to true, then the properties are at the top of the order, and override everything else. If it is false, they are in the middle of the order, and override application.properties, but are in turn overridden by System and ENV properties.

# Dynamically Updating Properties and Log Levels
To dynamically update the properties or logger levels for running containers, simply update the contents of the appropriate .yaml definition file.
```sh
$ cat myservice-dynamic-properties.yaml
apiVersion: v1
data:
  dynamic.properties: |
    dynamic.sample=SOME_NEW_VALUE_AS_OVERRIDE
kind: ConfigMap
metadata:
  name: myservice-dynamic-properties
  namespace: default

$ cat myservice-logging-properties.yaml
apiVersion: v1
data:
  logging.properties: |
    logging.level.com.att.ajsc.mynamespace.myservice=debug
kind: ConfigMap
metadata:
  name: myservice-logging-properties
  namespace: default
```
Then replace the ConfigMaps in the Kubernetes cluster.
```sh
$ kubectl replace -f myservice-dynamic-properties.yaml
configmap "myservice-dynamic-properties" replaced

$ kubectl replace -f myservice-logging-properties.yaml
configmap "myservice-logging-properties" replaced
```

The Kubernetes master will detect the ConfigMap changes and use the downward API to push the updates to pods with volume mounts populated by those ConfigMaps. This dynamically updates the files on the mounts (by changing a symbolic link). The microservice detects this file update performs 3 actions in response.
- Change the logger levels of the microservice to match the properties from logging.properties
- Re-bind any @ConfigurationProperties beans in the context so that values from dynamic.properties are automatically picked up.
- Force any beans annoted with @RefreshScope to re-initialize the next time the bean in invoked. This will allow for new dynamic property values to be used when the bean is re-initialized.

# Dynamic Logger Level Considerations
The SDK implements loggers using the Logback library. When log levels are changed, there is an expectation that inheritance will be leveraged so that individual class loggers do not have to be changed individually. Changing the log level of com.att.ajsc will change the log level of all classes in all packages that are children of that package. This **ONLY** works if the inherited logger has not been explicitly set. So, if com.att.ajsc.myservice is set to error and com.att.ajsc is later changed to debug, com.att.ajsc.myservice and it's children will remain at the error level.

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)
   [Spring Externalized Configuration]: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
   [using ConfigMap]: http://kubernetes.io/docs/user-guide/configmap/
   [Spring Cloud Environment Changes]: http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_environment_changes
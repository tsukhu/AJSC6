http://jircflpr-labc01.ds.dtvops.net:8090/display/TSD/SDK

# What is the SDK? What is AJSC?
Goal: Enable our developers, QA, and Operations to succeed in developing and maintaining an API for the clients they serve.

The **SDK** is the provided starting point for developing within our *paved road*. We have created an environment to deliver a feature from code to production with minimal manual steps and setup required by the developer. The *paved road* is an integrated technology stack to be able to develop, test, operate, and run code out to production that's provided and supported by the platform team so you can get to work on implementing features instead of fighting the technology.

**AJSC** is the *AT&T Java Service Container* which means it's the project under AT&T to write development kits for use in the approved paved road environments. This **SDK** is the java version of **AJSC**. 

# Service Models

There are two types of services generally needed and supported by this SDK and they are a **Core** service or a **Composite** Service.

The Composite service depends on other services and though them produces a higher level function. This service requires no data source.

![alt text](resources/composite_service.png
"Composite Service")

The Core service does not depend on other services and instead provides and independent function. It has its own data source that is not shared with any other service.

![alt text](resources/core_service.png
"Core Service")

# Adding to your project
In order to add this feature to your project you need to have this parent tag present in your pom.
```xml
<parent>
	<groupId>com.att.ajsc</groupId>
	<artifactId>sdk-java-starter-parent</artifactId>
	<version>6.1</version>
</parent>
```
# SDK Feature Support
|                      | Version               | Status 
| -------------------- | :-------------------: | :----------------------:
| GET /                | all                   | Use Generic Rest Service
| POST /               | all                   | Use Generic Rest Service
| PUT /                | all                   | Use Generic Rest Service
| DELETE /             | all                   | Use Generic Rest Service
| GET /status          | < com.att.ajsc:\*:6.0 | **Removed**
| Standardized Logging | > com.att.ajsc:\*:6.0 | 
| AJSC Branding        | > com.att.ajsc:\*:6.0 | 
| Modular Components   | > com.att.ajsc:\*:6.1 | 
| Standardized Naming  | > com.att.ajsc:\*:6.2 | 

# SDK Version Support
| Version                 | Supported | End of Support 
| ----------------------- | :-------: | :------------:
| com.direct.\*:\*:0.1.x  | No        | 2016-06-22
| com.aeg.common:\*:0.2.x | No        | 2016-06-22
| com.att.ajsc:\*:6.0     | Yes       | 2016-08-31
| com.att.ajsc:\*:6.1     | Yes       | 

# Feature Breakdown
| Feature                       | Supported | Notes
| -----------------------       | :-------: | :------------:
| Generation                    | Yes       | Use API Builder
|     * API Builder Integration | Yes       |
|     * Working Service         | Yes       | Maven Archetypes
|     * Git Repository          | Yes       | 
|     * Jenkins Job             | Yes       | 
|     * Build tool              | Yes       |
|     * Documentation           | Partial   | Interface documentation, and Readme, Yes but other documentation No. 
| Default Interface             | Yes       | [sdk-java-rest]()
|     * Transaction ID          | Yes       |
|     * Pagination              | Yes       |
|     * Query by Keyword         | Yes       |
|     * Advanced Filter Query   | Partial   | Query sytax supported except for 'or'ing is not working until we fix the generic adapter interface
|     * ISO8601 Dating          | Yes       |
| Messaging                     | Yes       | [sdk-java-kafka]()
| Adapters                      | Yes       | sdk-java-*
|     * Business Controller     | Yes       | [sdk-java-springservice]()
|     * RESTful Interface       | Yes       | [sdk-java-rest]()
|     * RDBMS                   | Yes       | [sdk-java-jpa]()
|     * Couchbase               | Yes       | [sdk-java-couchbase]()
|     * Kafka                   | Yes       | [sdk-java-kafka]()
|     * Errors & Exceptions     | Yes       | [sdk-java-error]()
|     * Logging                 | Yes       | Provided by Spring Boot and runtime. Log to SLF4J
|     * Configuration           | Yes       | Provided by Spring Boot and runtime. Extras included in [sdk-java-configuration]()
|     * Utilities               | Yes       | Date, Validation, Environment, etc. [sdk-java-utility]()
| Versioning                    | Yes       | 
| Errors                        | Yes       | 
| Configuration                 | Yes       | 
| Security                      | Partial   | SDK is under CSO review
| Logging                       | Yes       | 
|     * Transaction ID          | Yes       | 
|     * Request Time            | Yes       | 
|     * Response Time           | Yes       | 
|     * Duration                | Yes       | 
|     * Service Name            | Yes       | 
|     * Container ID            | Yes       | 
| Test Suite                    | Yes       | JUnit & LISA
| Documentation                 | Partial   | Includes Interface but we still need to add Design documentation
| Static Code Review            | Yes       | SonarQube
| Deployment                    | Yes       | Jenkins uses the platform deployment service
| Registration                  | No        | Apigee registration is being added but the registration path is not finalized.
| Docker Image                  | Yes       |
| Data Source                   | Partial   | Adapters are available but data source preparation is not. 
|     * Provisioning            | No        | This path is not yet finalized. Work is underway on the runtime environment.
|     * Setup                   | No        | This path is not yet discussed and will depend on the provisioning strategy. 
| Continuous Integration        | Partial   | Supported but test integration path not finalized
|     * Smoke Tests             | No        | This path is not yet discussed and will depend on the test integration path.
|     * Acceptance Tests        | No        | This path is not yet discussed and will depend on the test integration path.
| Continuous Deployment         | Partial   | Supported but deployment path not finalized
| Multitenancy                  | No        | A separate instance is spun up but path is not finalized
| Development add-ons           | Yes       |
|     * Live QC & testing       | No        |
|     * Profiles support        | Yes       | Provided by Spring Boot and runtime.
|     * Automated coding styling| No        | 

# SDK Dependencies
|                        | sdk-java-starter | sdk-java-configuration | sdk-java-error | sdk-java-utility  | sdk-java-interceptor | sdk-java-kafka | sdk-java-dao | sdk-java-couchbase | sdk-java-jpa | sdk-java-springservice | sdk-java-rest
| ---------------------- | :--------------: | :--------------------: | :------------: | :---------------: | :------------------: | :------------: | :----------: | :----------------: | :----------: | :--------------------: | :-----------:
| sdk-java-starter       |                  |                        |                |                   |                      |                |              |                    |              |                        |              
| sdk-java-configuration |  x               |                        |                |                   |                      |                |              |                    |              |                        |              
| sdk-java-error         |  x               |                        |                |                   |                      |                |              |                    |              |                        |              
| sdk-java-utility       |  x               |                        |  x             |                   |  x                   |                |  x           |                    |              |                        |              
| sdk-java-interceptor   |  x               |                        |                |                   |                      |                |              |                    |              |                        |              
| sdk-java-kafka         |  x               |                        |                |                   |                      |                |              |                    |              |                        |              
| sdk-java-dao           |  x               |                        |                |                   |                      |                |              |                    |              |                        |              
| sdk-java-couchbase     |  x               |                        |                |                   |                      |                |  x           |                    |              |                        |              
| sdk-java-jpa           |  x               |                        |                |                   |                      |                |  x           |                    |              | x                      |              
| sdk-java-springservice |  x               |                        |                |                   |                      |                |  x           |                    |              |                        |              
| sdk-java-rest          |  x               |                        |  x             |  x                |  x                   |                |  x           |                    |              | x                      |              

# FAQs

1. How can I get started the fastest?

Use the API Builder user interface to select the features you want and it will generate a working service to get you started.

2. Can I use my own technology component that I think is better?

Yes, **but** there's a catch. If you do so then you're not on our *paved road* so the platform team is unable to support you. We do encourage going off the *paved road* if you have a solution that is possibly better. Then we can work together to pull it onto the *paved road* for everyone to take advantage of. Feel free to talk about it in the community of practice discussion for head end developers.

3. So this thing doesn't work as I expected. Who should I talk to?

The platform team has a 'Platform Q&A' chat room on EG HipChat. Come by and ask around. We'll find someone to answer your question or concern.

4. Why are you ending support for an SDK? I have services running and you're making me upgrade!

Is your service running without issues? Leave it! If it's working fine then no need to upgrade. Once you have to make a change then please check the latest SDK version and upgrade as part of that change.

Dependency resolution is always a balancing act. Unfortunately, we cannot support versions forever. Since we're rapidly making releases to get features out, we need to keep a short timeline or we'll be unable to meet the demand. Additionally, we want you to upgrade sooner than later so you, and others, can take advantage of all the new features added.

# Upgrade Instructions

## Diff the latest example

Apart from reading step by step upgrade instructions, you could diff the example services such as [sdk-java-base-example](). This way you can compare to a working service and know you're upgrading correctly and not missing anything different.

## Diff a new version in the sandbox

Another option, you could create your service again using API Builder in the sandbox. Then diff the new service to find what changes are needed if you were to create your service from scratch today. This way you can compare to a working service that matches your own, minus the additions you've made, and know you're upgrading correctly and not missing anything different.

## 0.x to 6.1

1. Update the parent pom using the dependency tag above under 'Adding to your project'.

2. Add the modules you need to use such as [sdk-java-rest](). Remember, the SDK is modular now so if you don't need the component then don't add it.

3. Update your pom to match the [base example pom]() so you're using the latest plugins and environment setup.

4. Update your import statements. The SDK package names are now under com.att.ajsc.\* and swagger is now under io.swagger.\*.

5. Update your properties.

	a. The context root is no longer needed so remove it and re-point tests and documents that are hard coded.
 
	b. Any sdk properties were moved from com.directv.\*, com.aeg.\*, or aeis.\*, to ajsc.\*.

6. Update your docker source to match the [base example docker source]() so you're using the latest additions.

7. Update your [swagger viewer]() so it can read the new Open API specification.

8. Update your README.md to match the latest [README template]().

## 6.0 to 6.1+

1. Update the parent pom using the dependency tag above under 'Adding to your project'.

2. Add the modules you need to use such as [sdk-java-rest](). Remember, the SDK is modular now so if you don't need the component then don't add it.

3. Update your pom to match the [base example pom]() so you're using the latest plugins and environment setup.

4. Update your import statements. Some classes were moved to their more appropriate package.

5. Update your docker source to match the [base example docker source]() so you're using the latest additions.

6. Update your README.md to match the latest [README template]().

@echo off
call mvn -f sdk-java-common-logging/pom.xml clean install -DskipTests
call mvn -f sdk-java-starter/pom.xml clean install -DskipTests
call mvn -f sdk-java-error/pom.xml clean install -DskipTests
call mvn -f sdk-java-dao/pom.xml clean install -DskipTests
call mvn -f sdk-java-interceptor/pom.xml clean install -DskipTests
call mvn -f sdk-java-utility/pom.xml clean install -DskipTests
call mvn -f sdk-java-configuration/pom.xml clean install -DskipTests
call mvn -f sdk-java-si-interceptor/pom.xml clean install -DskipTests
call mvn -f sdk-java-camel-interceptor/pom.xml clean install -DskipTests
call mvn -f sdk-java-restlet-interceptor/pom.xml clean install -DskipTests
call mvn -f sdk-java-introscope-interceptors/pom.xml clean install -DskipTests
call mvn -f sdk-java-logging-interceptor/pom.xml clean install -DskipTests
call mvn -f sdk-java-springservice/pom.xml clean install -DskipTests
call mvn -f sdk-java-camel-rest/pom.xml clean install -DskipTests
call mvn -f sdk-java-spring-cloud-file/pom.xml clean install -DskipTests
call mvn -f sdk-java-common-interceptors/pom.xml clean install -DskipTests
call mvn -f sdk-java-restlet-common/pom.xml clean install -DskipTests
call mvn -f sdk-java-aaf-interceptors/pom.xml clean install -DskipTests
call mvn -f sdk-java-rest/pom.xml clean install -DskipTests
call mvn -f sdk-java-camel-archetype/pom.xml clean install -DskipTests
call mvn -f sdk-java-jersey-archetype/pom.xml clean install -DskipTests
call mvn -f sdk-java-restlet-archetype/pom.xml clean install -DskipTests


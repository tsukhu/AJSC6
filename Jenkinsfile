node {
    // Get the maven tool.
    // ** NOTE: This 'M3' maven tool must be configured
    // **       in the Jenkins global configuration.
    def mvnHome = tool 'M3'
    sh "echo ${mvnHome}"
    
    
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    // Get some code from a GitHub repository
    checkout scm    
   
    // Mark the code build 'stage'....
    stage 'Build dmaap-framework'
    // Run the maven build
    //sh for unix bat for windows
	
    //sh "${mvnHome}/bin/mvn -f sdk-java-common-logging/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-starter/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-error/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-dao/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-interceptor/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-utility/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-configuration/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-si-interceptor/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-camel-interceptor/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-restlet-interceptor/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-introscope-interceptors/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-logging-interceptor/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-springservice/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-camel-rest/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-spring-cloud-file/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-common-interceptors/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-restlet-common/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-aaf-interceptors/pom.xml clean deploy"
    //sh "${mvnHome}/bin/mvn -f sdk-java-rest/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f sdk-java-camel-archetype/pom.xml clean deploy"
  
}

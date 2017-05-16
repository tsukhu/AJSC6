
# AAF interceptors

## Description
AAF is created in order to make AAF Authorization pluggable into any application created with AJSC archetype. For more information, please visit To learn more, see <a href="https://wiki.web.att.com/display/ajsc/Authorization+Using+AAF">the wiki page</a>.

## Usage
To use this interceptor in a service developer has to add the following dependency to the service pom and by injecting the bean in spring boot application.


    <dependency>
		<groupId>com.att.ajsc</groupId>
		<artifactId>sdk-java-aaf-interceptors</artifactId>
		<version>6.0.0.0-oss</version>
	</dependency>
    

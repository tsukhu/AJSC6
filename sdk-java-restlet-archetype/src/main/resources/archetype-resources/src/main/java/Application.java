/*******************************************************************************
 *   BSD License
 *    
 *   Copyright (c) 2017, AT&T Intellectual Property.  All other rights reserved.
 *    
 *   Redistribution and use in source and binary forms, with or without modification, are permitted
 *   provided that the following conditions are met:
 *    
 *   1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. All advertising materials mentioning features or use of this software must display the
 *      following acknowledgement:  This product includes software developed by the AT&T.
 *   4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *      promote products derived from this software without specific prior written permission.
 *    
 *   THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 *   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *   SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package};

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.Servlet;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.restlet.ext.spring.SpringServerServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.att.ajsc.common.utility.SystemPropertiesLoader;

@SpringBootApplication
@ComponentScan(basePackages = "com")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class Application extends SpringBootServletInitializer {

	private static final String RESTLET_SERVLET_NAME = "RestletServlet";
	private static final String RESTLET_INIT_PARAM_NAME = "org.restlet.component";
	private static final String RESTLET_INIT_PARAM_VALUE = "restletComponent";
	private static final String RESTLET_URL_MAPPING = "/*";

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SystemPropertiesLoader.addSystemProperties(); 
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ServletRegistrationBean RestletServletRegistrationBean() {
		
		ServletRegistrationBean registration = new ServletRegistrationBean();
		registration.setName(RESTLET_SERVLET_NAME);
		registration.setServlet((Servlet) new SpringServerServlet());
		registration.addInitParameter(RESTLET_INIT_PARAM_NAME, RESTLET_INIT_PARAM_VALUE);
		Collection<String> urlMappings = new ArrayList<String>();
		urlMappings.add(RESTLET_URL_MAPPING);
		registration.setUrlMappings(urlMappings);
		return registration;
	}

	@Bean
	public Client restClient() {
		return ClientBuilder.newClient();
	}

}
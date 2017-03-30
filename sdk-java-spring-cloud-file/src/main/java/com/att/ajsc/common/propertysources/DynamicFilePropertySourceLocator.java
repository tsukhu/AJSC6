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
package com.att.ajsc.common.propertysources;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * @author author
 *
 */
@Order(1)
@Configuration
public class DynamicFilePropertySourceLocator implements PropertySourceLocator {

	@Value("${com.att.ajsc.dynamic.properties.path:}")
	private String properties_path;

	final static Logger logger = LoggerFactory.getLogger(DynamicFilePropertySourceLocator.class);

	// @Override
	public PropertySource<?> locate(Environment environment) {

		logger.debug("Instantiating dynamic file property sources");
		CompositePropertySource compPropertySource = new CompositePropertySource("dynamic-composite");
		if (!properties_path.trim().isEmpty()) {
			String[] paths = properties_path.trim().split(",");

			for (int i = 0; i < paths.length; i++) {
				String path = null;
				if (Paths.get(paths[i]).toFile().getAbsoluteFile().exists()) {
					path = Paths.get(paths[i]).toFile().getAbsolutePath();
				} else if (Paths.get(paths[i]).toFile().exists()) {
					path = Paths.get(paths[i]).toFile().getPath();
				}

				else if (Paths.get(System.getProperty("user.dir") + paths[i]).toFile().exists()) {
					path = System.getProperty("user.dir") + Paths.get(paths[i]).toFile().getPath();
				} else {
					logger.error("Can't find the specified path: " + paths[i]);
				}

				if (path != null) {
					ResourcePropertySource propertySource = null;
					try {
						propertySource = new ResourcePropertySource("file:" + path);
					} catch (Exception e) {
						logger.error("Error reading the property file:" + paths[i]);
					}
					if (propertySource != null) {
						compPropertySource.addPropertySource(propertySource);
					}
				}
			}
		} else {
			logger.debug("dynamic properties not defined");
		}

		return compPropertySource;
	}

}

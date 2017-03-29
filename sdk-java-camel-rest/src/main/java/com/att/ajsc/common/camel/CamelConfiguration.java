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
package com.att.ajsc.common.camel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.att.ajsc.common.AjscProvider;
import com.att.ajsc.common.AjscService;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Configuration
public class CamelConfiguration {

	private static EELFLogger log = AjscEelfManager.getInstance().getLogger(CamelConfiguration.class);

	@Autowired
	private ApplicationContext context;
	@Autowired
	private CamelContext camelContext;
	@Value("${camel.defaultthreadpool.poolsize}")
	private Integer poolSize;
	@Value("${camel.defaultthreadpool.maxpoolsize}")
	private Integer maxPoolSize;
	@Value("${camel.defaultthreadpool.maxqueuesize}")
	private Integer maxQueueSize;
	@Value("${camel.defaultthreadpool.keepaliveTime}")
	private Long keepAliveTime;
	@Value("${camel.defaultthreadpool.rejectpolicy}")
	private String rejectPolicy;

	@PostConstruct
	public void jaxrsServices() {

		Map<String, ArrayList<Object>> services = new HashMap<String, ArrayList<Object>>();

		Map<String, Object> beans = context.getBeansWithAnnotation(AjscService.class);
		Collection<String> collection = beans.keySet();

		for (Iterator<String> iterator = collection.iterator(); iterator.hasNext();) {

			String key = iterator.next();
			Object object = context.getBean(key);
			String className = getActualClass(object);
			AjscService service = null;
			try {
				service = Class.forName(className).getAnnotation(AjscService.class);
			} catch (ClassNotFoundException e) {
				log.error("Error while reading the annotation for the class: " + className);
			}

			if (service != null) {

				if (services.containsKey(service.name())) {
					List<Object> restServices = (ArrayList<Object>) services.get(service.name());
					restServices.add(object);
					services.put(service.name(), (ArrayList<Object>) restServices);
				} else {
					List<Object> restServices = new ArrayList<Object>();
					restServices.add(object);
					services.put(service.name(), (ArrayList<Object>) restServices);
				}

				if (!services.isEmpty()) {
					Collection<String> serviceNames = services.keySet();
					for (Iterator<String> iterator2 = serviceNames.iterator(); iterator2.hasNext();) {
						String serviceName = (String) iterator2.next();
						((ArrayList<Object>) (context.getBean(serviceName))).addAll(services.get(serviceName));
					}
				}

			}
		}

	}

	@PostConstruct
	public void jaxrsProviders() {

		Map<String, ArrayList<Object>> providers = new HashMap<String, ArrayList<Object>>();

		Map<String, Object> beans = context.getBeansWithAnnotation(AjscProvider.class);
		Collection<String> collection = beans.keySet();

		for (Iterator<String> iterator = collection.iterator(); iterator.hasNext();) {

			String key = iterator.next();
			Object object = context.getBean(key);
			String className = getActualClass(object);
			AjscProvider provider = null;
			try {
				provider = Class.forName(className).getAnnotation(AjscProvider.class);
			} catch (ClassNotFoundException e) {
				log.error("Error while reading the annotation for the class: " + className);
			}

			if (provider != null) {

				if (providers.containsKey(provider.name())) {
					List<Object> restServices = (ArrayList<Object>) providers.get(provider.name());
					restServices.add(object);
					providers.put(provider.name(), (ArrayList<Object>) restServices);
				} else {
					List<Object> restProviders = new ArrayList<Object>();
					restProviders.add(object);
					providers.put(provider.name(), (ArrayList<Object>) restProviders);
				}

			}
		}
		if (!providers.isEmpty()) {

			Collection<String> providerNames = providers.keySet();
			for (Iterator<String> iterator2 = providerNames.iterator(); iterator2.hasNext();) {
				String providerName = (String) iterator2.next();
				((ArrayList<Object>) (context.getBean(providerName))).addAll(providers.get(providerName));
			}

		}

	}

	@PostConstruct
	public void updateDefaultThreadPool() {
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxPoolSize(maxPoolSize);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxQueueSize(maxQueueSize);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setPoolSize(poolSize);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setKeepAliveTime(keepAliveTime);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile()
				.setRejectedPolicy(ThreadPoolRejectedPolicy.valueOf(rejectPolicy));

	}

	private String getActualClass(Object obj) {

		String className = null;
		if (obj.toString().indexOf("$$") > 0) {
			className = obj.toString().substring(0, obj.toString().indexOf("$$"));
		} else {
			className = obj.toString().substring(0, obj.toString().indexOf("@"));
		}
		return className;

	}

}

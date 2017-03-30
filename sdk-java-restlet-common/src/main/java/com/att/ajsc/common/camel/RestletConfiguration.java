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

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.component.restlet.RestletComponent;
import org.restlet.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestletConfiguration {

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

	@Value("${restlet.component.controller.daemon}")
	private Boolean controller_daemon;
	@Value("${restlet.component.controller.sleep.time.ms}")
	private Integer controller_sleep_time_ms;
	@Value("${restlet.component.inbound.buffer.size}")
	private Integer inbound_buffer_size;
	@Value("${restlet.component.min.threads}")
	private Integer min_threads;
	@Value("${restlet.component.max.threads}")
	private Integer max_threads;
	@Value("${restlet.component.low.threads}")
	private Integer low_threads;
	@Value("${restlet.component.max.queued}")
	private Integer max_queued;
	@Value("${restlet.component.max.connections.per.host}")
	private Integer max_connections_per_host;
	@Value("${restlet.component.max.total.connections}")
	private Integer max_total_connections;
	@Value("${restlet.component.outbound.buffer.size}")
	private Integer outbound_buffer_size;
	@Value("${restlet.component.persisting.connections}")
	private Boolean persisting_connections;
	@Value("${restlet.component.pipelining.connections}")
	private Boolean pipelining_connections;
	@Value("${restlet.component.thread.max.idle.time.ms}")
	private Integer thread_max_idle_time_ms;
	@Value("${restlet.component.use.forwarded.header}")
	private Boolean use_forwarded_header;
	@Value("${restlet.component.reuse.address}")
	private Boolean reuse_address;
	@Value("${routeMatchingMode:2}")
	private int matchingMode;

	@PostConstruct
	public void updateDefaultThreadPool() {
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxPoolSize(maxPoolSize);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setMaxQueueSize(maxQueueSize);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setPoolSize(poolSize);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile().setKeepAliveTime(keepAliveTime);
		camelContext.getExecutorServiceManager().getDefaultThreadPoolProfile()
				.setRejectedPolicy(ThreadPoolRejectedPolicy.valueOf(rejectPolicy));

	}

	@Bean(name = "restletComponent")
	public Component component() {
		Component component = new Component();
		component.getDefaultHost().setDefaultMatchingMode(matchingMode);
		return component;

	}

	@Bean
	public RestletComponent restletComponent() {
		RestletComponent component = new RestletComponent(component());
		component.setControllerDaemon(controller_daemon);
		component.setControllerSleepTimeMs(controller_sleep_time_ms);
		component.setInboundBufferSize(inbound_buffer_size);
		component.setMinThreads(min_threads);
		component.setMaxThreads(max_threads);
		component.setLowThreads(low_threads);
		component.setMaxQueued(max_queued);
		component.setMaxConnectionsPerHost(max_connections_per_host);
		component.setMaxTotalConnections(max_total_connections);
		component.setOutboundBufferSize(outbound_buffer_size);
		component.setPersistingConnections(persisting_connections);
		component.setPipeliningConnections(pipelining_connections);
		component.setThreadMaxIdleTimeMs(thread_max_idle_time_ms);
		component.setUseForwardedForHeader(use_forwarded_header);
		component.setReuseAddress(reuse_address);
		return component;
	}

}

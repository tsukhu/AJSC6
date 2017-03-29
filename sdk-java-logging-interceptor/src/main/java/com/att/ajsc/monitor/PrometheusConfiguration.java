package com.att.ajsc.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.CollectorRegistry;

@Configuration
public class PrometheusConfiguration {

	@Bean
	CollectorRegistry metricRegistry() {
		return CollectorRegistry.defaultRegistry;
	}

}

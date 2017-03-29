package com.att.ajsc.monitor;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementContextConfiguration;
import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import com.att.ajsc.logging.AjscEelfManager;
import com.att.ajsc.prometheus.JvmMatrixCollector;
import com.att.eelf.configuration.EELFLogger;

import io.prometheus.client.hotspot.ClassLoadingExports;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import io.prometheus.jmx.JmxCollector;

@ManagementContextConfiguration
public class AjscMonitorConfiguration {
	
	private static final EELFLogger logger = AjscEelfManager.getInstance().getLogger(AjscMonitorConfiguration.class);
	
	@Value("${jmx.configuration.file:etc/config/prometheus_jmx_config.yaml}")
	private String jmxConfiguration;

	@Bean
	public PrometheusEndpoint prometheusEndpoint() {
		return new PrometheusEndpoint();
	}

	@Bean
	@ConditionalOnBean(PrometheusEndpoint.class)
	@ConditionalOnEnabledEndpoint("prometheus")
	public AjscMonitor prometheusMvcEndpoint(PrometheusEndpoint prometheusEndpoint) {
		try {
			new JmxCollector(new File(jmxConfiguration)).register();
		} catch (Exception e) {
			logger.error("Error while registering the JMX Collector", e);
		}
		new StandardExports().register();
		new GarbageCollectorExports().register();
		new ThreadExports().register();
		new ClassLoadingExports().register();
		new JvmMatrixCollector().register();
		return new AjscMonitor(prometheusEndpoint);
	}

}
	

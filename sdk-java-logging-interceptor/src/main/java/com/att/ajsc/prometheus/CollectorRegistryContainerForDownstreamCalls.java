package com.att.ajsc.prometheus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;

@Component
public class CollectorRegistryContainerForDownstreamCalls {
	@Autowired
	private CollectorRegistry registry;
	private Summary elapsedTime = null;

	public Summary getElapsedTime() {

		if (elapsedTime == null) {
			elapsedTime = Summary.build().name("elapsedTimeByCallTypeAndTrxId")
					.help("Elapsed time for downstream calls")
					.labelNames(new String[] { "callType", "serviceName", "uri" }).register(registry);
		}
		return elapsedTime;

	}

}

package com.att.ajsc.monitor;

import org.springframework.boot.actuate.endpoint.mvc.AbstractEndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.HypermediaDisabled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.prometheus.client.exporter.common.TextFormat;

public class AjscMonitor extends AbstractEndpointMvcAdapter<PrometheusEndpoint> {

	public AjscMonitor(PrometheusEndpoint delegate) {
		super(delegate);
	}

	@RequestMapping(method = RequestMethod.GET, produces = TextFormat.CONTENT_TYPE_004)
	@ResponseBody
	@HypermediaDisabled
	protected Object invoke() {
		return super.invoke();
	}

}

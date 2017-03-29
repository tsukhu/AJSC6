package com.att.ajsc.monitor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import com.att.ajsc.prometheus.LogReader;

import io.prometheus.client.exporter.common.TextFormat;

public class PrometheusEndpoint extends AbstractEndpoint<String> {

	@Value("${endpoints.prometheus.enabled:true}")
	private Boolean isEnabled;

	@Autowired
	private LogReader logReader;

	private final static String ERROR_MESSAGE = "Error while converting the matrics to Prometheus format";

	public PrometheusEndpoint() {
		super("prometheus");
	}

	@PostConstruct
	public void updateEndpoint() {
		this.setEnabled(isEnabled);
	}

	@Override
	public String invoke() {

		Writer writer = new StringWriter();
		try {
			TextFormat.write004(writer, logReader.getRegistry().metricFamilySamples());
		} catch (IOException e) {
			return ERROR_MESSAGE;
		}

		return writer.toString();
	}

}

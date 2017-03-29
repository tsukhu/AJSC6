package com.att.ajsc.prometheus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;

@Component
public class CollectorRegistryContainerForService {
	@Autowired
	private CollectorRegistry registry;
	private Counter totalSuccesses = null;
	private Summary totalRequestLatencySummary = null;
	private Summary requestLatencySummary = null;
	private Gauge immediateAverageLatencyGauge = null;
	private Gauge minLatencyGauge = null;
	private Gauge maxLatencyGauge = null;
	private Counter totalUrlHits = null;
	private Counter totalUrlHitsByMethod = null;
	private Counter totalUrlHitsByCode = null;
	private Counter successes = null;
	private Counter errors = null;
	private Counter totalErrors = null;

	public Summary getRequestLatencySummary() {

		if (requestLatencySummary == null) {
			requestLatencySummary = Summary.build().name("summary_api_latency")
					.help("Summary of latencies associated with this application.")
					.labelNames(new String[] { "uri", "serviceName" }).register(registry);

		}
		return requestLatencySummary;
	}

	public Summary getTotalRequestLatencySummary() {

		if (totalRequestLatencySummary == null) {
			totalRequestLatencySummary = Summary.build().name("total_summary_api_latency")
					.help("Summary of latencies associated with this application including downstream calls.")
					.labelNames(new String[] { "uri", "serviceName" }).register(registry);

		}
		return totalRequestLatencySummary;
	}

	public Gauge getMinRequestLatency() {

		if (minLatencyGauge == null) {
			minLatencyGauge = Gauge.build().name("minimum_response_time").help("minimum_response_time")
					.labelNames(new String[] { "uri", "serviceName" }).register(registry);
		}
		return minLatencyGauge;

	}

	public Gauge getMaxRequestLatency() {

		if (maxLatencyGauge == null) {
			maxLatencyGauge = Gauge.build().name("maximum_response_time").help("maximum_response_time")
					.labelNames(new String[] { "uri", "serviceName" }).register(registry);
		}
		return maxLatencyGauge;

	}

	public Gauge getImmediateAverageLatencyGauge() {

		if (immediateAverageLatencyGauge == null) {
			immediateAverageLatencyGauge = Gauge.build().name("avg_api_latency_by_method")
					.help("Average latency time of API transactions per every 5 seconds")
					.labelNames(new String[] { "uri", "serviceName", "method" }).register(registry);
		}
		return immediateAverageLatencyGauge;

	}

	public Counter getTotalUrlHits() {

		if (totalUrlHits == null) {
			totalUrlHits = Counter.build().name("total_url_hits").labelNames("uri", "serviceName")
					.help("Total number of requests to this application.").register(registry);
		}
		return totalUrlHits;
	}

	public Counter getTotalUrlHitsByMethod() {

		if (totalUrlHitsByMethod == null) {
			totalUrlHitsByMethod = Counter.build().name("total_url_hits_by_method")
					.labelNames("uri", "serviceName", "method")
					.help("Total number of requests to this application by method.").register(registry);
		}
		return totalUrlHitsByMethod;

	}

	public Counter getTotalUrlHitsByCode() {

		if (totalUrlHitsByCode == null) {
			totalUrlHitsByCode = Counter.build().name("total_url_hits_by_code").labelNames("uri", "serviceName", "code")
					.help("Total number of requests to this application by code.").register(registry);
		}
		return totalUrlHitsByCode;

	}

	public Counter getSuccesses() {

		if (successes == null) {
			successes = Counter.build().name("successes_by_uri").labelNames("uri", "serviceName")
					.help("Total number of successful requests to this application by uri.").register(registry);
		}
		return successes;

	}

	public Counter getErrors() {

		if (errors == null) {
			errors = Counter.build().name("errors_by_uri").labelNames("uri", "serviceName")
					.help("Total number of failed requests to this applicationby uri.").register(registry);
		}
		return errors;

	}

	public Counter getTotalSuccesses() {

		if (totalSuccesses == null) {
			totalSuccesses = Counter.build().name("successes_total")
					.help("Total number of successful requests to this application.").register(registry);
		}
		return totalSuccesses;
	}

	public Counter getTotalErrors() {

		if (totalErrors == null) {
			totalErrors = Counter.build().name("errors_total")
					.help("Total number of failed requests to this application.").register(registry);
		}
		return totalErrors;

	}

}

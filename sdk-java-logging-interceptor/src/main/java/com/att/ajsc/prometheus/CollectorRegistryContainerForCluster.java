package com.att.ajsc.prometheus;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;

public class CollectorRegistryContainerForCluster {

	private Counter totalRequests;
	private Counter totalRequestsByJob;
	private Counter totalSuccesses;
	private Counter totalErrors;
	private Summary totalRequestLatencySummary;
	private Gauge immediateAverageLatencyGauge;
	private CollectorRegistry registry;

	public CollectorRegistryContainerForCluster(CollectorRegistry registry) {
		this.registry = registry;
	}

	public Counter getTotalRequests() {

		if (totalRequests == null) {
			totalRequests = Counter.build().name("requests_to_cluster")
					.help("Total number of  requests to this cluster.").register(registry);
		}
		return totalRequests;
	}

	public Counter getTotalRequestsByJob() {

		if (totalRequestsByJob == null) {
			totalRequestsByJob = Counter.build().name("requests_to_cluster_per_job")
					.help("Total number of  requests to this cluster per application.").labelNames("serviceName")
					.register(registry);
		}
		return totalRequestsByJob;

	}

	public Counter getTotalSuccesses() {

		if (totalSuccesses == null) {
			totalSuccesses = Counter.build().name("successes_in_cluster")
					.help("Total number of successful requests to this cluster.").register(registry);
		}

		return totalSuccesses;

	}

	public Counter getTotalErrors() {

		if (totalErrors == null) {
			totalErrors = Counter.build().name("errors_in_cluster")
					.help("Total number of failed requests to this cluster.").register(registry);
		}
		return totalErrors;

	}

	public Summary getTotalRequestLatencySummary() {

		if (totalRequestLatencySummary == null) {
			totalRequestLatencySummary = Summary.build().name("summary_api_latency_by_cluster")
					.help("Summary of latencies associated with this application.").register(registry);
		}
		return totalRequestLatencySummary;

	}

	public Gauge getImmediateAverageLatencyGauge() {

		if (immediateAverageLatencyGauge == null) {
			immediateAverageLatencyGauge = Gauge.build().name("avg_api_latency_by_method_in_cluster")
					.help("Average latency time of API transactions per every 5 seconds")
					.labelNames(new String[] { "method" }).register(registry);
		}
		return immediateAverageLatencyGauge;

	}

}

package com.att.ajsc.prometheus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MinorPerformanceTxnInbound;
import com.att.ajsc.logging.json.MinorPerformanceTxnOutbound;
import com.att.ajsc.logging.json.MinorPerformanceTxnPair;
import com.att.ajsc.logging.json.PerformanceLogRecord;

import io.prometheus.client.CollectorRegistry;

@Component
public class LogReader {

	static final Logger logger = LoggerFactory.getLogger(LogReader.class);

	@Autowired
	private CollectorRegistry registry;
	@Autowired
	private CollectorRegistryContainerForService registryMapForServices;
	@Autowired
	private CollectorRegistryContainerForDownstreamCalls registryMapForDownstreamCalls;
	// private Map<String, CollectorRegistryContainerForCluster>
	// registryMapForClusters = new HashMap<String,
	// CollectorRegistryContainerForCluster>();

	public synchronized void readPerformanceLog(PerformanceLogRecord perfLogRecord) {

		Set<MinorPerformanceTxnPair> downstreamCalls = perfLogRecord.getMinorPerformanceTxnPairs();

		for (MinorPerformanceTxnPair pair : downstreamCalls) {
			MinorPerformanceTxnInbound inbound = pair.getMinorPerformanceTxnInbound();
			MinorPerformanceTxnOutbound outbound = pair.getMinorPerformanceTxnOutbound();

			boolean isValidInboundRecord = isValidInboundRecord(inbound);
			boolean isValidOutboundRecord = isValidOutboundRecord(outbound);

			if (isValidInboundRecord && isValidOutboundRecord) {

				String downstreamType = inbound.getDownstreamCallType();
				if (StringUtils.isEmpty(downstreamType)) {
					downstreamType = "down stream call";
				}

				String downstreamService = inbound.getServiceName();

				long start = Long.valueOf(inbound.getStartTime());

				long end = Long.valueOf(outbound.getEndTime());

				long elapsedTime = end - start;

				if (registryMapForServices.getMinRequestLatency().labels(downstreamService, downstreamService)
						.get() == 0
						|| elapsedTime < registryMapForServices.getMinRequestLatency()
								.labels(downstreamService, downstreamService).get()) {
					registryMapForServices.getMinRequestLatency().labels(downstreamService, downstreamService)
							.set(elapsedTime);
				}
				if (registryMapForServices.getMaxRequestLatency().labels(downstreamService, downstreamService)
						.get() == 0
						|| elapsedTime > registryMapForServices.getMaxRequestLatency()
								.labels(downstreamService, downstreamService).get()) {
					registryMapForServices.getMaxRequestLatency().labels(downstreamService, downstreamService)
							.set(elapsedTime);
				}

				registryMapForDownstreamCalls.getElapsedTime()
						.labels(downstreamType, downstreamService, downstreamService).observe(elapsedTime);
				registryMapForServices.getTotalRequestLatencySummary().labels(downstreamService, downstreamService)
						.observe(elapsedTime);

			} else {
				logger.error(
						"MinorPerformanceTxnInbound must contain a a service name and valid start time.MinorPerformanceTxnOutbound must contain valid end time ");
			}
		}
	}

	private boolean isValidInboundRecord(MinorPerformanceTxnInbound inbound) {

		boolean isValid = false;
		if (inbound != null && StringUtils.isNotEmpty(inbound.getServiceName())) {
			isValid = true;
			try {
				Long.valueOf(inbound.getStartTime());
			} catch (Exception e) {
				isValid = false;
			}

		}
		return isValid;
	}

	private boolean isValidOutboundRecord(MinorPerformanceTxnOutbound outbound) {

		boolean isValid = false;
		if (outbound != null) {
			isValid = true;
			try {
				Long.valueOf(outbound.getEndTime());
			} catch (Exception e) {
				isValid = false;
			}
		}
		return isValid;
	}

	public synchronized void readAuditLog(AuditLogRecord auditLogRecord) {

		Map<String, Double> averageLatencyMapPerService = new HashMap<String, Double>();
		Map<String, Double> averageLatencyMapPerCluster = new HashMap<String, Double>();

		// parse transactionName
		String serviceName = auditLogRecord.getTransactionName();

		// parse transactionName
		String cluster = auditLogRecord.getCluster();

		/*
		 * if (!registryMapForClusters.containsKey(cluster)) {
		 * registryMapForClusters.put(cluster, new
		 * CollectorRegistryContainerForCluster(registry)); }
		 */

		double elapsedTime = Double.valueOf(auditLogRecord.getElapsedTime());
		String url = auditLogRecord.getRequestURL();

		String method = auditLogRecord.getHttpMethod();

		String responseCode = auditLogRecord.getResponseCode();
		String status = auditLogRecord.getTransactionStatus();

		// CollectorRegistryContainerForCluster clusterContainer =
		// registryMapForClusters.get(cluster);

		if (registryMapForServices.getMinRequestLatency().labels(url, serviceName).get() == 0
				|| elapsedTime < registryMapForServices.getMinRequestLatency().labels(url, serviceName).get()) {
			registryMapForServices.getMinRequestLatency().labels(url, serviceName).set(elapsedTime);
		}
		if (registryMapForServices.getMaxRequestLatency().labels(url, serviceName).get() == 0
				|| elapsedTime > registryMapForServices.getMaxRequestLatency().labels(url, serviceName).get()) {
			registryMapForServices.getMaxRequestLatency().labels(url, serviceName).set(elapsedTime);
		}

		registryMapForServices.getRequestLatencySummary().labels(url, serviceName).observe(elapsedTime);
		registryMapForServices.getTotalRequestLatencySummary().labels(url, serviceName).observe(elapsedTime);

		/*
		 * clusterContainer.getTotalRequestLatencySummary().observe(elapsedTime)
		 * ; clusterContainer.getTotalRequests().inc();
		 * clusterContainer.getTotalRequestsByJob().labels(serviceName).inc();
		 */

		// gauge data to push average latency for the last five
		// seconds of this URL

		String servicekey = serviceName + url + method;
		if (!averageLatencyMapPerService.containsKey(servicekey)) {
			averageLatencyMapPerService.put(servicekey, 0.0);
		}

		String clusterkey = cluster + method;
		if (!averageLatencyMapPerCluster.containsKey(clusterkey)) {
			averageLatencyMapPerCluster.put(clusterkey, 0.0);
		}

		double currentAvgLatencyService = averageLatencyMapPerService.get(servicekey);
		double newAvgLatencyService = 0.0;

		if (currentAvgLatencyService == 0.0) {
			newAvgLatencyService = elapsedTime;
			averageLatencyMapPerService.put(servicekey, newAvgLatencyService);
		} else {
			newAvgLatencyService = (double) (elapsedTime + currentAvgLatencyService) / (double) 2;
			averageLatencyMapPerService.put(servicekey, newAvgLatencyService);
		}

		double currentAvgLatencyCluster = averageLatencyMapPerCluster.get(clusterkey);
		double newAvgLatencyCluster = 0.0;

		if (currentAvgLatencyCluster == 0.0) {
			newAvgLatencyCluster = elapsedTime;
			averageLatencyMapPerCluster.put(clusterkey, newAvgLatencyCluster);
		} else {
			newAvgLatencyCluster = (double) (elapsedTime + currentAvgLatencyCluster) / (double) 2;
			averageLatencyMapPerCluster.put(clusterkey, newAvgLatencyCluster);
		}

		registryMapForServices.getImmediateAverageLatencyGauge().labels(url, serviceName, method)
				.set(newAvgLatencyService);
		// clusterContainer.getImmediateAverageLatencyGauge().labels(method).set(newAvgLatencyCluster);

		registryMapForServices.getTotalUrlHits().labels(url, serviceName).inc();
		registryMapForServices.getTotalUrlHitsByMethod().labels(url, serviceName, method).inc();
		registryMapForServices.getTotalUrlHitsByCode().labels(url, serviceName, responseCode).inc();

		if (StringUtils.equals(status, "C")) {
			registryMapForServices.getSuccesses().labels(url, serviceName).inc();
			registryMapForServices.getTotalSuccesses().inc();
			// clusterContainer.getTotalSuccesses().inc();
		} else if (StringUtils.equals(status, "E")) {
			registryMapForServices.getErrors().labels(url, serviceName).inc();
			registryMapForServices.getTotalErrors().inc();
			// clusterContainer.getTotalErrors().inc();

		}

	}

	public CollectorRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(CollectorRegistry registry) {
		this.registry = registry;
	}

	@Async
	public void readLog(PerformanceLogRecord performanceLogRecord, AuditLogRecord auditLogRecord) {
		readAuditLog(auditLogRecord);
		readPerformanceLog(performanceLogRecord);
	}

}

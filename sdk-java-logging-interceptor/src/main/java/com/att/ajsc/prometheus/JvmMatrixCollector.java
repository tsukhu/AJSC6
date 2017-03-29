package com.att.ajsc.prometheus;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.GaugeMetricFamily;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JvmMatrixCollector extends Collector {
	private final MemoryMXBean memoryBean;
	private final List<MemoryPoolMXBean> poolBeans;

	private Gauge memory = null;
	private Gauge freeMemory = null;

	public JvmMatrixCollector() {
		this(ManagementFactory.getMemoryMXBean(), ManagementFactory.getMemoryPoolMXBeans());
	}

	public JvmMatrixCollector(MemoryMXBean memoryBean, List<MemoryPoolMXBean> poolBeans) {
		this.memoryBean = memoryBean;
		this.poolBeans = poolBeans;
	}

	void addMemoryAreaMetrics(List<MetricFamilySamples> sampleFamilies) {
		MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
		MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

		GaugeMetricFamily used = new GaugeMetricFamily("jvm_memory_bytes_used",
				"Used bytes of a given JVM memory area.", Collections.singletonList("area"));
		used.addMetric(Collections.singletonList("heap"), Double.valueOf(String.valueOf(heapUsage.getUsed())));
		used.addMetric(Collections.singletonList("nonheap"), nonHeapUsage.getUsed());
		sampleFamilies.add(used);

		GaugeMetricFamily committed = new GaugeMetricFamily("jvm_memory_bytes_committed",
				"Committed (bytes) of a given JVM memory area.", Collections.singletonList("area"));
		committed.addMetric(Collections.singletonList("heap"), heapUsage.getCommitted());
		committed.addMetric(Collections.singletonList("nonheap"), nonHeapUsage.getCommitted());
		sampleFamilies.add(committed);

		GaugeMetricFamily max = new GaugeMetricFamily("jvm_memory_bytes_max", "Max (bytes) of a given JVM memory area.",
				Collections.singletonList("area"));
		max.addMetric(Collections.singletonList("heap"), heapUsage.getMax());
		max.addMetric(Collections.singletonList("nonheap"), nonHeapUsage.getMax());
		sampleFamilies.add(max);
	}

	void addMemoryPoolMetrics(List<MetricFamilySamples> sampleFamilies) {
		GaugeMetricFamily used = new GaugeMetricFamily("jvm_memory_pool_bytes_used",
				"Used bytes of a given JVM memory pool.", Collections.singletonList("pool"));
		sampleFamilies.add(used);
		GaugeMetricFamily committed = new GaugeMetricFamily("jvm_memory_pool_bytes_committed",
				"Committed bytes of a given JVM memory pool.", Collections.singletonList("pool"));
		sampleFamilies.add(committed);
		GaugeMetricFamily max = new GaugeMetricFamily("jvm_memory_pool_bytes_max",
				"Max bytes of a given JVM memory pool.", Collections.singletonList("pool"));
		sampleFamilies.add(max);
		for (final MemoryPoolMXBean pool : poolBeans) {
			MemoryUsage poolUsage = pool.getUsage();
			used.addMetric(Collections.singletonList(pool.getName()), poolUsage.getUsed());
			committed.addMetric(Collections.singletonList(pool.getName()), poolUsage.getCommitted());
			max.addMetric(Collections.singletonList(pool.getName()), poolUsage.getMax());
		}
	}

	private void addMemoryMetrics() {

		long nonHeapMemory = 0;
		Runtime runtime = Runtime.getRuntime();

		try {
			nonHeapMemory = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
		} catch (Throwable ex) {
			nonHeapMemory = 0;
		}

		getMemory().set(runtime.totalMemory() + nonHeapMemory);
		getFreeMemory().set(runtime.freeMemory());

	}

	public List<MetricFamilySamples> collect() {
		List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
		addMemoryAreaMetrics(mfs);
		addMemoryPoolMetrics(mfs);
		addMemoryMetrics();
		return mfs;
	}

	public Gauge getMemory() {

		if (memory == null) {
			memory = Gauge.build().name("memory").help("memory").register(CollectorRegistry.defaultRegistry);
		}
		return memory;

	}

	public Gauge getFreeMemory() {

		if (freeMemory == null) {
			freeMemory = Gauge.build().name("free_memory").help("free_memory")
					.register(CollectorRegistry.defaultRegistry);
		}
		return freeMemory;

	}
}

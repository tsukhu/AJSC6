package com.att.ajsc.interceptors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.ajsc.common.LoggingUtil;
import com.att.ajsc.common.interceptors.AjscPostInterceptor;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.ajsc.logging.LogMsgBuilderFactory;
import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MajorPerformanceTxnOutbound;
import com.att.ajsc.logging.json.PerformanceLogRecord;
import com.att.ajsc.prometheus.LogReader;
import com.att.eelf.configuration.EELFLogger;

public class LoggingCamelPostInterceptor extends AjscPostInterceptor {

	public static final String PERFORMANCE_LOG = "performanceLogRecord";
	public static final String START_TIME = "startTtime";
	public static final String AUDIT_LOG = "auditLogRecord";
	public static final EELFLogger auditLogger = AjscEelfManager.getInstance().getAuditLogger();
	public static final EELFLogger performanceLogger = AjscEelfManager.getInstance().getPerformanceLogger();

	@Autowired
	private LogReader logReader;

	public LoggingCamelPostInterceptor() {
		setPosition(Integer.MAX_VALUE);
	}

	@Override
	public boolean allowOrReject(Exchange exchange) {

		AuditLogRecord auditLog = null;
		long endTime = System.currentTimeMillis();

		if (exchange.getProperty(PERFORMANCE_LOG) == null) {
			return true;
		}

		if (exchange.getProperty(AUDIT_LOG) == null) {
			auditLog = new AuditLogRecord();
		} else {

			auditLog = (AuditLogRecord) exchange.getProperty(AUDIT_LOG);
		}

		PerformanceLogRecord performanceLog = updatePerformanceLog(exchange, endTime);

		auditLog = updateAuditLog(exchange, auditLog, performanceLog, endTime);
		logReader.readLog(performanceLog, auditLog);
		auditLogger.info(LogMsgBuilderFactory.get(auditLog).build());
		performanceLogger.info(LogMsgBuilderFactory.get(performanceLog).build());

		return true;
	}

	public PerformanceLogRecord updatePerformanceLog(Exchange exchange, long endTime) {

		String hostName = null;
		String vTier = null;
		PerformanceLogRecord performanceLog = (PerformanceLogRecord) exchange.getProperty(PERFORMANCE_LOG);
		String responseCode = String.valueOf(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
		performanceLog.setResponseCode(responseCode);
		performanceLog.setResponseMsgSize(LoggingUtil.getLength(exchange).toString());
		MajorPerformanceTxnOutbound majorPerformanceTxnOutbound = new MajorPerformanceTxnOutbound();
		majorPerformanceTxnOutbound.setServiceName(performanceLog.getServiceName());
		majorPerformanceTxnOutbound.setEndTime(String.valueOf(endTime));
		if (responseCode.equalsIgnoreCase("200")) {
			majorPerformanceTxnOutbound.setTransactionStatus("C");
		} else {
			majorPerformanceTxnOutbound.setTransactionStatus("E");
		}
		performanceLog.setMajorPerformanceTxnOutbound(majorPerformanceTxnOutbound);

		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhe) {
			hostName = "N/A";
		}
		int i = hostName.indexOf('.');
		if (i > 0)
			vTier = hostName.substring(0, i);
		else
			vTier = hostName;

		performanceLog.setCluster("cluster"); // TODO
		performanceLog.setVtier(vTier);

		return performanceLog;

	}

	public AuditLogRecord updateAuditLog(Exchange exchange, AuditLogRecord auditLog,
			PerformanceLogRecord performanceLog, long endTime) {

		HttpServletRequest request = null;
		String servicename = performanceLog.getServiceName();
		long startTime = (long) exchange.getProperty(START_TIME);
		exchange.removeProperty(START_TIME);

		auditLog.setInitiatedTimestamp(performanceLog.getStartTime());
		auditLog.setApplicationId(performanceLog.getUserID());
		auditLog.setConversationid(performanceLog.getConversationId());
		auditLog.setUniqueTransactionId(performanceLog.getUniqueTransactionId());
		auditLog.setHttpMethod(performanceLog.getHttpMethod());
		auditLog.setRequestURL(String.valueOf(exchange.getIn().getHeader(Exchange.HTTP_URL)));
		auditLog.setElapsedTime(String.valueOf(endTime - startTime));
		String responseCode = String.valueOf(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
		if (responseCode.equalsIgnoreCase("200")) {
			auditLog.setTransactionStatus("C");
		} else {
			auditLog.setTransactionStatus("E");
		}

		auditLog.setResponseCode(responseCode);
		auditLog.setEndTimestamp(new Timestamp(endTime).toString());
		auditLog.setTransactionName(servicename);

		request = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
		if (request != null) {
			auditLog.setClientIp(request.getRemoteAddr());
		}
		auditLog.setVtier(performanceLog.getVtier());
		auditLog.setCluster(performanceLog.getCluster());
		return auditLog;

	}

	public static boolean isNullOrEmpty(String s) {
		return (s == null || s.trim().isEmpty());
	}

	public static String ifNullThenEmpty(String s) {
		return (isNullOrEmpty(s) ? "" : s);
	}

}

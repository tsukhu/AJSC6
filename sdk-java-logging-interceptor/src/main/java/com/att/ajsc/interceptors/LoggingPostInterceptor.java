package com.att.ajsc.interceptors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.att.ajsc.common.AjscPostInterceptor;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.ajsc.logging.LogMsgBuilderFactory;
import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MajorPerformanceTxnOutbound;
import com.att.ajsc.logging.json.PerformanceLogRecord;
import com.att.ajsc.prometheus.LogReader;
import com.att.eelf.configuration.EELFLogger;

public class LoggingPostInterceptor extends AjscPostInterceptor {

	public static final String PERFORMANCE_LOG = "performanceLogRecord";
	public static final String START_TIME = "startTtime";
	public static final EELFLogger auditLogger = AjscEelfManager.getInstance().getAuditLogger();
	public static final EELFLogger performanceLogger = AjscEelfManager.getInstance().getPerformanceLogger();
	public static final String AUDIT_LOG = "auditLogRecord";
	public static final String REQUEST = "request";

	@Autowired
	private LogReader logReader;

	public LoggingPostInterceptor() {
		setPosition(Integer.MAX_VALUE);
	}

	@Override
	public boolean allowOrReject(ContainerRequestContext requestContext, ContainerResponseContext ressponseContext) {

		if (requestContext.getProperty(PERFORMANCE_LOG) == null) {
			return true;
		}

		AuditLogRecord auditLog = null;
		long endTime = System.currentTimeMillis();

		if (requestContext.getProperty(AUDIT_LOG) == null) {
			auditLog = new AuditLogRecord();
		} else {
			auditLog = (AuditLogRecord) requestContext.getProperty(AUDIT_LOG);
		}
		PerformanceLogRecord performanceLog = updatePerformanceLog(requestContext, ressponseContext, endTime);

		auditLog = updateAuditLog(requestContext, ressponseContext, auditLog, performanceLog, endTime);

		auditLogger.info(LogMsgBuilderFactory.get(auditLog).build());
		performanceLogger.info(LogMsgBuilderFactory.get(performanceLog).build());
		logReader.readLog(performanceLog, auditLog);

		return true;
	}

	public PerformanceLogRecord updatePerformanceLog(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext, long endTime) {

		String hostName = null;
		String vTier = null;

		PerformanceLogRecord performanceLog = (PerformanceLogRecord) requestContext.getProperty(PERFORMANCE_LOG);

		performanceLog.setResponseCode(String.valueOf(responseContext.getStatusInfo().getStatusCode()));
		performanceLog.setResponseMsgSize(String.valueOf(responseContext.getLength()));
		MajorPerformanceTxnOutbound majorPerformanceTxnOutbound = new MajorPerformanceTxnOutbound();
		majorPerformanceTxnOutbound.setServiceName(performanceLog.getServiceName());
		majorPerformanceTxnOutbound.setEndTime(new Timestamp(endTime).toString());
		if (responseContext.getStatusInfo().getStatusCode() == 200) {
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

	public AuditLogRecord updateAuditLog(ContainerRequestContext requestContext,
			ContainerResponseContext ressponseContext, AuditLogRecord auditLog, PerformanceLogRecord performanceLog,
			long endTime) {

		String servicename = performanceLog.getServiceName();
		long startTime = (long) requestContext.getProperty(START_TIME);
		requestContext.removeProperty(START_TIME);

		auditLog.setInitiatedTimestamp(performanceLog.getStartTime());
		auditLog.setApplicationId(performanceLog.getUserID());
		auditLog.setConversationid(performanceLog.getConversationId());
		auditLog.setUniqueTransactionId(performanceLog.getUniqueTransactionId());
		auditLog.setHttpMethod(performanceLog.getHttpMethod());
		auditLog.setRequestURL(requestContext.getUriInfo().getAbsolutePath().toString());
		auditLog.setElapsedTime(String.valueOf(endTime - startTime));
		if (ressponseContext.getStatusInfo().getStatusCode() == 200) {
			auditLog.setTransactionStatus("C");
		} else {
			auditLog.setTransactionStatus("E");
		}

		auditLog.setResponseCode(String.valueOf(ressponseContext.getStatusInfo().getStatusCode()));
		auditLog.setEndTimestamp(new Timestamp(endTime).toString());
		auditLog.setTransactionName(servicename);
		if (requestContext.getProperty(REQUEST) != null) {
			auditLog.setClientIp(((HttpServletRequest) (requestContext.getProperty(REQUEST))).getRemoteAddr());
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

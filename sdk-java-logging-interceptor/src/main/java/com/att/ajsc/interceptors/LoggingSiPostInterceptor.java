package com.att.ajsc.interceptors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import com.att.ajsc.common.si.interceptors.AjscPostInterceptor;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.ajsc.logging.LogMsgBuilderFactory;
import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MajorPerformanceTxnOutbound;
import com.att.ajsc.logging.json.PerformanceLogRecord;
import com.att.eelf.configuration.EELFLogger;

public class LoggingSiPostInterceptor extends AjscPostInterceptor {

	public static final String PERFORMANCE_LOG = "performanceLogRecord";
	public static final String AUDIT_LOG = "auditLogRecord";
	public static final String START_TIME = "startTtime";
	public static final String STATUSCODE_HEADER = "http_statusCode";
	final EELFLogger auditLogger = AjscEelfManager.getInstance().getAuditLogger();
	final EELFLogger performanceLogger = AjscEelfManager.getInstance().getPerformanceLogger();

	public LoggingSiPostInterceptor() {
		setPosition(Integer.MAX_VALUE + 1);
	}

	@Override
	public Message<?> allowOrReject(Message<?> message) {

		long endTime = System.currentTimeMillis();

		AuditLogRecord auditLog = new AuditLogRecord();// (AuditLogRecord)
														// message.getHeaders().get(AUDIT_LOG);
		PerformanceLogRecord performanceLog = updatePerformanceLog(message, endTime);

		auditLog = updateAuditLog(message, auditLog, performanceLog, endTime);

		auditLogger.info(LogMsgBuilderFactory.get(auditLog).build());
		performanceLogger.info(LogMsgBuilderFactory.get(performanceLog).build());

		return message;
	}

	/**
	 * 
	 * @param message
	 * @param endTime
	 * @return PerformanceLogRecord
	 */
	public PerformanceLogRecord updatePerformanceLog(Message<?> message, long endTime) {

		String hostName = null;
		String vTier = null;

		PerformanceLogRecord performanceLog = (PerformanceLogRecord) message.getHeaders().get(PERFORMANCE_LOG);
		String respCode = this.responseCode(message.getHeaders());
		performanceLog.setResponseCode(respCode);
		performanceLog.setResponseMsgSize(String.valueOf(message.getPayload().toString().length()));
		MajorPerformanceTxnOutbound majorPerformanceTxnOutbound = new MajorPerformanceTxnOutbound();
		majorPerformanceTxnOutbound.setServiceName(performanceLog.getServiceName());
		majorPerformanceTxnOutbound.setEndTime(String.valueOf(endTime));
		if ("200".equalsIgnoreCase(respCode)) {
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

	/**
	 * 
	 * @param message
	 * @param auditLog
	 * @param performanceLog
	 * @param endTime
	 * @return AuditLogRecord
	 */
	public AuditLogRecord updateAuditLog(Message<?> message, AuditLogRecord auditLog,
			PerformanceLogRecord performanceLog, long endTime) {

		String hostName = null;
		String vTier = null;
		String ipAddress = null;

		String servicename = performanceLog.getServiceName();
		long startTime = (long) message.getHeaders().get(START_TIME);
		// message.getHeaders().remove(START_TIME);

		auditLog.setInitiatedTimestamp(performanceLog.getStartTime());
		auditLog.setApplicationId(performanceLog.getUserID());
		auditLog.setConversationid(performanceLog.getConversationId());
		auditLog.setUniqueTransactionId(performanceLog.getUniqueTransactionId());
		auditLog.setHttpMethod(performanceLog.getHttpMethod());
		auditLog.setRequestURL(super.getUri());
		auditLog.setElapsedTime(String.valueOf(endTime - startTime));
		String respCode = this.responseCode(message.getHeaders());
		if ("200".equalsIgnoreCase(respCode)) {
			auditLog.setTransactionStatus("C");
		} else {
			auditLog.setTransactionStatus("E");
		}

		auditLog.setResponseCode(this.responseCode(message.getHeaders()));
		auditLog.setEndTimestamp(new Timestamp(endTime).toString());
		auditLog.setTransactionName(servicename);

		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException uhe) {
			ipAddress = "N/A";
		}

		auditLog.setVtier(vTier);
		auditLog.setCluster(performanceLog.getCluster());
		return auditLog;

	}

	/**
	 * 
	 * @param headers
	 * @return String
	 */
	public String responseCode(MessageHeaders headers) {
		String responseCode = "200"; // Spring Integration default response code
										// is 200
		if (headers.get(STATUSCODE_HEADER) != null) {
			responseCode = String.valueOf(headers.get(STATUSCODE_HEADER));
		}
		return responseCode;
	}

	/**
	 * 
	 * @param s
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(String s) {
		return (s == null || s.trim().isEmpty());
	}

	/**
	 * 
	 * @param s
	 * @return String
	 */
	public static String ifNullThenEmpty(String s) {
		return (isNullOrEmpty(s) ? "" : s);
	}
}

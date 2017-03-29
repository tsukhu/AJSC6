package com.att.ajsc.interceptors;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.PathSegment;

import com.att.ajsc.common.AjscPreInterceptor;
import com.att.ajsc.common.LoggingUtil;
import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MajorPerformanceTxnInbound;
import com.att.ajsc.logging.json.PerformanceLogRecord;

public class LoggingPreInterceptor extends AjscPreInterceptor {

	public static final String PERFORMANCE_LOG = "performanceLogRecord";
	public static final String TRANSACTION_ID = "X-ATT-Transaction-Id";
	public static final String USER_ID = "x-UserId";
	public static final String START_TIME = "startTtime";
	public static final String CONVERSATION_ID = "X-ATT-ConversationId";
	public static final String ORIGINATOR_ID = "X-ATT-OriginatorId";
	public static final String MESSAGE_ID = "X-ATT-MessageId";
	public static final String CSI_MESSAGE_ID = "X-CSI-MessageId";
	public static final String CSI_CONVERSATION_ID = "X-CSI-ConversationId";
	public static final String CSI_ORIGINATOR_ID = "X-CSI-OriginatorId";
	public static final String AUDIT_LOG = "auditLogRecord";
	public static final String HTTP_AUTHORIZATION = "Authorization";

	public LoggingPreInterceptor() {
		setPosition(Integer.MIN_VALUE + 5);
	}

	@Override
	public boolean allowOrReject(ContainerRequestContext requestContext) {

		PerformanceLogRecord performanceLog = getPerformanceLogRecord(requestContext);
		requestContext.setProperty(PERFORMANCE_LOG, performanceLog);

		return true;
	}

	public PerformanceLogRecord getPerformanceLogRecord(ContainerRequestContext requestContext) {

		PerformanceLogRecord performanceLog = new PerformanceLogRecord();
		String userNme = null;
		String servicename = null;
		long startTime = System.currentTimeMillis();

		performanceLog.setStartTime(new Timestamp(System.currentTimeMillis()).toString());

		servicename = getServiceName(requestContext);
		performanceLog.setServiceName(servicename);

		if (requestContext.getHeaderString(CONVERSATION_ID) != null) {
			performanceLog.setConversationId(requestContext.getHeaderString(CONVERSATION_ID));
		} else if (requestContext.getProperty(CSI_CONVERSATION_ID) != null) {
			performanceLog.setConversationId(requestContext.getProperty(CSI_CONVERSATION_ID).toString());
		}
		if (requestContext.getHeaderString(ORIGINATOR_ID) != null) {
			performanceLog.setOriginatorId(requestContext.getHeaderString(ORIGINATOR_ID));
		} else if (requestContext.getProperty(CSI_ORIGINATOR_ID) != null) {
			performanceLog.setOriginatorId(requestContext.getProperty(CSI_ORIGINATOR_ID).toString());
		}
		AuditLogRecord auditLog = new AuditLogRecord();
		if (requestContext.getHeaderString(MESSAGE_ID) != null) {
			auditLog.setOriginalMessageId(requestContext.getHeaderString(MESSAGE_ID));
		} else if (requestContext.getProperty(CSI_MESSAGE_ID) != null) {
			auditLog.setOriginalMessageId(requestContext.getHeaderString(CSI_MESSAGE_ID));
		}

		requestContext.setProperty(AUDIT_LOG, auditLog);

		performanceLog.setUniqueTransactionId(requestContext.getHeaderString(TRANSACTION_ID).toString());
		performanceLog.setRequestMsgSize(String.valueOf(requestContext.getLength()));
		performanceLog.setHttpMethod(requestContext.getMethod());
		if (requestContext.getHeaderString(USER_ID) != null) {
			userNme = requestContext.getHeaderString(USER_ID);
		} else {
			userNme = LoggingUtil.getUserName(requestContext.getHeaderString(HTTP_AUTHORIZATION));
		}

		performanceLog.setUserID(userNme);
		performanceLog.setTransactionStatus("I");

		MajorPerformanceTxnInbound majorPerformanceTxnInbound = new MajorPerformanceTxnInbound();
		majorPerformanceTxnInbound.setServiceName(servicename);
		requestContext.setProperty(START_TIME, startTime);
		majorPerformanceTxnInbound.setStartTime(new Timestamp(startTime).toString());
		performanceLog.setMajorPerformanceTxnInbound(majorPerformanceTxnInbound);

		return performanceLog;

	}

	public String getServiceName(ContainerRequestContext requestContext) {

		String serviceName = "";

		List<PathSegment> pathSegments = requestContext.getUriInfo().getPathSegments();
		for (Iterator<PathSegment> iterator = pathSegments.iterator(); iterator.hasNext();) {
			PathSegment pathSegment = (PathSegment) iterator.next();
			if (serviceName == null) {
				serviceName = pathSegment.getPath();
			} else {
				serviceName = serviceName + "." + pathSegment.getPath();
			}
		}

		return serviceName;

	}

}

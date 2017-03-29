package com.att.ajsc.interceptors;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.camel.Exchange;

import com.att.ajsc.common.LoggingUtil;
import com.att.ajsc.common.interceptors.AjscPreInterceptor;
import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MajorPerformanceTxnInbound;
import com.att.ajsc.logging.json.PerformanceLogRecord;

public class LoggingCamelPreInterceptor extends AjscPreInterceptor {

	public static final String PERFORMANCE_LOG = "performanceLogRecord";
	public static final String TRANSACTION_ID = "X-ATT-Transaction-Id";
	public static final String MOCK_USER_NAME = "ajscUser";
	public static final String CONVERSATION_ID = "X-ATT-ConversationId";
	public static final String ORIGINATOR_ID = "X-ATT-OriginatorId";
	public static final String USER_ID = "x-UserId";
	public static final String START_TIME = "startTtime";
	public static final String SERVICE_NAME = "X-CSI-MethodName";
	public static final String MESSAGE_ID = "X-ATT-MessageId";
	public static final String CSI_MESSAGE_ID = "X-CSI-MessageId";
	public static final String CSI_CONVERSATION_ID = "X-CSI-ConversationId";
	public static final String CSI_ORIGINATOR_ID = "X-CSI-OriginatorId";
	public static final String AUDIT_LOG = "auditLogRecord";
	public static final String HTTP_AUTHORIZATION = "Authorization";

	@Context
	private HttpServletRequest servletRequest;

	public LoggingCamelPreInterceptor() {
		setPosition(Integer.MIN_VALUE + 5);
	}

	@Override
	public boolean allowOrReject(Exchange exchange) {

		PerformanceLogRecord performanceLog = getPerformanceLogRecord(exchange);
		exchange.setProperty(PERFORMANCE_LOG, performanceLog);

		return true;
	}

	public PerformanceLogRecord getPerformanceLogRecord(Exchange exchange) {

		PerformanceLogRecord performanceLog = new PerformanceLogRecord();
		String userNme = null;
		String serviceName = null;
		HttpServletRequest request;
		long startTime = System.currentTimeMillis();
		performanceLog.setStartTime(new Timestamp(System.currentTimeMillis()).toString());
		request = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
		serviceName = LoggingUtil.getServiceName(exchange);
		performanceLog.setServiceName(serviceName);

		if (exchange.getIn().getHeader(CONVERSATION_ID) != null) {
			performanceLog.setConversationId(exchange.getIn().getHeader(CONVERSATION_ID).toString());
		} else if (request.getAttribute(CSI_CONVERSATION_ID) != null) {
			performanceLog.setConversationId(request.getAttribute(CSI_CONVERSATION_ID).toString());
		}

		if (exchange.getIn().getHeader(ORIGINATOR_ID) != null) {
			performanceLog.setOriginatorId(exchange.getIn().getHeader(ORIGINATOR_ID).toString());
		} else if (request.getAttribute(CSI_ORIGINATOR_ID) != null) {
			performanceLog.setOriginatorId(request.getAttribute(CSI_ORIGINATOR_ID).toString());
		}

		AuditLogRecord auditLog = new AuditLogRecord();
		if (exchange.getIn().getHeader(MESSAGE_ID) != null) {
			auditLog.setOriginalMessageId(exchange.getIn().getHeader(MESSAGE_ID).toString());
		} else if (request.getAttribute(CSI_MESSAGE_ID) != null) {
			auditLog.setOriginalMessageId(request.getAttribute(CSI_MESSAGE_ID).toString());
		}
		exchange.setProperty(AUDIT_LOG, auditLog);

		performanceLog.setUniqueTransactionId(exchange.getIn().getHeader(TRANSACTION_ID).toString());

		performanceLog.setRequestMsgSize(String.valueOf(request.getContentLength()));
		performanceLog.setHttpMethod(exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString());
		if (exchange.getIn().getHeader(USER_ID) != null) {
			userNme = String.valueOf(exchange.getIn().getHeader(USER_ID));
		} else {
			String authorization = null;
			if (exchange.getIn().getHeader(HTTP_AUTHORIZATION) != null) {
				authorization = exchange.getIn().getHeader(HTTP_AUTHORIZATION).toString();
			}
			userNme = LoggingUtil.getUserName(authorization);
		}

		performanceLog.setUserID(userNme);

		performanceLog.setTransactionStatus("I");

		MajorPerformanceTxnInbound majorPerformanceTxnInbound = new MajorPerformanceTxnInbound();
		majorPerformanceTxnInbound.setServiceName(serviceName);
		exchange.setProperty(START_TIME, startTime);
		majorPerformanceTxnInbound.setStartTime(new Timestamp(startTime).toString());
		performanceLog.setMajorPerformanceTxnInbound(majorPerformanceTxnInbound);

		return performanceLog;

	}

	public String getServiceName(Exchange exchange) {

		return "";
	}

}

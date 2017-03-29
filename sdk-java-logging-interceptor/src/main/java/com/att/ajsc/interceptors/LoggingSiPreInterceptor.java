package com.att.ajsc.interceptors;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import com.att.ajsc.common.si.interceptors.AjscPreInterceptor;
import com.att.ajsc.logging.json.MajorPerformanceTxnInbound;
import com.att.ajsc.logging.json.PerformanceLogRecord;

public class LoggingSiPreInterceptor extends AjscPreInterceptor {

	public static final String PERFORMANCE_LOG = "performanceLogRecord";
	public static final String TRANSACTION_ID = "X-ATT-Transaction-Id";
	public static final String MOCK_USER_NAME = "ajscUser";
	public static final String CONVERSATION_ID = "X-ATT-ConversationId";
	public static final String ORIGINATOR_ID = "X-ATT-OriginatorId";
	public static final String USER_ID = "x-UserId";
	public static final String START_TIME = "startTtime";
	public static final String SERVICE_NAME = "X-CSI-MethodName";

	private static final String CONTENT_LENGTH = "content-length";

	@Context
	private HttpServletRequest servletRequest;

	public LoggingSiPreInterceptor() {
		setPosition(Integer.MIN_VALUE+2);
	}

	@Override
	public Message<?> allowOrReject(Message<?> message, MessageChannel channel) {

		long startTime = System.currentTimeMillis();

		PerformanceLogRecord performanceLog = getPerformanceLogRecord(message, channel, startTime);
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		messageHeaders.put(START_TIME, startTime);
		messageHeaders.put(PERFORMANCE_LOG, performanceLog);

		Message<?> updatedMessage = updateMessagewithHeader(message, messageHeaders);

		return updatedMessage;
	}

	/**
	 * 
	 * @param message
	 * @param channel
	 * @param startTime
	 * @return PerformanceLogRecord
	 */
	public PerformanceLogRecord getPerformanceLogRecord(Message<?> message, MessageChannel channel, long startTime) {

		PerformanceLogRecord performanceLog = new PerformanceLogRecord();
		String userNme = null;
		String servicename = null;

		String serviceName = "N/A";
		if (message.getHeaders().get(SERVICE_NAME) != null) {
			serviceName = message.getHeaders().get(SERVICE_NAME).toString();
		}

		performanceLog.setStartTime(new Timestamp(System.currentTimeMillis()).toString());
		if (message.getHeaders().get(CONVERSATION_ID) != null) {
			performanceLog.setConversationId(String.valueOf(message.getHeaders().get(CONVERSATION_ID)));
		}
		performanceLog.setUniqueTransactionId(message.getHeaders().get(TRANSACTION_ID).toString());
		performanceLog.setServiceName(serviceName);
		performanceLog.setRequestMsgSize(String.valueOf(message.getPayload().toString().length()));
		// performanceLog.setHttpMethod(message.getMethods());
		if (message.getHeaders().get(USER_ID) != null) {
			userNme = String.valueOf(message.getHeaders().get(USER_ID));
		} else {
			userNme = MOCK_USER_NAME;
		}

		performanceLog.setUserID(userNme);
		if (message.getHeaders().get(ORIGINATOR_ID) != null) {
			performanceLog.setOriginatorId(String.valueOf(message.getHeaders().get(ORIGINATOR_ID)));
		}

		performanceLog.setTransactionStatus("I");

		MajorPerformanceTxnInbound majorPerformanceTxnInbound = new MajorPerformanceTxnInbound();
		majorPerformanceTxnInbound.setServiceName(serviceName);
		majorPerformanceTxnInbound.setStartTime(new Timestamp(startTime).toString());
		performanceLog.setMajorPerformanceTxnInbound(majorPerformanceTxnInbound);

		return performanceLog;

	}

	/**
	 * 
	 * @param message
	 * @param messageHeaders
	 * @return Message<?>
	 */
	private Message<?> updateMessagewithHeader(Message<?> message, Map<String, Object> messageHeaders) {

		Map<String, Object> headers = new HashMap<String, Object>();

		Collection<String> keys = message.getHeaders().keySet();

		for (String key : keys) {

			if (!key.equals(CONTENT_LENGTH)) {
				headers.put(key, message.getHeaders().get(key));
			}
		}
		Collection<String> headerKeys = messageHeaders.keySet();
		for (String key : headerKeys) {
			headers.put(key, messageHeaders.get(key));
		}
		return new GenericMessage<>(message.getPayload(), headers);
	}

}

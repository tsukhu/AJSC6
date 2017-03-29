/*******************************************************************************
 *   BSD License
 *    
 *   Copyright (c) 2017, AT&T Intellectual Property.  All other rights reserved.
 *    
 *   Redistribution and use in source and binary forms, with or without modification, are permitted
 *   provided that the following conditions are met:
 *    
 *   1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *      and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. All advertising materials mentioning features or use of this software must display the
 *      following acknowledgement:  This product includes software developed by the AT&T.
 *   4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *      promote products derived from this software without specific prior written permission.
 *    
 *   THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 *   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *   MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *   SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 *******************************************************************************/
package com.att.ajsc.common.si.interceptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.att.ajsc.common.InterceptorMessages;
import com.att.ajsc.common.TransactionTrail;
import com.att.ajsc.common.error.PreInterceptorError;
import com.att.ajsc.common.si.interceptors.AjscInterceptor;
import com.att.ajsc.common.si.interceptors.AjscPostInterceptor;
import com.att.ajsc.common.si.interceptors.AjscPreInterceptor;

@Component
public class Interceptor {

	private static final String URL = ".url";
	private static final String STATUSCODE_HEADER = "http_statusCode";
	private static final String TRANSACTION_TRAIL = "transactionTrail";
	private static final String COMMON_CONTEXT = "commonContext";
	private static final String INTERCEPTED_CHANNEL = "intercepted_channel";
	private static final String HOST = "host";
	private static final String HTTP_REQUEST_URL = "http_requestUrl";

	@Autowired
	private Environment environment;
	@Autowired
	private ApplicationContext context;

	private static EELFLogger logger = AjscEelfManager.getInstance().getLogger(Interceptor.class);

	private Message<?> invokeInterceptors(List<AjscInterceptor> interceptors, Message<?> message,
			MessageChannel channel) {

		Message<?> updatedMessage = message;
		Message<?> previousMessage = message;

		if (!interceptors.isEmpty()) {

			if (channel != null) {
				for (AjscInterceptor interceptor : interceptors) {
					try {

						updatedMessage = ((AjscPreInterceptor) interceptor).allowOrReject(updatedMessage, channel);

						if (updatedMessage == null) {							
							logger.error(InterceptorMessages.INTERCEPTOR_FAILED_PRE_INTERCEPTOR,
									interceptor.getClass().toString());
							throw new PreInterceptorError("Invoking Pre Intrceptor Chain failed",
									previousMessage.getHeaders());
						}
					} catch (Exception e) {
						if (e instanceof PreInterceptorError) {
							throw (PreInterceptorError) e;
						}

						logger.error(InterceptorMessages.INTERCEPTOR_ERROR_PRE_INTERCEPTOR, e,
								interceptor.getClass().toString());
						throw new PreInterceptorError("Exception occurred while invoking pre interceptors",
								previousMessage.getHeaders());
					}
					previousMessage = updatedMessage;
				}
			} else {

				for (AjscInterceptor interceptor : interceptors) {
					try {
						message = ((AjscPostInterceptor) interceptor).allowOrReject(message);
						if (message == null) {
							logger.info(InterceptorMessages.INTERCEPTOR_FAILED_POST_INTERCEPTOR,
									interceptor.getClass().toString());		
							break;
						} else {
							updatedMessage = message;
						}
					} catch (Exception e) {
						logger.error(InterceptorMessages.INTERCEPTOR_ERROR_POST_INTERCEPTOR, e,
								interceptor.getClass().toString());
						updatedMessage = updateResponseWithError(message);

						break;
					}
				}

			}

		}

		return updatedMessage;

	}

	private List<AjscInterceptor> filterInterceptors(List<AjscInterceptor> interceptors, String pathInfo,
			Message<?> message) throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> filteredInterceptors = filterByChannel(interceptors, message);

		if (!StringUtils.isEmpty(pathInfo)) {
			for (AjscInterceptor interceptor : interceptors) {
				if (!hasMatch(interceptor.getUri(), pathInfo)) {
					filteredInterceptors.remove(interceptor);
				}
			}
		}
		if (filteredInterceptors.size() == 0) {
			return filteredInterceptors;
		}
		return sortIntereceptors(filteredInterceptors);
	}

	private Message<?> removeHeader(Message<?> message, List<String> headersToRemove) {

		Map<String, Object> headers = new HashMap<String, Object>();

		Collection<String> keys = message.getHeaders().keySet();

		for (String key : keys) {

			if (!headersToRemove.contains(key)) {
				headers.put(key, message.getHeaders().get(key));
			}
		}

		return MessageBuilder.fromMessage(new GenericMessage(message.getPayload(), headers)).build();

	}

	private List<AjscInterceptor> filterByChannel(List<AjscInterceptor> interceptors, Message<?> message) {

		List<AjscInterceptor> filteredInterceptors = new ArrayList<AjscInterceptor>();

		for (AjscInterceptor interceptor : interceptors) {
			String[] channels = interceptor.getChannels().split(",");
			for (int i = 0; i < channels.length; i++) {
				String channel = channels[i];
				if (channel.equals(getChannel(message))) {
					filteredInterceptors.add(interceptor);
				}
			}
		}

		return filteredInterceptors;

	}

	private List<AjscInterceptor> sortIntereceptors(List<AjscInterceptor> list) {

		Collections.sort(list, new Comparator<AjscInterceptor>() {
			public int compare(AjscInterceptor o1, AjscInterceptor o2) {
				return (o1.getPosition() > o2.getPosition() ? 1
						: (o1.getPosition() == o2.getPosition() ? getPriority(o1, o2) : -1));
			}
		});
		return list;
	}

	private int getPriority(AjscInterceptor o1, AjscInterceptor o2) {

		int priority = 0;
		if (o1.getPriority() > o2.getPriority()) {
			priority = 1;
		} else if (o1.getPriority() < o2.getPriority()) {
			priority = -1;
		}
		return priority;
	}

	private boolean hasMatch(String uri, String pathinfo) {

		boolean hasMatch = false;
		PathMatcher pathMatcher = new AntPathMatcher();

		if (!StringUtils.isEmpty(uri)) {
			String[] uris = uri.split(",");
			for (int i = 0; i < uris.length; i++) {
				if (pathMatcher.match(uris[i], pathinfo)) {
					hasMatch = true;
					break;
				}
			}
		}

		return hasMatch;

	}

	private String getActualClass(AjscInterceptor interceptor) {

		String className = null;
		if (interceptor.toString().indexOf("$$") > 0) {
			className = interceptor.toString().substring(0, interceptor.toString().indexOf("$$"));
		} else {
			className = interceptor.toString().substring(0, interceptor.toString().indexOf("@"));
		}
		return className;

	}

	private Message<?> updateResponseWithError(Message<?> message) {
		return MessageBuilder.withPayload(message.getPayload()).copyHeadersIfAbsent(message.getHeaders())
				.setHeader(STATUSCODE_HEADER, HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	public boolean isHttp(Message<?> message) {

		MessageHeaders headers = null;
		if (message.getPayload() instanceof Exception) {
			headers = ((MessagingException) (message.getPayload())).getFailedMessage().getHeaders();
		} else {
			headers = message.getHeaders();
		}

		boolean isHttp = false;
		if (headers.containsKey(HTTP_REQUEST_URL)) {
			isHttp = true;
		}

		return isHttp;

	}

	public String getChannel(Message<?> message) {

		String channel = null;
		if (message.getHeaders().get(INTERCEPTED_CHANNEL) != null) {
			channel = message.getHeaders().get(INTERCEPTED_CHANNEL).toString();
		}
		return channel;
	}

	private String getPath(Message<?> message) {

		String path = "";

		if (isHttp(message)) {
			MessageHeaders headers = null;
			if (message.getPayload() instanceof Exception) {
				headers = ((MessagingException) (message.getPayload())).getFailedMessage().getHeaders();
			} else {
				headers = message.getHeaders();
			}

			String host = headers.get(HOST).toString();
			String url = headers.get(HTTP_REQUEST_URL).toString();
			path = url.substring(url.indexOf(host) + host.length());

		}

		return path;
	}

	public Message<?> invokePreInterceptorChain(Message<?> message, MessageChannel channel) {

		List<AjscInterceptor> preInterceptors = null;
		Message<?> updatedMessage = null;

		String pathInfo = getPath(message);
		List<AjscInterceptor> updatedPreInterceptors = new ArrayList<AjscInterceptor>();
		Map<String, AjscPreInterceptor> preInterceptorMap = context.getBeansOfType(AjscPreInterceptor.class);
		Collection<AjscPreInterceptor> preInterceptor = preInterceptorMap.values();

		if (!StringUtils.isEmpty(pathInfo)) {

			for (AjscPreInterceptor ajscPreInterceptor : preInterceptor) {
				String uri = (String) environment.getProperty(getActualClass(ajscPreInterceptor) + URL);
				if (!StringUtils.isEmpty(uri)) {
					ajscPreInterceptor.setUri(uri);
				}
				updatedPreInterceptors.add(ajscPreInterceptor);
			}
		} else {
			updatedPreInterceptors.addAll(preInterceptor);
		}

		try {
			preInterceptors = filterInterceptors(updatedPreInterceptors, pathInfo, message);
		} catch (Exception e) {			
			logger.error(InterceptorMessages.INTERCEPTOR_ERROR_PRE_INTERCEPTOR, 
					e.getClass().toString());
			return null;
		}
		updatedMessage = invokeInterceptors(preInterceptors, message, channel);

		return updatedMessage;

	}

	public Message<?> invokePostInterceptorChain(Message<?> message)
			throws InstantiationException, IllegalAccessException {

		Message<?> updatedMessage = null;
		List<AjscInterceptor> postInterceptors = null;

		String pathInfo = getPath(message);

		List<AjscInterceptor> updatedPostinterceptors = new ArrayList<AjscInterceptor>();
		Map<String, AjscPostInterceptor> postInterceptorMap = context.getBeansOfType(AjscPostInterceptor.class);

		Collection<AjscPostInterceptor> postInterceptor = postInterceptorMap.values();
		for (AjscPostInterceptor ajscPostInterceptor : postInterceptor) {

			if (!StringUtils.isEmpty(pathInfo)) {
				String uri = (String) environment.getProperty(getActualClass(ajscPostInterceptor) + URL);
				if (!StringUtils.isEmpty(uri)) {
					ajscPostInterceptor.setUri(uri);
				}
			}
			updatedPostinterceptors.add(ajscPostInterceptor);

		}

		postInterceptors = filterInterceptors(updatedPostinterceptors, pathInfo, message);
		updatedMessage = invokeInterceptors(postInterceptors, message, null);
		return updatedMessage;

	}

	public Message<?> endTransaction(Message<?> message) {

		List<String> headersToRemove = new ArrayList<String>();
		headersToRemove.add(INTERCEPTED_CHANNEL);
		headersToRemove.add(TRANSACTION_TRAIL);
		headersToRemove.add(COMMON_CONTEXT);

		TransactionTrail transactionTrail = null;
		if (message.getHeaders().containsKey(TRANSACTION_TRAIL)) {
			transactionTrail = (TransactionTrail) message.getHeaders().get(TRANSACTION_TRAIL);
		} else {
			transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
		}
		message = removeHeader(message, headersToRemove);
		if (transactionTrail != null) {
			transactionTrail.setEndTime(System.currentTimeMillis());
			transactionTrail.setTotalTime(transactionTrail.getEndTime() - transactionTrail.getStartTime());		
			logger.info(InterceptorMessages.INTERCEPTOR_TRAIL_LOGGER_MESSAGE);
		}
		return message;
	}

}

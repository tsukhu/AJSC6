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
package com.att.ajsc.common.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.Interceptor;
import com.att.ajsc.common.RestMessages;
import com.att.ajsc.common.TransactionTrail;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Component
public class TransactionIdResponseFilter implements ContainerResponseFilter, ClientResponseFilter {

	public static final String TRANSACTION_ID_KEY = "X-ATT-Transaction-Id";

	private static final String TRANSACTION_TRAIL = "transactionTrail";

	private static final String INTERCEPTOR_ERROR = "interceptorError";

	private static final String RESPONSE = "response";

	private static final String PROMETHEUS = "prometheus";

	private static final String ERROR = "error";

	private static EELFLogger logger = AjscEelfManager.getInstance().getLogger(TransactionIdResponseFilter.class);

	private static final String[] headersToCopy = { "X-CSI-MethodName", "X-CSI-ServiceName",
			"X-CSI-REST_NAME_NORMALIZED" };

	@Autowired
	private ApplicationContext context;

	@Autowired
	private Environment environment;

	@Autowired
	private Interceptor interceptor;

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	@Value("${spring.mvc.urls}")
	private String urlsTOSkip;

	public void filter(Map<String, List<Object>> requestHeaders, Map<String, List<Object>> responseHeaders)
			throws IOException {
		List<Object> responseTransactionIds = responseHeaders.get(TRANSACTION_ID_KEY);

		if (responseTransactionIds == null) {
			responseTransactionIds = new ArrayList<Object>();
		}

		if (responseTransactionIds.isEmpty()) {
			List<String> requestTransactionIds = (List) requestHeaders.get(TRANSACTION_ID_KEY);
			if (requestTransactionIds != null && !requestTransactionIds.isEmpty()) {
				String transactionId = requestTransactionIds.get(0);

				responseTransactionIds.add(transactionId);
				responseHeaders.put(TRANSACTION_ID_KEY, responseTransactionIds);
			}
		}

		for (int i = 0; i < headersToCopy.length; i++) {
			if (requestHeaders.get(headersToCopy[i]) != null) {
				responseHeaders.put(headersToCopy[i], requestHeaders.get(headersToCopy[i]));
			}
		}

	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		TransactionTrail transactionTrail;

		Map<String, List<Object>> requestHeaders = (Map) requestContext.getHeaders();
		Map<String, List<Object>> responseHeaders = responseContext.getHeaders();

		if ((requestContext.getProperty(INTERCEPTOR_ERROR) != null)
				|| (requestContext.getUriInfo().getPath().equals(PROMETHEUS))
				|| (requestContext.getUriInfo().getPath().equals(ERROR))) {
			return;
		}

		filter(requestHeaders, responseHeaders);
		requestContext.setProperty(RESPONSE, response);
		if (StringUtils.isNotEmpty(urlsTOSkip)) {
			String[] mvcUrls = urlsTOSkip.split(",");
			for (int i = 0; i < mvcUrls.length; i++) {
				if (requestContext.getUriInfo().getPath().startsWith(mvcUrls[i])) {
					return;
				}
			}
		}
		try {
			interceptor.filter(requestContext, responseContext, environment, context);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(RestMessages.REST_POST_INTERCEPTORS_INVOCATION_ERROR, e);
		}
		if (requestContext.getProperty(TRANSACTION_TRAIL) != null) {
			transactionTrail = (TransactionTrail) requestContext.getProperty(TRANSACTION_TRAIL);
			requestContext.setProperty(INTERCEPTOR_ERROR, true);
		} else {
			transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
		}
		transactionTrail.setEndTime(System.currentTimeMillis());
		transactionTrail.setTotalTime(transactionTrail.getEndTime() - transactionTrail.getStartTime());
		logger.info(RestMessages.REST_TRACE_LOG_MESSAGE, transactionTrail.toString());
	}

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		Map<String, List<Object>> requestHeaders = requestContext.getHeaders();
		Map<String, List<Object>> responseHeaders = (Map) responseContext.getHeaders();

		filter(requestHeaders, responseHeaders);
	}

}

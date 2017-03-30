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
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
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
import com.att.ajsc.common.context.CommonContext;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Component
public class TransactionIdRequestFilter implements ContainerRequestFilter, ClientRequestFilter {

	public static final String TRANSACTION_ID_KEY = "X-ATT-Transaction-Id";

	private static final String TRANSACTION_TRAIL = "transactionTrail";

	private static final String COMMON_CONTEXT = "commonContext";

	private static final String REQUEST = "request";

	private static EELFLogger logger = AjscEelfManager.getInstance().getLogger(TransactionIdRequestFilter.class);

	@Context
	private HttpServletRequest servletRequest;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private Environment environment;

	@Autowired
	private Interceptor interceptor;

	@Value("${spring.mvc.urls}")
	private String urlsTOSkip;

	public void filter(Map<String, List<Object>> headers) throws IOException {
		List<Object> transactionIds = headers.get(TRANSACTION_ID_KEY);

		if (transactionIds == null) {
			transactionIds = new ArrayList<Object>();
		}
		if (transactionIds.isEmpty()) {
			String transactionId = java.util.UUID.randomUUID().toString();
			transactionIds.add(transactionId);
			headers.put(TRANSACTION_ID_KEY, transactionIds);
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		CommonContext commonContext;
		TransactionTrail transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
		transactionTrail.setStartTime(System.currentTimeMillis());
		Map<String, List<Object>> headers = (Map) requestContext.getHeaders();

		filter(headers);

		if (requestContext.getProperty(COMMON_CONTEXT) != null) {
			commonContext = (CommonContext) requestContext.getProperty(COMMON_CONTEXT);
		} else {
			commonContext = new CommonContext();
		}
		List<String> requestTransactionIds = (List) headers.get(TRANSACTION_ID_KEY);
		commonContext.setTransactionId(requestTransactionIds.get(0));
		transactionTrail.setTransactionid(requestTransactionIds.get(0));
		requestContext.setProperty(COMMON_CONTEXT, commonContext);
		requestContext.setProperty(REQUEST, servletRequest);
		if (StringUtils.isNotEmpty(urlsTOSkip)) {
			String[] mvcUrls = urlsTOSkip.split(",");
			for (int i = 0; i < mvcUrls.length; i++) {
				if (requestContext.getUriInfo().getPath().startsWith(mvcUrls[i])) {
					return;
				}
			}
		}
		try {
			interceptor.filter(requestContext, environment, context);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(RestMessages.REST_PRE_INTERCEPTORS_INVOCATION_ERROR, e);
		}

	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		Map<String, List<Object>> headers = requestContext.getHeaders();
		filter(headers);
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

}

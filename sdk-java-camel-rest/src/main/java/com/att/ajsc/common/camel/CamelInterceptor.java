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
package com.att.ajsc.common.camel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.Interceptor;
import com.att.ajsc.common.JaxrsMessages;
import com.att.ajsc.common.TransactionTrail;
import com.att.ajsc.common.context.CommonContext;
import com.att.ajsc.common.utility.DateUtility;
import com.att.ajsc.common.utility.EnvironmentUtility;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Component
public class CamelInterceptor implements Processor {

	private static EELFLogger log = AjscEelfManager.getInstance().getLogger(CamelInterceptor.class);

	public static final String TRANSACTION_ID_KEY = "X-ATT-Transaction-Id";

	private static final String TRANSACTION_TRAIL = "transactionTrail";

	private static final String COMMON_CONTEXT = "commonContext";

	@Autowired
	private ApplicationContext context;
	@Autowired
	EnvironmentUtility environmentUtility;
	@Autowired
	private Interceptor interceptor;

	public void invokepreInterceptorChain(Exchange exchange) {

		CommonContext commonContext;
		TransactionTrail transactionTrail;
		boolean isPreInterceptorChainExecuted = true;

		Map<String, Object> headers = exchange.getIn().getHeaders();
		filter(headers);
		initializeMdc(exchange);

		if (interceptor.isForServlet(exchange)) {
			transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
		} else {
			transactionTrail = new TransactionTrail();
			exchange.setProperty(TRANSACTION_TRAIL, transactionTrail);
		}
		transactionTrail.setStartTime(System.currentTimeMillis());

		if (exchange.getIn().getHeader(COMMON_CONTEXT) != null) {
			commonContext = (CommonContext) exchange.getIn().getHeader(COMMON_CONTEXT);
		} else {
			commonContext = new CommonContext();
		}
		List<String> requestTransactionIds = (List) headers.get(TRANSACTION_ID_KEY);
		commonContext.setTransactionId(requestTransactionIds.get(0));
		transactionTrail.setTransactionid(requestTransactionIds.get(0));
		exchange.getIn().setHeader(COMMON_CONTEXT, commonContext);

		try {
			isPreInterceptorChainExecuted = interceptor.invokePreInterceptorChain(exchange);
		} catch (Exception e) {
			log.error(JaxrsMessages.JAXRS_PRE_INTERCEPTORS_INVOCATION_ERROR, e);
			isPreInterceptorChainExecuted = false;
		}
		if (!isPreInterceptorChainExecuted) {
			updateHeader(exchange, true);
			if (interceptor.isForServlet(exchange)) {
				transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
			} else {
				if (exchange.getProperty(TRANSACTION_TRAIL) != null) {
					transactionTrail = (TransactionTrail)exchange.getProperty(TRANSACTION_TRAIL);
				}
			}
			if (transactionTrail != null) {
				updateMdc();
				transactionTrail.setEndTime(System.currentTimeMillis());
				transactionTrail.setTotalTime(transactionTrail.getEndTime() - transactionTrail.getStartTime());
				log.info(JaxrsMessages.JAXRS_TRACE_LOG_MESSAGE, transactionTrail.toString());
			}

			exchange.setProperty(Exchange.ROUTE_STOP, true);
		}
	}

	public void invokepostInterceptorChain(Exchange exchange) {

		TransactionTrail transactionTrail = null;

		try {
			interceptor.invokePostInterceptorChain(exchange);
		} catch (Exception e) {
			log.error(JaxrsMessages.JAXRS_POST_INTERCEPTORS_INVOCATION_ERROR, e);
		}

		updateHeader(exchange, false);
		if (interceptor.isForServlet(exchange)) {
			transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
		} else {
			if (exchange.getProperty(TRANSACTION_TRAIL) != null) {
				transactionTrail = (TransactionTrail) exchange.getProperty(TRANSACTION_TRAIL);
			}
		}
		if (transactionTrail != null) {
			updateMdc();
			transactionTrail.setEndTime(System.currentTimeMillis());
			transactionTrail.setTotalTime(transactionTrail.getEndTime() - transactionTrail.getStartTime());
			log.info(JaxrsMessages.JAXRS_TRACE_LOG_MESSAGE, transactionTrail.toString());
		}

	}

	public void filter(Map<String, Object> headers) {

		List<Object> transactionIds = (List<Object>) headers.get(TRANSACTION_ID_KEY);

		if (transactionIds == null) {
			transactionIds = new ArrayList<Object>();
		}
		if (transactionIds.isEmpty()) {
			String transactionId = java.util.UUID.randomUUID().toString();
			transactionIds.add(transactionId);
			headers.put(TRANSACTION_ID_KEY, transactionIds);
		}
	}

	public void updateHeader(Exchange exchange, boolean preInterceptorChanin) {

		List<Object> transactionIds = (List<Object>) exchange.getIn().getHeader(TRANSACTION_ID_KEY);
		if (preInterceptorChanin) {
			exchange.getOut().setHeader(TRANSACTION_ID_KEY, transactionIds.get(0));
			exchange.getOut().removeHeader(COMMON_CONTEXT);
		} else {
			exchange.getIn().setHeader(TRANSACTION_ID_KEY, transactionIds.get(0));
			exchange.getIn().removeHeader(COMMON_CONTEXT);
		}

	}

	@Override
	public void process(Exchange exchange) throws Exception {
		invokepostInterceptorChain(exchange);
	}

	public void initializeMdc(Exchange exchange) {

		MDC.clear();
		MDC.put("hostname", environmentUtility.getHostName());
		MDC.put("serviceName", environmentUtility.getApplicationName());
		MDC.put("version", environmentUtility.getVersion());
		if (StringUtils.isNotEmpty(System.getProperty("PID"))) {
			MDC.put("PID", System.getProperty("PID"));
		}
		List<String> requestTransactionIds = (List<String>) exchange.getIn().getHeader(TRANSACTION_ID_KEY);
		if (requestTransactionIds != null && !requestTransactionIds.isEmpty()) {
			String transactionId = requestTransactionIds.get(0);
			MDC.put("transactionId", transactionId);
			if (transactionId == null || transactionId.isEmpty()) {
				MDC.put("transactionId", "null/empty");
			}
		} else {
			MDC.put("transactionId", "not found in headers");
		}
		MDC.put("requestTimestamp", DateUtility.nowAsIsoString());

	}

	public void updateMdc() {

		Date responseTimestamp = DateUtility.nowAsDate();
		MDC.put("responseTimestamp", DateUtility.toIsoString(responseTimestamp));
		String requestTimestampString = MDC.get("requestTimestamp");
		Date requestTimestamp = DateUtility.toDate(requestTimestampString);
		Long duration = responseTimestamp.getTime() - requestTimestamp.getTime();
		MDC.put("duration", duration + "");
	}

}

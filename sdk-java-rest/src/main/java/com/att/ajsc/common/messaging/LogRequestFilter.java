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
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.utility.DateUtility;
import com.att.ajsc.common.utility.EnvironmentUtility;

@Component
public class LogRequestFilter implements ContainerRequestFilter, ClientRequestFilter {

	@Autowired
	EnvironmentUtility environmentUtility;

	public void filter(Map<String, List<Object>> headers) throws IOException {
		MDC.clear();
		MDC.put("hostname", environmentUtility.getHostName());
		MDC.put("serviceName", environmentUtility.getApplicationName());
		MDC.put("version", environmentUtility.getVersion());
		if (StringUtils.isNotEmpty(System.getProperty("PID"))) {
			MDC.put("PID", System.getProperty("PID"));
		}
		List<String> requestTransactionIds = (List) headers.get(TransactionIdRequestFilter.TRANSACTION_ID_KEY);
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

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Map<String, List<Object>> headers = (Map) requestContext.getHeaders();
		filter(headers);
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		Map<String, List<Object>> headers = requestContext.getHeaders();
		filter(headers);
	}

}

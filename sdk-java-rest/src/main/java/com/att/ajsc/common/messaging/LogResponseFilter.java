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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.MDC;

import com.att.ajsc.common.utility.DateUtility;

public class LogResponseFilter implements ContainerResponseFilter, ClientResponseFilter {
	
	public void filter(
			Map<String, List<Object>> requestHeaders, 
			Map<String, List<Object>> responseHeaders
			) throws IOException {
		Date responseTimestamp = DateUtility.nowAsDate();
		MDC.put("responseTimestamp", DateUtility.toIsoString(responseTimestamp));
		String requestTimestampString = MDC.get("requestTimestamp");
		Date requestTimestamp = DateUtility.toDate(requestTimestampString);
		Long duration = responseTimestamp.getTime() - requestTimestamp.getTime();
		MDC.put("duration", duration + "");
	}
	
	@Override
	public void filter(
			ContainerRequestContext requestContext, 
			ContainerResponseContext responseContext
			) throws IOException {
		Map<String, List<Object>> requestHeaders = (Map) requestContext.getHeaders();
		Map<String, List<Object>> responseHeaders = responseContext.getHeaders();
		
		filter(requestHeaders, responseHeaders);
	}

	@Override
	public void filter(
			ClientRequestContext requestContext, 
			ClientResponseContext responseContext
			) throws IOException {
		Map<String, List<Object>> requestHeaders = requestContext.getHeaders();
		Map<String, List<Object>> responseHeaders = (Map) responseContext.getHeaders();
		
		filter(requestHeaders, responseHeaders);
		
	}

}

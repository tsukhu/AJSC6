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
package com.att.ajsc.aaf.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.ajsc.aaf.utils.AAFUtils;
import com.att.ajsc.common.AjscPreInterceptor;
import com.att.ajsc.common.InterceptorError;

public class AAFPreInterceptor extends AjscPreInterceptor {
	private static final String REQUEST = "request";
	private static Logger logger = LoggerFactory.getLogger(AAFPreInterceptor.class);

	public AAFPreInterceptor() {
		setPosition(Integer.MIN_VALUE);
	}

	@Override
	public boolean allowOrReject(ContainerRequestContext requestContext) {
		logger.info("Calling AAF Interceptor for Jersey based Archetype");

		HttpServletRequest request = (HttpServletRequest) (requestContext.getProperty(REQUEST));

		boolean authorized = false;

		if (request != null) {

			// get the url from request
			String url = "/" + requestContext.getUriInfo().getPath();

			authorized = AAFUtils.handleRequest(request, url);
			logger.info("permissions verified: " + authorized);

		} else {
			logger.error("The servlet request object was null.");
		}

		// if not authorized- set response code here
		if (!authorized) {
			InterceptorError error = new InterceptorError();
			error.setResponseCode(403);
			error.setResponseMessage("Forbidden!");
			requestContext.setProperty("InterceptorError", error);
		}
		return authorized;
	}

}

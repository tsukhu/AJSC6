package com.att.ajsc.aaf.interceptors;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.ajsc.aaf.utils.AAFUtils;
import com.att.ajsc.common.InterceptorError;
import com.att.ajsc.common.interceptors.AjscPreInterceptor;

public class AAFCamelPreInterceptor extends AjscPreInterceptor {

	private static Logger logger = LoggerFactory.getLogger(AAFCamelPreInterceptor.class);

	public AAFCamelPreInterceptor() {
		setPosition(Integer.MIN_VALUE);
	}

	@Override
	public boolean allowOrReject(Exchange exchange) {
		logger.info("Calling AAF Interceptor for Camel based Archetype");

		HttpServletRequest request = AAFUtils.getRequest(exchange);

		boolean authorized = false;

		if (request != null) {
			// get the url from request
			String url = request.getPathInfo();

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
			exchange.setProperty(INTERCEPTOR_ERROR, error);
		}
		return authorized;

	}

}

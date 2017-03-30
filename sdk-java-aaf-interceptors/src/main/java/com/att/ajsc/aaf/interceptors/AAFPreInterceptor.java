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

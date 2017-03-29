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
package com.att.ajsc.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.att.ajsc.common.restlet.interceptors.AjscInterceptor;
import com.att.ajsc.common.restlet.interceptors.AjscPostInterceptor;
import com.att.ajsc.common.restlet.interceptors.AjscPreInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Component
public class Interceptor {

	private static final String URL = ".url";
	private static final String INTERCEPTOR_ERROR = "InterceptorError";
	private static final String RESTLET_ENDPOINT = "RestletEndpoint";
	private static final String SERVLET_ENDPOINT = "ServletEndpoint";
	private static final String INVALID_REQUEST = "Invalid request. Please verify your request & try once again";
	private static final String PRE_INTERCEPTOR_INTERNAL_SERVER_ERROR = "Internal server Error while invoking pre interceptor chain";
	private static EELFLogger logger = AjscEelfManager.getInstance().getLogger(Interceptor.class);

	@Autowired
	private Environment environment;
	@Autowired
	private ApplicationContext context;

	private boolean invokeInterceptors(List<AjscInterceptor> interceptors, Exchange exchange,
			ApplicationContext context, boolean isPreInterceptorChain) {

		boolean invokeNext = true;

		if (!interceptors.isEmpty()) {

			if (isPreInterceptorChain) {
				for (AjscInterceptor interceptor : interceptors) {
					try {

						invokeNext = ((AjscPreInterceptor) interceptor).allowOrReject(exchange);

						if (!invokeNext) {
							if (isRest(exchange) && exchange.getProperty(INTERCEPTOR_ERROR) != null
									&& exchange.getProperty(INTERCEPTOR_ERROR).getClass().getSimpleName()
											.equals(INTERCEPTOR_ERROR)) {
								InterceptorError error = (InterceptorError) exchange.getProperty(INTERCEPTOR_ERROR);
								updateExchange(exchange, error);
								exchange.removeProperties(INTERCEPTOR_ERROR);
							} else {
								InterceptorError error = new InterceptorError();
								error.setResponseCode(Status.CLIENT_ERROR_FORBIDDEN.getCode());
								error.setResponseMessage(INVALID_REQUEST);
								updateExchange(exchange, error);
							}							
							logger.error(InterceptorMessages.INTERCEPTOR_FAILED_PRE_INTERCEPTOR,
									interceptor.getClass().toString());
							break;
						}
					} catch (Exception e) {

						logger.error(InterceptorMessages.INTERCEPTOR_ERROR_PRE_INTERCEPTOR, e,
								interceptor.getClass().toString());
						clearResponse(exchange, true);
						invokeNext = false;
						break;
					}
				}
			} else {

				for (AjscInterceptor interceptor : interceptors) {
					try {
						invokeNext = ((AjscPostInterceptor) interceptor).allowOrReject(exchange);
						if (!invokeNext) {
							logger.info(InterceptorMessages.INTERCEPTOR_FAILED_POST_INTERCEPTOR,
									interceptor.getClass().toString());	
							break;
						}
					} catch (Exception e) {
						logger.error(InterceptorMessages.INTERCEPTOR_ERROR_POST_INTERCEPTOR, e,
								interceptor.getClass().toString());
						clearResponse(exchange, false);
						invokeNext = false;
						break;
					}
				}

			}

		}

		return invokeNext;

	}

	private List<AjscInterceptor> filterInterceptors(List<AjscInterceptor> interceptors, String pathInfo,
			Exchange exchange) throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> filteredInterceptors = filterByEndpoint(interceptors, exchange);

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

	private List<AjscInterceptor> filterByEndpoint(List<AjscInterceptor> interceptors, Exchange exchange) {

		List<AjscInterceptor> filteredInterceptors = new ArrayList<AjscInterceptor>();

		for (AjscInterceptor interceptor : interceptors) {
			String[] endpoints = interceptor.getFromEndPoint().split(",");
			for (int i = 0; i < endpoints.length; i++) {
				String endpoint = endpoints[i];
				if (endpoint.equals(getFromEndpoint(exchange))) {
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

	public boolean invokePostInterceptorChain(Exchange exchange) throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> postInterceptors = null;
		boolean isSuccess = true;
		String pathInfo = getPath(exchange);

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

		postInterceptors = filterInterceptors(updatedPostinterceptors, pathInfo, exchange);
		isSuccess = invokeInterceptors(postInterceptors, exchange, context, false);
		return isSuccess;

	}

	public boolean invokePreInterceptorChain(Exchange exchange) throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> preInterceptors = null;
		boolean isSuccess = true;

		String pathInfo = getPath(exchange);
		List<AjscInterceptor> updatdPreInterceptors = new ArrayList<AjscInterceptor>();
		Map<String, AjscPreInterceptor> preInterceptorMap = context.getBeansOfType(AjscPreInterceptor.class);
		Collection<AjscPreInterceptor> preInterceptor = preInterceptorMap.values();

		if (!StringUtils.isEmpty(pathInfo)) {

			for (AjscPreInterceptor ajscPreInterceptor : preInterceptor) {
				String uri = (String) environment.getProperty(getActualClass(ajscPreInterceptor) + URL);
				if (!StringUtils.isEmpty(uri)) {
					ajscPreInterceptor.setUri(uri);
				}
				updatdPreInterceptors.add(ajscPreInterceptor);
			}
		} else {
			updatdPreInterceptors.addAll(preInterceptor);
		}

		preInterceptors = filterInterceptors(updatdPreInterceptors, pathInfo, exchange);
		isSuccess = invokeInterceptors(preInterceptors, exchange, context, true);
		return isSuccess;

	}

	private String getFromEndpoint(Exchange exchange) {

		String fromEndpoint = exchange.getFromEndpoint().getClass().getSimpleName();
		return fromEndpoint;
	}

	private String getPath(Exchange exchange) {

		String path = "";

		String fromEndpoint = getFromEndpoint(exchange);
		if (fromEndpoint.equals(RESTLET_ENDPOINT)) {
			Request request = (Request) exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST);
			path = exchange.getIn().getHeader(Exchange.HTTP_URI).toString();
			path = path.substring(request.getHostRef().toString().length());
		}
		return path;
	}

	public boolean isRest(Exchange exchange) {

		boolean returnValue = false;

		String fromEndpoint = getFromEndpoint(exchange);
		if (fromEndpoint.equals(RESTLET_ENDPOINT)) {
			returnValue = true;
		}

		return returnValue;

	}

	public boolean isForServlet(Exchange exchange) {

		boolean returnValue = false;

		String fromEndpoint = getFromEndpoint(exchange);
		if (fromEndpoint.equals(SERVLET_ENDPOINT) || fromEndpoint.equals(RESTLET_ENDPOINT)) {
			returnValue = true;
		}

		return returnValue;

	}

	private void updateExchange(Exchange exchange, InterceptorError error) {

		String fromEndpoint = getFromEndpoint(exchange);

		if (fromEndpoint.equals(RESTLET_ENDPOINT)) {
			org.restlet.Response response = ((org.restlet.Response) exchange.getIn()
					.getHeader(RestletConstants.RESTLET_RESPONSE, org.restlet.Response.class));
			org.restlet.data.Status status = org.restlet.data.Status.valueOf(error.getResponseCode());
			if (status == null) {
				response.setStatus(org.restlet.data.Status.CLIENT_ERROR_FORBIDDEN);
			} else {
				response.setStatus(status);
			}

			String jsonInString = getJSonString(error);
			response.setEntity(jsonInString, MediaType.APPLICATION_JSON);
			exchange.getOut().setBody(response);
		}

	}

	private String getJSonString(InterceptorError error) {

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = "";
		try {
			jsonInString = mapper.writeValueAsString(error);
		} catch (JsonProcessingException e) {
			logger.error(InterceptorMessages.INTERCEPTOR_FAILED_PRE_INTERCEPTOR, 
					error.getClass().toString());
		}
		return jsonInString;

	}

	private void clearResponse(Exchange exchange, boolean fromPreInterceptorChain) {

		InterceptorError error = new InterceptorError();
		error.setResponseCode(Status.SERVER_ERROR_INTERNAL.getCode());

		if (fromPreInterceptorChain) {
			error.setResponseMessage(PRE_INTERCEPTOR_INTERNAL_SERVER_ERROR);
		}
		String jsonString = getJSonString(error);
		String fromEndpoint = getFromEndpoint(exchange);

		if (fromEndpoint.equals(RESTLET_ENDPOINT)) {

			org.restlet.Response response = ((org.restlet.Response) exchange.getIn()
					.getHeader(RestletConstants.RESTLET_RESPONSE, org.restlet.Response.class));
			if (response != null) {
				if (fromPreInterceptorChain) {
					response.setEntity(jsonString, MediaType.APPLICATION_JSON);
				}
				response.setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
				if (fromPreInterceptorChain) {
					exchange.getOut().setBody(response, org.restlet.Response.class);
				}
			}

		}

	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

}

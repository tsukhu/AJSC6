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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Component
public class Interceptor {

	private static final String URL = ".url";
	private static final String TRANSACTION_TRAIL = "transactionTrail";
	private static final String INTERCEPTOR_ERROR = "InterceptorError";
	private static EELFLogger logger = AjscEelfManager.getInstance().getLogger(Interceptor.class);

	private void invokeInterceptors(List<AjscInterceptor> interceptors, ContainerRequestContext requestContext,
			ContainerResponseContext responseContext, ApplicationContext context) {

		boolean isPreInterceptorChain = true;

		if (responseContext != null) {
			isPreInterceptorChain = false;
		}

		if (!interceptors.isEmpty()) {
			if (isPreInterceptorChain) {
				for (AjscInterceptor interceptor : interceptors) {
					try {

						boolean invokeNext = ((AjscPreInterceptor) interceptor).allowOrReject(requestContext);
						if (!invokeNext) {
							if (requestContext.getProperty(INTERCEPTOR_ERROR) != null
									&& requestContext.getProperty(INTERCEPTOR_ERROR).getClass().getSimpleName()
											.equals(INTERCEPTOR_ERROR)) {
								InterceptorError error = (InterceptorError) requestContext
										.getProperty(INTERCEPTOR_ERROR);
								if (Status.fromStatusCode(error.getResponseCode()) == null) {
									requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
								} else {
									requestContext
											.abortWith(Response.status(error.getResponseCode()).entity(error).build());
								}
								requestContext.setProperty(INTERCEPTOR_ERROR, null);
							} else {
								requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
								requestContext.setProperty(TRANSACTION_TRAIL, context.getBean(TRANSACTION_TRAIL));
							}
							requestContext.setProperty(TRANSACTION_TRAIL, context.getBean(TRANSACTION_TRAIL));
							logger.error(InterceptorMessages.INTERCEPTOR_FAILED_PRE_INTERCEPTOR,
									interceptor.getClass().toString());
							break;
						}
					} catch (Exception e) {
						logger.error(InterceptorMessages.INTERCEPTOR_ERROR_PRE_INTERCEPTOR, e,
								interceptor.getClass().toString());
						requestContext.abortWith(Response.status(Status.INTERNAL_SERVER_ERROR).build());
						break;
					}
				}
			} else {

				for (AjscInterceptor interceptor : interceptors) {
					try {
						boolean invokeNext = ((AjscPostInterceptor) interceptor).allowOrReject(requestContext,
								responseContext);
						if (!invokeNext) {
							logger.info(InterceptorMessages.INTERCEPTOR_FAILED_POST_INTERCEPTOR,
									interceptor.getClass().toString());
							break;
						}
					} catch (Exception e) {
						logger.error(InterceptorMessages.INTERCEPTOR_ERROR_POST_INTERCEPTOR, e,
								interceptor.getClass().toString());
						responseContext.setStatus(Status.INTERNAL_SERVER_ERROR.getStatusCode());
						break;
					}
				}

			}

		}

	}

	private List<AjscInterceptor> filterInterceptors(List<AjscInterceptor> interceptors, String pathinfo)
			throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> updtdInterceptors = new ArrayList<AjscInterceptor>();

		for (AjscInterceptor interceptor : interceptors) {
			if (hasMatch(interceptor.getUri(), pathinfo) && !updtdInterceptors.contains(interceptor)) {
				updtdInterceptors.add(interceptor);
			}
		}

		return sortIntereceptors(updtdInterceptors);
	}

	private List<AjscInterceptor> sortIntereceptors(List<AjscInterceptor> list) {

		Collections.sort(list, new Comparator<AjscInterceptor>() {
			public int compare(AjscInterceptor o1, AjscInterceptor o2) {
				return (o1.getPosition() > o2.getPosition() ? 1 : (o1.getPosition() == o2.getPosition() ? 0 : -1));
			}
		});
		return list;
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

	public void filter(ContainerRequestContext requestContext, Environment environment, ApplicationContext context)
			throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> preInterceptors = null;

		String pathInfo = requestContext.getUriInfo().getAbsolutePath().getPath();

		List<AjscInterceptor> updatdPreInterceptors = new ArrayList<AjscInterceptor>();
		Map<String, AjscPreInterceptor> preInterceptorMap = context.getBeansOfType(AjscPreInterceptor.class);
		Collection<AjscPreInterceptor> preInterceptor = preInterceptorMap.values();

		for (AjscPreInterceptor ajscPreInterceptor : preInterceptor) {

			String uri = (String) environment.getProperty(getActualClass(ajscPreInterceptor) + URL);
			if (!StringUtils.isEmpty(uri)) {
				ajscPreInterceptor.setUri(uri);
			}
			updatdPreInterceptors.add(ajscPreInterceptor);
		}

		preInterceptors = filterInterceptors(updatdPreInterceptors, pathInfo);
		invokeInterceptors(preInterceptors, requestContext, null, context);

	}

	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext,
			Environment environment, ApplicationContext context) throws InstantiationException, IllegalAccessException {

		List<AjscInterceptor> postInterceptors = null;

		String pathInfo = requestContext.getUriInfo().getAbsolutePath().getPath();

		List<AjscInterceptor> updatedPostinterceptors = new ArrayList<AjscInterceptor>();
		Map<String, AjscPostInterceptor> postInterceptorMap = context.getBeansOfType(AjscPostInterceptor.class);

		Collection<AjscPostInterceptor> postInterceptor = postInterceptorMap.values();
		for (AjscPostInterceptor ajscPostInterceptor : postInterceptor) {

			String uri = (String) environment.getProperty(getActualClass(ajscPostInterceptor) + URL);
			if (!StringUtils.isEmpty(uri)) {
				ajscPostInterceptor.setUri(uri);
			}
			updatedPostinterceptors.add(ajscPostInterceptor);

		}

		postInterceptors = filterInterceptors(updatedPostinterceptors, pathInfo);
		invokeInterceptors(postInterceptors, requestContext, responseContext, context);

	}

}

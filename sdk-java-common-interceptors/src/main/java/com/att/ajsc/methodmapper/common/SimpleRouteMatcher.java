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

package com.att.ajsc.methodmapper.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRouteMatcher implements RouteMatcher {

	private static Logger logger = LoggerFactory.getLogger(SimpleRouteMatcher.class);

	private List<RouteEntry> routes;

	public List<RouteEntry> getRoutes() {
		return routes;
	}

	private static class RouteEntry {

		private String service;
		private HttpMethod httpMethod;
		private String path;
		private String logicalMethod;
		private String type;

		private String serviceName;

		public final static int NO_MATCH = -1;
		private final static int PERFECT_MATCH = 2;
		private final static int PARAM_MATCH = 1;

		/*
		 * For a wildcard method match - clone with the incoming method to
		 * match.
		 */
		private RouteEntry cloneWithHttpMethod(HttpMethod method) {
			RouteEntry re = new RouteEntry();
			re.service = this.service;
			re.path = this.path;
			re.logicalMethod = this.logicalMethod;
			re.httpMethod = method;

			return re;
		}

		private int matches(HttpMethod httpMethod, String path) {
			int match = 0;
			/*
			 * No special scoring for starstar. Don't define the same URI with a
			 * wildcard with a star and with an http method
			 */
			if (this.httpMethod == httpMethod) {
				match = matchPath(path);
			}
			return match;
		}

		private int matchPath(String path) { // NOSONAR
			if (!this.path.endsWith("*") && ((path.endsWith("/") && !this.path.endsWith("/")) // NOSONAR
					|| (this.path.endsWith("/") && !path.endsWith("/")))) {
				// One and not both ends with slash
				return NO_MATCH;
			}
			if (this.path.equals(path)) {
				// Paths are the same
				return Integer.MAX_VALUE;
			}

			// check params
			List<String> thisPathList = SparkUtils.convertRouteToList(this.path);
			List<String> pathList = SparkUtils.convertRouteToList(path);
			int matchStrength = 0;

			int thisPathSize = thisPathList.size();
			int pathSize = pathList.size();

			if (thisPathSize == pathSize) {
				for (int i = 0; i < thisPathSize; i++) {
					String thisPathPart = thisPathList.get(i);
					String pathPart = pathList.get(i);

					if ((i == thisPathSize - 1) && (thisPathPart.equals("*") && this.path.endsWith("*"))) {
						// wildcard match
						return matchStrength;
					}

					if ((!SparkUtils.isParam(thisPathPart)) && !thisPathPart.equals(pathPart)
							&& !thisPathPart.equals("*")) {
						return NO_MATCH;
					}

					if (thisPathPart.equals(pathPart))
						matchStrength += PERFECT_MATCH;

					if (SparkUtils.isParam(thisPathPart))
						matchStrength += PARAM_MATCH;
				}
				// All parts matched
				return matchStrength;
			} else {
				// Number of "path parts" not the same
				// check wild card:
				if (this.path.endsWith("*")) {
					if (pathSize == (thisPathSize - 1) && (path.endsWith("/"))) {
						// Hack for making wildcards work with trailing slash
						pathList.add("");
						pathList.add("");
						pathSize += 2;
					}

					if (thisPathSize < pathSize) {
						for (int i = 0; i < thisPathSize; i++) {
							String thisPathPart = thisPathList.get(i);
							String pathPart = pathList.get(i);
							if (thisPathPart.equals("*") && (i == thisPathSize - 1) && this.path.endsWith("*")) {
								// wildcard match
								return matchStrength;
							}
							if (!SparkUtils.isParam(thisPathPart) && !thisPathPart.equals(pathPart)
									&& !thisPathPart.equals("*")) {
								return NO_MATCH;
							}
							if (thisPathPart.equals(pathPart))
								matchStrength += PERFECT_MATCH;

							if (SparkUtils.isParam(thisPathPart))
								matchStrength += PARAM_MATCH;
						}
						// All parts matched
						return matchStrength;
					}
					// End check wild card
				}
				return NO_MATCH;
			}
		}

		public String toString() {
			return httpMethod.name() + ", " + path + ", " + logicalMethod;
		}

		public String getServiceName() {
			return serviceName;
		}

		public String getType() {
			return type;
		}

	}

	public SimpleRouteMatcher() {
		routes = new ArrayList<RouteEntry>();
	}

	@Override
	public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path) {
		int matchStrength = RouteEntry.NO_MATCH;
		RouteEntry targetRoute = null;
		for (RouteEntry entry : routes) {
			RouteEntry entryToMatch = entry;
			if (HttpMethod.starstar.equals(entry.httpMethod))
				entryToMatch = entry.cloneWithHttpMethod(httpMethod);
			int nmatch = entryToMatch.matches(httpMethod, path);
			if (nmatch > matchStrength && (entry.httpMethod.name().equalsIgnoreCase(httpMethod.name())
					|| HttpMethod.starstar.equals(entry.httpMethod))) {
				targetRoute = entry;
				matchStrength = nmatch;
			}
		}
		if (targetRoute != null) {
			return new RouteMatch(targetRoute.service, targetRoute.httpMethod, targetRoute.logicalMethod,
					targetRoute.path, path, matchStrength);
		} else {
			return null;
		}
	}

	@Override
	public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path) {
		int matchStrength = RouteEntry.NO_MATCH;
		List<RouteMatch> matchSet = new ArrayList<RouteMatch>();
		for (RouteEntry entry : routes) {
			if ((matchStrength = entry.matches(httpMethod, path)) > RouteEntry.NO_MATCH) {
				matchSet.add(new RouteMatch(entry.service, httpMethod, entry.logicalMethod, entry.path, path,
						matchStrength));
			}
		}
		return matchSet;
	}

	@Override
	public void parseValidateAddRoute(String service, String httpMethod, String url, String logicalMethod, String type,
			String serviceName) {
		try {
			HttpMethod method;
			try {
				if ("*".equals(httpMethod))
					method = HttpMethod.starstar;
				else
					method = HttpMethod.valueOf(httpMethod);
			} catch (IllegalArgumentException e) {
				logger.error("The @Route value: " + url + " has an invalid HTTP method part: " + httpMethod + ".", e);
				return;
			}
			addRoute(service, method, url, logicalMethod, type, serviceName);
		} catch (Exception e) {
			logger.error("The @Route value: " + url + " is not in the correct format", e);
		}

	}

	private void addRoute(String service, HttpMethod method, String url, String logicalMethod, String type,
			String serviceName) {
		RouteEntry entry = new RouteEntry();
		entry.service = service;
		entry.httpMethod = method;
		entry.path = url;
		entry.logicalMethod = logicalMethod;
		entry.serviceName = serviceName;
		// Adds to end of list
		routes.add(entry);
	}

	@Override
	public void clearRoutes() {
		routes.clear();
	}

}

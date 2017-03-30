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
package com.att.ajsc.csilogging.util;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlCalendar;
import org.restlet.Request;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.adapter.HttpResponse;
import org.restlet.ext.servlet.internal.ServletCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

public class UtilLib {
	final static Logger logger = LoggerFactory.getLogger(UtilLib.class);
	private static final String OPEN_PARENTHESIS = "(";
	public static final String CLOSE_PARENTHESIS = ")";
	public static final String SEMICOLON = ";";
	public static final String REST_NAME_NORMALIZED_NO_PATTERN_MATCH = "NO MATCH";

	public static boolean isNullOrEmpty(String s) {
		return (s == null || s.trim().isEmpty());
	}

	public static String ifNullThenEmpty(String s) {
		return (isNullOrEmpty(s) ? "" : s);
	}

	public static String jsonpWrapper(String jsonpFunction, String json) {
		String jsonWrapperResponse = json;
		if (jsonpFunction != null && !jsonpFunction.trim().isEmpty() && json != null && !json.trim().isEmpty()) {
			jsonWrapperResponse = jsonpFunction.trim() + OPEN_PARENTHESIS + json + CLOSE_PARENTHESIS + SEMICOLON;
		}
		return jsonWrapperResponse;
	}

	public static String uCaseFirstLetter(String s) {
		String ns = null;
		if (s != null && !s.isEmpty()) {
			String firstLetter = s.substring(0, 1).toUpperCase();
			String remainingString = s.substring(1);
			ns = firstLetter + remainingString;
		}
		return ns;
	}

	public static XMLGregorianCalendar epochToXmlGC(long epoch) {
		try {
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			GregorianCalendar gcal = new GregorianCalendar();
			gcal.setTimeInMillis(epoch);
			gcal.setTimeZone(TimeZone.getTimeZone("Z"));
			XMLGregorianCalendar xgc = dtf.newXMLGregorianCalendar(gcal);
			return xgc;
		} catch (Exception e) {
			// Do nothing!!! - return a null;
		}
		return null;
	}

	public static String getClientApp() {
		return "ajsc-csi-restful~" + SystemParams.instance().getAppName();
	}

	public static String dme2UriToLocaldme2Uri(String dme2Uri, String version, String envContext, String routeOffer) {
		boolean serviceNameFound = false;
		String uriParts[] = ifNullThenEmpty(dme2Uri).split("/");
		String serviceName = "";
		for (String uriPart : uriParts) {
			if (ifNullThenEmpty(uriPart).startsWith("service=")) {
				serviceNameFound = true;
				serviceName = uriPart.substring("service=".length());
				break;
			}
		}

		String newDme2Uri = dme2Uri;
		if (serviceNameFound) {
			newDme2Uri = "dme2://DME2LOCAL" + "/service=" + serviceName + "/version=" + version + "/envContext="
					+ envContext + "/routeOffer=" + routeOffer;
		}
		return newDme2Uri;
	}

	public static String getErrorResponseBodyType(String acceptHeader) {
		try {
			boolean acceptJson = false;
			boolean acceptXml = false;

			String acceptValues[] = ifNullThenEmpty(acceptHeader).split(",");
			for (String acceptValue : acceptValues) {
				String splitValue[] = ifNullThenEmpty(acceptValue).split(";");
				if (splitValue != null && splitValue.length > 0 && ifNullThenEmpty(splitValue[0]).length() > 0) {
					if (splitValue[0].endsWith("/json"))
						acceptJson = true;
					else if (splitValue[0].endsWith("/xml"))
						acceptXml = true;
					else if (splitValue[0].equals("*/*")) {
						acceptJson = true;
						acceptXml = true;
					}
				}
			}

			if (acceptJson)
				return CommonNames.BODY_TYPE_JSON;
			else if (acceptXml)
				return CommonNames.BODY_TYPE_XML;
		} catch (Exception e) {
			// Do nothing - just use Json
		}
		return CommonNames.BODY_TYPE_JSON;
	}

	public static String getStartTimestamp(String epoch) {
		long stime = Long.parseLong((String) epoch);
		XmlCalendar cal = new XmlCalendar(new Date(stime));
		XMLGregorianCalendar initTime = null;
		try {
			initTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND),
					Math.round(cal.get(Calendar.ZONE_OFFSET) / 1000 / 60));
		} catch (Exception ex) {
			initTime = null;
		}
		if (initTime == null)
			return null;
		else
			return initTime.toString();
	}

	public static String getServiceName(String servletPath, String pathInfo) {

		String servicename = "N/A";
		try {
			String serviceType = servletPath.replaceAll("\\/", "");
			String SERVLET_URL_PATTERN = System.getProperty("APP_SERVLET_URL_PATTERN").replaceAll("\\/", "");
			String RESTLET_URL_PATTERN = System.getProperty("APP_RESTLET_URL_PATTERN").replaceAll("\\/", "");
			String componentType = "";
			String namespace = System.getProperty(CommonNames.SOACLOUD_NAMESPACE);

			String pathinfoArr[] = pathInfo.split("\\/");
			int arrLength = pathinfoArr.length;
			String input = "";
			if (serviceType.equalsIgnoreCase("services") || serviceType.equalsIgnoreCase(SERVLET_URL_PATTERN)) {
				componentType = CommonNames.COMPONENT_TYPE_SERVLET;
				input = getInput(pathinfoArr, arrLength, componentType, pathInfo);

				servicename = concatenateName(namespace, input);
			} else if (serviceType.equalsIgnoreCase("rest") || serviceType.equalsIgnoreCase(RESTLET_URL_PATTERN)) {
				componentType = CommonNames.COMPONENT_TYPE_RESTLET;
				input = getInput(pathinfoArr, arrLength, componentType, pathInfo);
				servicename = concatenateName(namespace, input);
			}

		} catch (Exception e) {

			logger.error(e.getMessage());

		}

		return servicename;
	}

	public static Boolean compareValues(String fromURL, String fromGRM) {

		if (fromURL.equals(fromGRM) || fromGRM.startsWith("{") || fromGRM.startsWith("[")) {
			return true;
		} else
			return false;
	}

	public static String getInput(String pathinfoArr[], int arrLength, String componentType, String pathInfo) {
		Set<String> endpointSet = null;
		/*
		 * if (componentType.equalsIgnoreCase("rest")) { endpointSet =
		 * DME2Helper.restletEndpointSet; } else { endpointSet =
		 * DME2Helper.serviceEndpointSet; }
		 */
		HashSet<String> setBasedArrLenth = new HashSet<String>();
		HashMap setBasedCharMap = new HashMap();
		HashSet<String> setBasedValues = new HashSet<String>();
		AntPathMatcher pathMatcher = new AntPathMatcher();

		String inputBasedonLength[];
		int globalvalue = 0;
		for (String s : endpointSet) {
			int dif = StringUtils.getLevenshteinDistance(pathInfo, s);

			if (globalvalue == 0 || globalvalue > dif) {
				globalvalue = dif;
				setBasedCharMap.put(globalvalue, s);
			}

			inputBasedonLength = s.split("\\/");
			int i = inputBasedonLength.length;
			if (arrLength == i) {
				setBasedArrLenth.add(s);
			}
		}

		String inputBasedOnValues[];
		for (String s1 : setBasedArrLenth) {
			inputBasedOnValues = s1.split("\\/");

			int j = 1;
			while (compareValues(pathinfoArr[j], inputBasedOnValues[j])) {
				j++;
				if (j >= arrLength) {
					break;
				}
			}
			if (j == arrLength) {
				setBasedValues.add(s1);
			}
		}
		String input = "";

		if (setBasedValues.size() == 1) {
			for (String s2 : setBasedValues) {
				input = s2;
			}
		} else {
			for (String s2 : setBasedValues) {
				if (pathMatcher.match(pathInfo, s2)) {
					input = s2;
				}
			}
		}
		if (input.isEmpty()) {
			input = (String) setBasedCharMap.get(globalvalue);

		}
		return "/" + componentType + input;
	}

	public static String concatenateName(String namespace, String input) {

		String serviceName = "";
		if (System.getProperty("AJSC_SERVICE_NAMESPACE") != null
				&& System.getProperty("AJSC_SERVICE_VERSION") != null) {
			serviceName = namespace + "." + System.getProperty("AJSC_SERVICE_NAMESPACE") + "-"
					+ System.getProperty("AJSC_SERVICE_VERSION") + input;
		} else {
			serviceName = namespace + input;
		}
		return serviceName;
	}

	public static String getServiceName(HttpServletRequest request) {
		String serviceName = "N/A";

		if (request.getAttribute(CommonNames.REST_NAME_HEADER) != null) {
			serviceName = request.getAttribute(CommonNames.REST_NAME_HEADER).toString();
		} else {

			String service = null;
			String pathinfo[] = request.getRequestURI().split("\\/");
			for (int i = 1; i < pathinfo.length; i++) {
				if (service == null) {
					service = pathinfo[i];
				} else {
					service = service + "." + pathinfo[i];
				}

			}
			serviceName = service;

		}
		return serviceName;
	}

	public static HttpServletRequest getRequest(Exchange exchange, String endPoint) {

		HttpServletRequest request = null;

		if (endPoint.equals(CommonNames.SERVLET_ENDPOINT)
				&& exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class) != null) {
			request = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
		} else if (endPoint.equals(CommonNames.RESTLET_ENDPOINT)
				&& exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class) != null) {
			HttpRequest httpRequest = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class);
			request = ((ServletCall) ((HttpRequest) httpRequest).getHttpCall()).getRequest();
		}
		return request;
	}

	public static HttpServletResponse getResponse(Exchange exchange, String endPoint) {

		HttpServletResponse response = null;

		if (endPoint.equals(CommonNames.SERVLET_ENDPOINT)
				&& exchange.getIn().getHeader(Exchange.HTTP_SERVLET_RESPONSE, HttpServletResponse.class) != null) {
			response = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_RESPONSE, HttpServletResponse.class);
		} else if (endPoint.equals(CommonNames.RESTLET_ENDPOINT)
				&& exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, HttpResponse.class) != null) {
			HttpResponse httpResponse = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE,
					HttpResponse.class);
			response = ((ServletCall) ((HttpResponse) httpResponse).getHttpCall()).getResponse();

		}
		return response;
	}

	public static Long getResponseLength(Exchange exchange) {

		long length = -1;
		try {
			length = ((ByteArrayInputStream) (exchange.getIn().getBody())).available();
		} catch (Exception e) {
			logger.error("Error while calcultaing the response length" + e);
		}
		return length;

	}

	public static Object getResponseEntity(Exchange exchange) {

		Object responseEntity = null;
		org.restlet.Response response = ((org.restlet.Response) exchange.getIn()
				.getHeader(RestletConstants.RESTLET_RESPONSE, org.restlet.Response.class));
		if (response != null && response.getEntity() != null) {
			responseEntity = response.getEntity();
		} else {
			responseEntity = exchange.getIn().getBody();
		}
		return responseEntity;

	}

	public static String getPath(Exchange exchange, String endPoint) {

		String path = "";

		if (endPoint.equals(CommonNames.SERVLET_ENDPOINT)) {
			path = exchange.getIn().getHeader(Exchange.HTTP_URI).toString();
		} else if (endPoint.equals(CommonNames.RESTLET_ENDPOINT)) {
			Request request = (Request) exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST);
			path = exchange.getIn().getHeader(Exchange.HTTP_URI).toString();
			path = path.substring(request.getHostRef().toString().length());
		}
		return path;
	}

	public static String getMethod(Exchange exchange) {

		String method = "";

		String fromEndpoint = getFromEndpoint(exchange);
		if (fromEndpoint.equals(CommonNames.SERVLET_ENDPOINT) || fromEndpoint.equals(CommonNames.RESTLET_ENDPOINT)) {
			method = exchange.getIn().getHeader(Exchange.HTTP_METHOD).toString();
		}
		return method;
	}

	public static String getFromEndpoint(Exchange exchange) {

		String fromEndpoint = exchange.getFromEndpoint().getClass().getSimpleName();
		return fromEndpoint;
	}

}

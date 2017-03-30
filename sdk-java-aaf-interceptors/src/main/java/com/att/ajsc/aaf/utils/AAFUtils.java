package com.att.ajsc.aaf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.ext.servlet.internal.ServletCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.att.aft.dme2.internal.apache.commons.lang.StringUtils;

public class AAFUtils {
	public static final String SERVLET_ENDPOINT = "ServletEndpoint";
	public static final String RESTLET_ENDPOINT = "RestletEndpoint";

	public static final String AAF_USER_ROLES_CONFIG_FILE = "etc/aaf/AAFUserRoles.properties";

	private static Logger logger = LoggerFactory.getLogger(AAFUtils.class);

	public static HttpServletRequest getRequest(Exchange exchange) {

		String endPoint = AAFUtils.getFromEndpoint(exchange);
		HttpServletRequest request = null;

		if (endPoint.equals(SERVLET_ENDPOINT)
				&& exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class) != null) {
			request = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
		} else if (endPoint.equals(RESTLET_ENDPOINT)
				&& exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class) != null) {
			HttpRequest httpRequest = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class);
			request = ((ServletCall) ((HttpRequest) httpRequest).getHttpCall()).getRequest();
		}
		return request;
	}

	public static String getFromEndpoint(Exchange exchange) {

		String fromEndpoint = exchange.getFromEndpoint().getClass().getSimpleName();
		return fromEndpoint;
	}

	public static boolean verifyRoles(ArrayList<String> aafRoles, HttpServletRequest hReq) {
		boolean allPermsAuthorized = true;
		if (!aafRoles.isEmpty()) {

			for (String aafRole : aafRoles) {
				boolean calledAAF = false;
				try {
					boolean permAuthorized = true;
					String permission = aafRole.substring(0, aafRole.indexOf(" ")).trim();
					String method = aafRole.substring(aafRole.indexOf(" ") + 1).trim();

					// debug statements
					logger.debug("permission : " + permission);
					logger.debug("method : " + method);
					logger.debug("requestMethod : " + hReq.getMethod());
					logger.debug("user : " + hReq.getRemoteUser());

					if (method.equalsIgnoreCase(hReq.getMethod()) || method.equalsIgnoreCase("ALL")) {
						permAuthorized = hReq.isUserInRole(permission.trim());
						calledAAF = true;
					}

					if (calledAAF) {
						if (permAuthorized) {
							logger.info(
									"User authorized by AAF for permission " + permission + " with method " + method);
						} else {
							logger.info("User not authorized by AAF for permission " + permission + " with method "
									+ method + "; BREAKING!");
							allPermsAuthorized = false;
							break;
						}
					} else {
						logger.info(
								"User authorization by AAF not required for " + permission + " with method " + method);
					}
				} catch (Exception e) {
					logger.error("Error in validating the permission" + e.getLocalizedMessage());
				}
			}
		}
		return allPermsAuthorized;
	}

	public static ArrayList<String> urlMappingResolver(Map<String, String> mappings, String pathinfo) {

		// **=com.att.ajsc.access|*|* ALL

		ArrayList<String> roles = new ArrayList<String>();

		PathMatcher pathMatcher = new AntPathMatcher();

		for (Map.Entry<String, String> entry : mappings.entrySet()) {
			String key = entry.getKey();

			String value = entry.getValue();

			String[] valueArray = value.split(",");

			if (pathMatcher.match(key, pathinfo)) {

				for (String val : valueArray) {
					if (!roles.contains(val)) {
						roles.add(val);
					}
				}
			}
		}

		return roles;
	}

	public static Map<String, String> getProperties(String filePath) throws Exception {

		File rolesFile = new File(filePath);
		HashMap<String, String> propMap = null;

		try {
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(rolesFile);
			prop.load(fis);

			propMap = new HashMap<String, String>((Map) prop);

			/*
			 * for(String key : prop.stringPropertyNames()) {
			 * System.setProperty(key, (String) prop.get(key)); }
			 */
			fis.close();

		} catch (Exception e) {
			logger.error("File " + (rolesFile != null ? rolesFile.getName() : "") + " cannot be loaded into the map ",
					e);
			throw new Exception("Error reading map file " + (rolesFile != null ? rolesFile.getName() : ""), e);
		}

		return propMap;
	}

	public static boolean handleRequest(HttpServletRequest request, String url) {

		if (StringUtils.isNotBlank(url)) {
			Map<String, String> aafUserRolesMap = null;
			try {
				// get the maps between url and permissions
				aafUserRolesMap = AAFUtils.getProperties(AAFUtils.AAF_USER_ROLES_CONFIG_FILE);
			} catch (Exception e) {
				logger.error("An error occured while processing the AAF User Roles properties", e);
			}

			// filter out the permissions that don't match with url
			ArrayList<String> aafRoles = AAFUtils.urlMappingResolver(aafUserRolesMap, url);

			return AAFUtils.verifyRoles(aafRoles, request);
		} else {
			logger.error("URL retrieved from service request was blank.");
			return false;
		}

	}

}

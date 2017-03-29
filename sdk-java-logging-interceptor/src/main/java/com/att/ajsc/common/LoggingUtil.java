package com.att.ajsc.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.ext.servlet.internal.ServletCall;

import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

public class LoggingUtil {

	final static EELFLogger logger = AjscEelfManager.getInstance().getLogger(LoggingUtil.class);

	public static final String MOCK_USER_NAME = "ajscUser";

	public static String getUserName(String authHeader) {

		String userNme = MOCK_USER_NAME;

		if (authHeader != null) {
			String[] split = authHeader.split("\\s+");
			if (split.length > 0) {
				String basic = split[0];

				if (basic.equalsIgnoreCase("Basic")) {
					String credentials = split[1];
					String userPass = new String(Base64.getDecoder().decode(credentials));
					int p = userPass.indexOf(":");
					if (p != -1) {
						userNme = userPass.substring(0, p);

					}
				}
			}
		}
		return userNme;
	}

	public static String getServiceName(Exchange exchange) {

		HttpServletRequest request = null;
		String serviceName = exchange.getIn().getHeader(Exchange.HTTP_URI).toString();
		if (exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class) != null) {
			request = exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
		} else if (exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class) != null) {
			HttpRequest httpRequest = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class);
			request = ((ServletCall) ((HttpRequest) httpRequest).getHttpCall()).getRequest();
		}

		if (request!=null) {

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

	public static Long getResponseLength(Exchange exchange) {

		long length = -1;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(exchange.getIn().getBody());
		} catch (IOException e) {
			logger.error("error while calcultaing the response length" + e);
		}

		length = out.size();
		return length;
	}

	public static Long getLength(Exchange exchange) {

		long length = -1;
		try {
			length = ((ByteArrayInputStream) (exchange.getIn().getBody())).available();
		} catch (Exception e) {
			logger.error("Error while calcultaing the response length" + e);
		}
		return length;

	}

}

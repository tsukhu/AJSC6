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
package com.att.ajsc.csilogging.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.InterceptorError;
import com.att.ajsc.common.utility.EnvironmentUtility;
import com.att.ajsc.csi.logging.AuditRecord;
import com.att.ajsc.csi.logging.PerformanceTracking;
import com.att.ajsc.csi.logging.PerformanceTrackingBean;
import com.att.ajsc.csilogging.util.CommonErrors;
import com.att.ajsc.csilogging.util.CommonNames;
import com.att.ajsc.csilogging.util.GUIDHelper;
import com.att.ajsc.csilogging.util.LoggerNameConverter;
import com.att.ajsc.csilogging.util.SystemParams;
import com.att.ajsc.csilogging.util.UtilLib;

@Component
public class CSILoggingUtils {

	final static Logger logger = LoggerFactory.getLogger(CSILoggingUtils.class);
	public static final String INTERCEPTOR_ERROR = "InterceptorError";

	public static final String PERFORMANCE_TRACKER_BEAN = "PERFORMANCE_TRACKER_BEAN";

	private void internalHandleRequest(HttpServletRequest request) {

		logger.debug("In...:handleRequest");

		// Instantiate PeformanceTracking bean, populate it with required
		// request headers and set it eventually as a request attribute

		PerformanceTrackingBean perfTrackerBean = new PerformanceTrackingBean();
		perfTrackerBean.setStartTime(request.getAttribute(CommonNames.START_TIME).toString());

		String client_App = request.getHeader(CommonNames.CSI_CLIENT_APP);
		if (client_App != null && !client_App.isEmpty() && (client_App.startsWith("ServiceGateway~")
				|| client_App.startsWith("M2E~") || client_App.startsWith("AJSC-CSI~"))) {
			request.setAttribute(CommonNames.CALL_TYPE, "GATEWAY");
			perfTrackerBean.setCallType("GATEWAY");

		} else {
			if (System.getProperty("directInvocationEnable") != null
					&& System.getProperty("directInvocationEnable").equalsIgnoreCase("true")) {
				request.setAttribute(CommonNames.CALL_TYPE, "DIRECT");
				perfTrackerBean.setCallType("DIRECT");
			}
		}

		if ("DIRECT".equals(perfTrackerBean.getCallType())) {

			String dme2ReadTimeOut = request.getHeader("AFT_DME2_EP_READ_TIMEOUT_MS");
			String timeToLive = request.getHeader("X-CSI-TimeToLive");

			if (dme2ReadTimeOut != null && timeToLive != null) {
				Long dme2ReadTimeOutLong = new Long(dme2ReadTimeOut);
				Long timeToLiveLong = new Long(timeToLive);
				if (dme2ReadTimeOutLong < timeToLiveLong) {
					request.setAttribute(CommonNames.SEND_400, true);
					logger.info("dme2 read time out is less then the time to live, so suspending the transaction");
					return;
				}
			}
		}
		setRequestAttribute(request, CommonNames.CSI_VERSION, SystemParams.instance().getAppVersion());

		setRequestAttribute(request, CommonNames.CSI_ORIGINAL_VERSION, SystemParams.instance().getAppVersion());

		/*
		 * String version = (String) request.getHeader(CommonNames.CSI_VERSION);
		 * 
		 * if (version == null || version.isEmpty()) {
		 * logger.debug("X-CSI-Version is null or empty"); version = "N/A"; //
		 * resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); } else { int i =
		 * version.indexOf('.'); if (i > 0) version = version.substring(0, i); }
		 */
		// request.setAttribute(CommonNames.CSI_VERSION, "v" + version);
		perfTrackerBean.setOriginationSystemVersion((String) request.getAttribute(CommonNames.CSI_VERSION));
		perfTrackerBean.setAuthorization((String) request.getHeader(CommonNames.HTTP_AUTHORIZATION));

		String userName = (String) request.getAttribute(CommonNames.CSI_USER_NAME);
		String convId = GUIDHelper.createCSIConversationId(userName);

		String uniqueTransactionId = null;

		setRequestAttribute(request, CommonNames.CSI_UNIQUE_TXN_ID, null);

		if (request.getAttribute(CommonNames.CSI_UNIQUE_TXN_ID) == null) {
			uniqueTransactionId = GUIDHelper.createUniqueTransactionId();

			setRequestAttribute(request, CommonNames.CSI_UNIQUE_TXN_ID, uniqueTransactionId);

		} else {
			uniqueTransactionId = (String) request.getAttribute(CommonNames.CSI_UNIQUE_TXN_ID);
		}

		perfTrackerBean.setUserName(userName);
		// setting userId same as userName so that downstream framework
		// component such as Invoke Adapter, Invoke Service, etc. can access the
		// user id without getting a null value
		perfTrackerBean.setUserID(userName);
		setRequestAttribute(request, CommonNames.CSI_CONVERSATION_ID, convId);
		perfTrackerBean.setConversationId((String) request.getAttribute(CommonNames.CSI_CONVERSATION_ID));

		// Thread.currentThread().setName((String)
		// request.getAttribute(CommonNames.CSI_CONVERSATION_ID));
		if (perfTrackerBean.getConversationId() != null)
			Thread.currentThread().setName(perfTrackerBean.getConversationId());
		setRequestAttribute(request, CommonNames.CSI_CLIENT_DME2_LOOKUP, "");
		perfTrackerBean.setClientDME2Lookup(((String) request.getAttribute(CommonNames.CSI_CLIENT_DME2_LOOKUP)));
		setRequestAttribute(request, CommonNames.CSI_UNIQUE_TXN_ID, uniqueTransactionId);

		perfTrackerBean.setUniqueTransactionId((String) request.getAttribute(CommonNames.CSI_UNIQUE_TXN_ID));

		setRequestAttribute(request, CommonNames.CSI_MESSAGE_ID, GUIDHelper.createGUID());
		perfTrackerBean.setOriginalMessageId((String) request.getAttribute(CommonNames.CSI_MESSAGE_ID));

		setRequestAttribute(request, CommonNames.CSI_TIME_TO_LIVE, CommonNames.ATTR_TTL_DEFAULT);

		setRequestAttribute(request, CommonNames.CSI_SEQUENCE_NUMBER, "1");

		setRequestAttribute(request, CommonNames.CSI_TOTAL_IN_SEQUENCE, "1");

		perfTrackerBean.setTtl((String) request.getAttribute(CommonNames.CSI_TIME_TO_LIVE));
		perfTrackerBean.setSeqNumber((String) request.getAttribute(CommonNames.CSI_SEQUENCE_NUMBER));
		perfTrackerBean.setTotalInSequence((String) request.getAttribute(CommonNames.CSI_TOTAL_IN_SEQUENCE));

		String originatorId = userName;
		setRequestAttribute(request, CommonNames.CSI_ORIGINATOR_ID, originatorId);
		setRequestAttribute(request, CommonNames.CSI_CLIENT_APP, UtilLib.getClientApp());

		perfTrackerBean.setOriginatorId((String) request.getAttribute(CommonNames.CSI_ORIGINATOR_ID));
		perfTrackerBean.setClientApp((String) request.getAttribute(CommonNames.CSI_CLIENT_APP));

		String serviceName = UtilLib.getServiceName(request);
		
		perfTrackerBean.setServiceName(serviceName);

		String appName = request.getHeader(CommonNames.HTTP_HEADER_SERVICE_NAME);

		// Set the clientSentTime which are set by dme2 client on herader
		perfTrackerBean.setClientSentTime(request.getHeader(CommonNames.AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP));
		request.setAttribute(PERFORMANCE_TRACKER_BEAN, perfTrackerBean);

	}

	public void handleResponse(HttpServletRequest request, HttpServletResponse response, QueueConnector connector) {

		setHeaders(request, response);

		PerformanceTrackingBean perfTrackerBean = (PerformanceTrackingBean) request
				.getAttribute(PERFORMANCE_TRACKER_BEAN);

		if (perfTrackerBean != null) {
			response.setHeader(CommonNames.ATT_CONVERSATION_ID, perfTrackerBean.getConversationId());
			response.setHeader(CommonNames.ATT_MESSAGE_ID, perfTrackerBean.getOriginalMessageId());
			finalizeRequest(request, response);

		}
		sendCsiLog(request, connector);
	}
	
	private void sendCsiLog(HttpServletRequest request, QueueConnector connector) {

		if (request.getAttribute(CommonNames.AUDIT_LOG) != null) {
			connector.sendAuditLog(request.getAttribute(CommonNames.AUDIT_LOG).toString());
			request.removeAttribute(CommonNames.AUDIT_LOG);
		}
		if (request.getAttribute(CommonNames.PERFORMANCE_LOG) != null) {
			connector.sendPerformanceLog(request.getAttribute(CommonNames.PERFORMANCE_LOG).toString());
			request.removeAttribute(CommonNames.PERFORMANCE_LOG);
		}
	}

	public void finalizeRequest(HttpServletRequest request, HttpServletResponse response) {

		logger.debug("In...:finalizeRequest");
		String servicename = UtilLib.getServiceName(request);
	
		PerformanceTrackingBean perfTrackerBean = (PerformanceTrackingBean) request
				.getAttribute(PERFORMANCE_TRACKER_BEAN);
		long startTime = (long) request.getAttribute(CommonNames.START_TIME);
		AuditRecord ar = new AuditRecord();
		try {
			logger.debug("Starting application specific handling...:finalizeRequest");
			// request.setAttribute(CommonNames.AUDIT_RECORD, ar);
			// request.setAttribute(CommonNames.ATTR_START_TIME,
			// Long.valueOf(startTime).toString());
			perfTrackerBean.setAuditRecord(ar);
			servicename = LoggerNameConverter.convertNormalizedName(request, servicename);

			perfTrackerBean.setServiceName(servicename);

			perfTrackerBean.setRequestContentLen(request.getContentLength());
			perfTrackerBean.setResponseMsgSize(getResponseLength(request));
			perfTrackerBean.setMethod(request.getMethod());

			ar.setInstanceName(SystemParams.instance().getInstanceName());
			ar.setInitiatedTimestamp(UtilLib.epochToXmlGC(startTime));
			ar.setVtier(SystemParams.instance().getVtier());
			ar.setCluster(SystemParams.instance().getCluster());
			ar.setHostName(SystemParams.instance().getHostName());
			ar.setHostIPAddress(SystemParams.instance().getIpAddress());
			ar.setSubject("CW.pub.spm2." + servicename + ".response");
			ar.setMode("");
			ar.setServiceKeyData1("");
			ar.setServiceKeyData2("");
			ar.setSourceClass(CommonNames.SOURCE_CLASS);
			ar.setSourceMethod(CommonNames.AUDIT_LOGGER_NAME);
			ar.setTransactionName(servicename);
			/*
			 * ar.setApplicationId(request.getAttribute(CommonNames.
			 * CSI_USER_NAME));
			 * ar.setConversationId(request.getAttribute(CommonNames.
			 * CSI_CONVERSATION_ID));
			 * ar.setUniqueTransactionId(request.getAttribute(CommonNames.
			 * CSI_UNIQUE_TXN_ID));
			 * ar.setOriginalMessageId(request.getAttribute(CommonNames.
			 * CSI_MESSAGE_ID));
			 * ar.setOriginatorId(request.getAttribute(CommonNames.
			 * CSI_ORIGINATOR_ID));
			 * ar.setClientApp(UtilLib.ifNullThenEmpty(request.getAttribute(
			 * CommonNames.CSI_CLIENT_APP))); ar.setOriginationSystemId("N/A");
			 * ar.setOriginationSystemName(request.getAttribute(CommonNames.
			 * CSI_USER_NAME));
			 * ar.setOriginationSystemVersion(request.getAttribute(CommonNames.
			 * CSI_VERSION));
			 */

			ar.setApplicationId(perfTrackerBean.getUserName());
			ar.setConversationId(perfTrackerBean.getConversationId());
			ar.setUniqueTransactionId(perfTrackerBean.getUniqueTransactionId());
			ar.setOriginalMessageId(perfTrackerBean.getOriginalMessageId());
			ar.setOriginatorId(perfTrackerBean.getOriginatorId());
			ar.setClientApp(UtilLib.ifNullThenEmpty(perfTrackerBean.getClientApp()));
			ar.setOriginationSystemId("N/A");
			ar.setOriginationSystemName(perfTrackerBean.getUserName());
			ar.setOriginationSystemVersion(perfTrackerBean.getOriginationSystemVersion());

			// new fields added per new schema
			ar.setClientIP(request.getRemoteAddr());
			ar.setHttpMethod(perfTrackerBean.getMethod());
			ar.setRequestURL(request.getPathInfo());

			// PerformanceTracking.initPerfTrack(request,servicename);
			PerformanceTracking.initPerfTrack(perfTrackerBean, servicename);
			// PerformanceTracking.addPerfTrack(request, "Main", "I",
			// startTime.toString(), servicename);

			int httpCode = response.getStatus();

			if (httpCode == HttpServletResponse.SC_UNAUTHORIZED) {
				ar.setResponseCode(CommonNames.CSI_AUTH_ERROR);
				ar.setResponseDescription(CommonErrors.DEF_401_FAULT_DESC);
				ar.setFaultCode(CommonErrors.DEF_401_FAULT_CODE);
				ar.setFaultDescription(CommonErrors.DEF_401_FAULT_DESC);
				ar.setFaultLevel("ERROR");
				ar.setTransactionStatus("E");
				ar.setFaultEntity("CSI");
				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
				ar.setExternalFaultCode(String.valueOf(httpCode));
				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);
			} else if (httpCode == HttpServletResponse.SC_FORBIDDEN) {
				ar.setResponseCode(CommonNames.CSI_AUTH_ERROR);
				ar.setResponseDescription(CommonErrors.DEF_403_FAULT_DESC);
				ar.setFaultCode(CommonErrors.DEF_403_FAULT_CODE);
				ar.setFaultDescription(CommonErrors.DEF_403_FAULT_DESC);
				ar.setFaultLevel("ERROR");
				ar.setTransactionStatus("E");
				ar.setFaultEntity("CSI");
				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
				ar.setExternalFaultCode(String.valueOf(httpCode));
				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);
			} else if (httpCode == HttpServletResponse.SC_NOT_IMPLEMENTED) {

				ar.setResponseCode(CommonNames.CSI_SERVICE_UNAVAIL_ERROR);
				ar.setResponseDescription(CommonErrors.DEF_501_FAULT_DESC);
				ar.setFaultCode(CommonErrors.DEF_501_FAULT_CODE);
				ar.setFaultDescription(CommonErrors.DEF_501_FAULT_DESC);
				ar.setFaultLevel("ERROR");
				ar.setTransactionStatus("E");
				ar.setFaultEntity("CSI");
				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
				ar.setExternalFaultCode(String.valueOf(httpCode));
				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);

			} else if (httpCode == HttpServletResponse.SC_SERVICE_UNAVAILABLE) {
				ar.setResponseCode(CommonNames.CSI_SERVICE_UNAVAIL_ERROR);
				ar.setResponseDescription(CommonErrors.DEF_503_FAULT_DESC);
				ar.setFaultCode(CommonErrors.DEF_503_FAULT_CODE);
				ar.setFaultDescription(CommonErrors.DEF_503_FAULT_DESC);
				ar.setFaultLevel("ERROR");
				ar.setTransactionStatus("E");
				ar.setFaultEntity("CSI");
				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
				ar.setExternalFaultCode(String.valueOf(httpCode));
				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);
			} else if (400 <= httpCode && httpCode <= 499) {

				ar.setResponseCode(CommonNames.CSI_SERVICE_UNAVAIL_ERROR);
				ar.setResponseDescription(CommonErrors.DEF_4NN_FAULT_DESC);
				ar.setFaultCode(CommonErrors.DEF_4NN_FAULT_CODE);
				ar.setFaultDescription(CommonErrors.DEF_4NN_FAULT_DESC);
				ar.setFaultLevel("ERROR");
				ar.setFaultEntity("CSI");
				ar.setTransactionStatus("E");
				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
				ar.setExternalFaultCode(String.valueOf(httpCode));
				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);

			} else if (httpCode == 500) {

				ar.setResponseCode(CommonNames.CSI_SERVICE_UNAVAIL_ERROR);
				ar.setResponseDescription(CommonErrors.DEF_500_FAULT_DESC);
				ar.setFaultCode(CommonErrors.DEF_500_FAULT_CODE);
				ar.setFaultDescription(CommonErrors.DEF_500_FAULT_DESC);
				ar.setFaultLevel("ERROR");
				ar.setFaultEntity("CSI");
				ar.setTransactionStatus("E");
				// ar.setFaultTimestamp(UtilLib.epochToXmlGC((new
				// Double(System.nanoTime()/1000000)).longValue()));
				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
				ar.setExternalFaultCode(String.valueOf(httpCode));
				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);

			}

			else {
				ar.setResponseDescription(CommonNames.CSI_SUCCESS);
				ar.setResponseCode(CommonNames.CSI_SUCCESS_RESPONSE_CODE);
				ar.setTransactionStatus("C");
			}

			// Enhance CSI logging to use the CAET error code

			if (response.getHeader(CommonNames.CAET_RestErrorCode) != null
					|| response.getHeader(CommonNames.CAET_CingularErrorCode) != null) {

				// if(request.getHeader("X-CAET-CingularErrorCode") != null){
				if ("Y".equals(request.getAttribute(CommonNames.AJSC_CAET_IS_REST_SERVICE))) {

					ar.setResponseCode(response.getHeader(CommonNames.CAET_CingularErrorCategory));
					ar.setResponseDescription(response.getHeader(CommonNames.CAET_RestErrorDescription));

				} else

				{

					ar.setResponseCode(response.getHeader(CommonNames.CAET_CingularErrorCode));

					ar.setResponseDescription(response.getHeader(CommonNames.CAET_CingularErrorDescription));

				}

				ar.setFaultCode(response.getHeader(CommonNames.CAET_FaultCode));

				ar.setFaultDescription(response.getHeader(CommonNames.CAET_FaultDesc));

				ar.setFaultLevel(CommonNames.ERROR);

				ar.setFaultEntity(response.getHeader(CommonNames.CAET_FaultEntity));

				ar.setTransactionStatus("E");

				ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));

				// ar.setFaultTimestamp(UtilLib.epochToXmlGC((new
				// Double(System.nanoTime()/1000000)).longValue()));

				ar.setExternalFaultCode(String.valueOf(httpCode));

				ar.setExternalFaultDescription(CommonErrors.GENERIC_XML_ERROR);

			}

		}

		catch (Exception e) {
			// AuditRecord ar =
			// (AuditRecord)request.getAttribute(CommonNames.AUDIT_RECORD);
			ar.setResponseCode(CommonNames.CSI_GENERIC_UNKNOWN_ERROR);
			ar.setResponseDescription(CommonErrors.DEF_5NN_FAULT_DESC);
			ar.setFaultEntity("CSI");
			ar.setFaultCode(CommonErrors.DEF_5NN_FAULT_CODE);
			ar.setFaultDescription(e.getMessage());
			ar.setFaultLevel("ERROR");
			ar.setFaultSequenceNumber("1");
			ar.setTransactionStatus("E");
			ar.setFaultTimestamp(UtilLib.epochToXmlGC(System.currentTimeMillis()));
			// ar.setFaultTimestamp(UtilLib.epochToXmlGC(((Long)System.nanoTime()/1000000).longValue()));
			logger.error("EXCEPTION - " + e.getMessage());
		}

		finally {
			// AuditRecord ar =
			// (AuditRecord)request.getAttribute(CommonNames.AUDIT_RECORD);
			if (ar != null) {

				if (perfTrackerBean != null && !perfTrackerBean.isAsync()) {
					perfTrackerBean.setAuditRecord(ar);
					logger.debug("Before calling completeLogging");
					completeLogging(request, servicename);
				}

			} else {
				logger.debug("Audit Record is null,abort logging");
			}
		}

	}

	private String getResponseLength(HttpServletRequest request) {

		long length = -1;

		if (request.getAttribute(CommonNames.RESPONSE_ENTITY) != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = null;
			try {
				os = new ObjectOutputStream(out);
				os.writeObject(request.getAttribute(CommonNames.RESPONSE_ENTITY));
			} catch (IOException e) {
				logger.error("error while getting the response length" + e);
			}

			length = out.size();
		} else if (request.getAttribute(CommonNames.RESPONSE_LENGTH) != null) {
			length = (long) request.getAttribute(CommonNames.RESPONSE_LENGTH);
		}
		return String.valueOf(length);
	}

	public static void completeLogging(HttpServletRequest request, String servicename) {

		try {
			logger.debug("In...:completeLogging");
			PerformanceTrackingBean perfTrackerBean = null;
			perfTrackerBean = (PerformanceTrackingBean) request.getAttribute(PERFORMANCE_TRACKER_BEAN);
			// AuditRecord ar =
			// (AuditRecord)request.getAttribute(CommonNames.AUDIT_RECORD);
			AuditRecord ar = perfTrackerBean.getAuditRecord();
			long endTime = System.currentTimeMillis();
			// long endTime = System.nanoTime()/1000000;

			// long startTime =
			// Long.parseLong((String)request.getAttribute(CommonNames.ATTR_START_TIME));
			long startTime = Long.parseLong(perfTrackerBean.getStartTime());
			ar.setElapsedTime(Long.toString(endTime - startTime));
			if (ar != null) {
				AuditRecordLogging.auditLogResult(ar, request);
			}

			if ("C".equals(ar.getTransactionStatus())) {
				// PerformanceTracking.addPerfTrack(request,
				// "Main",
				// "C",
				// Long.toString(endTime),servicename);
				PerformanceTracking.addPerfTrack(perfTrackerBean, "Main", "C", Long.toString(endTime), servicename,
						null);

			} else {
				// PerformanceTracking.addPerfTrack(request,
				// "Main",
				// "E",
				// Long.toString(endTime),servicename);
				PerformanceTracking.addPerfTrack(perfTrackerBean, "Main", "E", Long.toString(endTime), servicename,
						null);
			}
			// PerformanceTracking.addAdditionalPerfTrack(request, "10", "10");
			// PerformanceTracking.logTracker(request);
			PerformanceTracking.logTracker(perfTrackerBean, request);
			removeRequestAttribute(request);

		} catch (Exception e) {
			logger.error("Error completing logs - " + e);
		}
	}

	static void removeRequestAttribute(HttpServletRequest request) {
		String[] attributes = { CommonNames.CSI_VERSION, CommonNames.CSI_ORIGINAL_VERSION, CommonNames.CSI_USER_NAME,
				CommonNames.CSI_CONVERSATION_ID, CommonNames.CSI_UNIQUE_TXN_ID, CommonNames.CSI_TIME_TO_LIVE,
				CommonNames.CSI_SEQUENCE_NUMBER, CommonNames.CSI_TOTAL_IN_SEQUENCE, CommonNames.CSI_ORIGINATOR_ID,
				CommonNames.CSI_CLIENT_APP, CommonNames.PERF_RECORD, CommonNames.AUDIT_RECORD };

		for (int i = 0; i < attributes.length; i++) {
			request.removeAttribute(attributes[i]);
		}
	}

	protected void setHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader(CommonNames.CACHE_CONTROL, CommonNames.NO_CACHE);
	}

	static void setRequestAttribute(HttpServletRequest request, String name, String defaultValue) {
		String value = null;
		if (name.equals(CommonNames.CSI_CONVERSATION_ID)) {
			value = (request.getHeader(CommonNames.ATT_CONVERSATION_ID) != null)
					? request.getHeader(CommonNames.ATT_CONVERSATION_ID)
					: request.getHeader(CommonNames.CSI_CONVERSATION_ID);
		} else if (name.equals(CommonNames.CSI_MESSAGE_ID)) {
			value = (request.getHeader(CommonNames.ATT_MESSAGE_ID) != null)
					? request.getHeader(CommonNames.ATT_MESSAGE_ID) : request.getHeader(CommonNames.CSI_MESSAGE_ID);
		} else if (name.equals(CommonNames.CSI_ORIGINATOR_ID)) {
			value = (request.getHeader(CommonNames.ATT_ORIGINATOR_ID) != null)
					? request.getHeader(CommonNames.ATT_ORIGINATOR_ID)
					: request.getHeader(CommonNames.CSI_ORIGINATOR_ID);
		} else if (name.equals(CommonNames.CSI_TIME_TO_LIVE)) {
			value = (request.getHeader(CommonNames.ATT_TIME_TO_LIVE) != null)
					? request.getHeader(CommonNames.ATT_TIME_TO_LIVE) : request.getHeader(CommonNames.CSI_TIME_TO_LIVE);
		} else if (name.equals(CommonNames.CSI_UNIQUE_TXN_ID)) {
			value = (request.getAttribute(CommonNames.ATT_UNIQUE_TXN_ID) != null)
					? request.getAttribute(CommonNames.ATT_UNIQUE_TXN_ID).toString()
					: request.getHeader(CommonNames.CSI_UNIQUE_TXN_ID);
		} else {
			value = request.getHeader(name);
		}
		if (UtilLib.isNullOrEmpty(value)) {
			request.setAttribute(name, defaultValue);
		} else {
			request.setAttribute(name, value);
		}
	}
	

	public void setSystemProperties(String csiEnable, String namespace, String serviceName, String serviceVersion,
			String routeOffer, EnvironmentUtility utility) {
		System.setProperty(CommonNames.CSI_ENABLE, csiEnable);
		if (StringUtils.isNotEmpty(namespace)) {
			System.setProperty(CommonNames.SOACLOUD_NAMESPACE, namespace);
		}

		if (System.getProperty(CommonNames.APP_NAME) == null) {

			if (StringUtils.isNotEmpty(serviceName)) {
				System.setProperty(CommonNames.APP_NAME, serviceName);

			} else {
				System.setProperty(CommonNames.APP_NAME, utility.getServiceName());
			}
		}

		if (utility.getVersion() != null) {
			System.setProperty(CommonNames.APP_VERSION, utility.getVersion());
		} else {
			System.setProperty(CommonNames.APP_VERSION, serviceVersion);
		}

		if (routeOffer != null) {
			System.setProperty(CommonNames.ROUTE_OFFER, routeOffer);
		}
	}
	

	public void handleRequest(HttpServletRequest request, String interceptorName) {

		try {
			String csiUserName = request.getHeader("X-CSI-UserName");
			if (!processUserAndPassword(request) && csiUserName == null) {
				request.setAttribute(CommonNames.CSI_USER_NAME, CommonNames.CSI_MOCK_USER_NAME);
			}

			else {
				if (request.getAttribute(CommonNames.CSI_USER_NAME) == null) {
					request.setAttribute(CommonNames.CSI_USER_NAME, csiUserName);
				}
			}
			String sentTime = request.getHeader(CommonNames.AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP);
			request.setAttribute(CommonNames.AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP, sentTime);

			internalHandleRequest(request);

		} catch (Exception e) {
			logger.error("Error calling the " + interceptorName + ": ", e);
		}

	}
	
	private boolean processUserAndPassword(HttpServletRequest request) {
		String authHeader = request.getHeader(CommonNames.HTTP_AUTHORIZATION);
		boolean validUser = false;
		if (authHeader != null) {
			String[] split = authHeader.split("\\s+");
			if (split.length > 0) {
				String basic = split[0];

				if (basic.equalsIgnoreCase("Basic")) {
					String credentials = split[1];
					String userPass = new String(Base64.getDecoder().decode(credentials));
					int p = userPass.indexOf(":");
					if (p != -1) {
						validUser = true;
						request.setAttribute(CommonNames.CSI_USER_NAME, userPass.substring(0, p));

						logger.info("Username: " + request.getAttribute(CommonNames.CSI_USER_NAME));
					}
				}
			}
		}
		return validUser;
	}

	public boolean requestContainsError(HttpServletRequest request) {
		return request.getAttribute(CommonNames.SEND_400) != null
				&& (boolean) request.getAttribute(CommonNames.SEND_400);
	}


	public InterceptorError createInterceptorError() {
		InterceptorError error = new InterceptorError();
		error.setResponseCode(400);

		return error;
	}

}
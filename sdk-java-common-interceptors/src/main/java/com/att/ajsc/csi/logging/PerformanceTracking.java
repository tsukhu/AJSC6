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
package com.att.ajsc.csi.logging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.xmlbeans.XmlCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.ajsc.csilogging.util.CommonNames;
import com.att.ajsc.csilogging.util.GUIDHelper;
import com.att.ajsc.csilogging.util.MessageHistory;
import com.att.ajsc.csilogging.util.SystemParams;
import com.att.ajsc.csilogging.util.UtilLib;

public class PerformanceTracking {

	static final Logger logger = LoggerFactory.getLogger(PerformanceTracking.class);
	public static final String PERFORMANCE_TRACKER_BEAN = "PERFORMANCE_TRACKER_BEAN";

	public static void initPerfTrack(HttpServletRequest request, String serviceName) {
		ConcurrentLinkedQueue<String> tracker = new ConcurrentLinkedQueue<String>();

		StringBuilder message = new StringBuilder(300);
		message.append(serviceName);
		message.append("#");
		message.append("AJSC-CSI");
		message.append("#");
		message.append(SystemParams.instance().getInstanceName());
		message.append("#");
		message.append(request.getAttribute(CommonNames.CSI_CONVERSATION_ID));
		message.append("#");
		long stime = Long.parseLong((String) request.getAttribute(CommonNames.ATTR_START_TIME));
		XmlCalendar cal = new XmlCalendar(new Date(stime));
		XMLGregorianCalendar initTime = null;
		try {
			initTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND),
					Math.round(cal.get(Calendar.ZONE_OFFSET) / 1000 / 60));
		} catch (Exception e) {
			initTime = null;
			logger.error(e.getMessage());
		}
		if (initTime == null)
			message.append("");
		else
			message.append(initTime);
		message.append("#");
		message.append(request.getAttribute(CommonNames.CSI_USER_NAME));
		message.append("#");
		message.append(request.getAttribute(CommonNames.CSI_UNIQUE_TXN_ID));
		message.append("|");
		message.append("Main");
		message.append("#");
		message.append("I");
		message.append("#");
		message.append(stime);
		message.append("#");
		message.append(serviceName);

		// addPerfTrack(request,"Main", "I",
		// String.valueOf(System.currentTimeMillis()), serviceName);
		tracker.add(message.toString());
		if (request.getAttribute(CommonNames.PERF_RECORD) == null) {
			request.setAttribute(CommonNames.PERF_RECORD, tracker);
		}

	}

	// Overloaded initPerfTrack method that accepts PerformanceTrackingBean
	// instead of HttpServletRequest as the input param
	public static void initPerfTrack(PerformanceTrackingBean perfTrackerBean, String serviceName) {
		ConcurrentLinkedQueue<String> tracker = new ConcurrentLinkedQueue<String>();

		StringBuilder debugMsgFormat = new StringBuilder();

		TransactionDetails transactionDet;

		if (perfTrackerBean.getTransactionDet() != null) {
			transactionDet = perfTrackerBean.getTransactionDet();
		} else {
			transactionDet = new TransactionDetails();
		}

		StringBuilder message = new StringBuilder(300);
		message.append(serviceName);
		message.append("#");
		message.append("AJSC-CSI");
		message.append("#");
		message.append(SystemParams.instance().getInstanceName());
		message.append("#");
		message.append(perfTrackerBean.getConversationId());
		message.append("#");

		long stime = Long.parseLong((String) perfTrackerBean.getStartTime());

		XmlCalendar cal = new XmlCalendar(new Date(stime));
		XMLGregorianCalendar initTime = null;
		try {
			initTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND),
					Math.round(cal.get(Calendar.ZONE_OFFSET) / 1000 / 60));
		} catch (Exception e) {
			initTime = null;
			logger.error(e.getMessage());
		}

		if (initTime == null)
			message.append("");
		else
			message.append(initTime);

		message.append("#");
		message.append(perfTrackerBean.getUserName());
		message.append("#");
		message.append(perfTrackerBean.getUniqueTransactionId());
		message.append("|");
		message.append("Main");
		message.append("#");
		message.append("I");
		message.append("#");
		message.append(stime);
		message.append("#");
		message.append(serviceName);

		tracker.add(message.toString());
		if (Boolean.getBoolean(CommonNames.ENABLE_TRAIL_SUMMARY)) {
			debugMsgFormat.append(
					"\n\nProcessed Transaction Details: \n==================================================================================\n");
			debugMsgFormat.append("Service name: ").append(serviceName).append("\nMethod:")
					.append(perfTrackerBean.getMethod()).append("\nInstance Name:")
					.append(SystemParams.instance().getInstanceName()).append("\nConversation Id: ")
					.append(perfTrackerBean.getConversationId()).append("\nInitialized timestamp: ").append(initTime)
					.append("\nUser: ").append(perfTrackerBean.getUserName()).append("\nUnique Transaction Id: ")
					.append(perfTrackerBean.getUniqueTransactionId()).append("\nMain Initiated ");// .append(stime);
			transactionDet.setDebugMsg(debugMsgFormat.toString());
		}
		transactionDet.setStartTime(Long.toString(stime));
		perfTrackerBean.setTransactionDet(transactionDet);

		if (perfTrackerBean.getPerfRecord() == null) {
			perfTrackerBean.setPerfRecord(tracker);
		}

	}

	public static void addInvokeServiceTrack(HttpServletRequest request, String serviceName, long startTime,
			long endTime, String status, int reqMsgSize, int respMsgSize) {
		if (request.getAttribute(CommonNames.PERF_RECORD) == null) {

			String logicalServiceName = UtilLib.getServiceName(request);
			
			initPerfTrack(request, logicalServiceName);
		}

		addPerfTrack(request, "InvokeService", "I", Long.toString(startTime), serviceName);
		String customTag = serviceName + "#RecordSizes#ReqMsgSize=" + Integer.toString(reqMsgSize) + "#RespMsgSize="
				+ Integer.toString(respMsgSize);
		addPerfTrack(request, "InvokeService", status, Long.toString(endTime), customTag);
	}

	public static void addInvokeServiceTrack(PerformanceTrackingBean perfTrackerBean, String serviceName,
			long startTime, long endTime, String status, int reqMsgSize, int respMsgSize) {
		if (perfTrackerBean.getPerfRecord() == null) {
			initPerfTrack(perfTrackerBean, perfTrackerBean.getServiceName());
		}

		String activityId = GUIDHelper.createGUID();
		addPerfTrack(perfTrackerBean, "InvokeService", "I", Long.toString(startTime), serviceName, activityId);
		String customTag = serviceName + "#RecordSizes#ReqMsgSize=" + Integer.toString(reqMsgSize) + "#RespMsgSize="
				+ Integer.toString(respMsgSize);
		addPerfTrack(perfTrackerBean, "InvokeService", status, Long.toString(endTime), customTag, activityId);
	}

	public static void addPerfTrack(HttpServletRequest request, String location, String status, String timeStamp,
			String customTag) {

		if (request != null && request.getAttribute(CommonNames.PERF_RECORD) == null) {
			String servicename = UtilLib.getServiceName(request);
			initPerfTrack(request, servicename);
		}

		ConcurrentLinkedQueue<String> tracker = (ConcurrentLinkedQueue<String>) request
				.getAttribute(CommonNames.PERF_RECORD);
		if (tracker != null) {
			StringBuilder message = new StringBuilder(300);
			message.append(location);
			message.append("#");
			if (!status.isEmpty()) {
				message.append(status);
				message.append("#");
			}
			message.append(timeStamp);
			message.append("#");
			message.append(customTag);
			tracker.add(message.toString());
		}

	}

	// TODO - added temporarily as framework utilities jar is looking for this
	// method.Need to be removed
	@Deprecated
	@SuppressWarnings("unchecked")
	public static void addPerfTrack(PerformanceTrackingBean perfTrackerBean, String location, String status,
			String timeStamp, String customTag) {

		if (perfTrackerBean != null && perfTrackerBean.getPerfRecord() == null) {
			initPerfTrack(perfTrackerBean, perfTrackerBean.getServiceName());

		}

		TransactionDetails transactionDetail = perfTrackerBean.getTransactionDet();
		ArrayList<Transaction> transactionsList = null;

		Transaction newTransaction = null;

		if (transactionDetail != null) {
			transactionsList = transactionDetail.getTransactions();
		}

		ConcurrentLinkedQueue<String> tracker = null;

		if (perfTrackerBean.getPerfRecord() instanceof ConcurrentLinkedQueue<?>) {
			tracker = (ConcurrentLinkedQueue<String>) perfTrackerBean.getPerfRecord();
		}

		if (tracker != null) {
			StringBuilder message = new StringBuilder(300);
			message.append(location);
			message.append("#");
			if (!status.isEmpty()) {
				message.append(status);
				message.append("#");
			}
			message.append(timeStamp);
			message.append("#");
			message.append(customTag);
			tracker.add(message.toString());
		}

		if (!location.equals("Main")) {
			if (status.equalsIgnoreCase("I")) {
				newTransaction = new Transaction();
				if (customTag.indexOf("#") > 0) {
					newTransaction.setActivityName(customTag.substring(0, customTag.indexOf("#")));
					newTransaction.setActivityMethod(customTag.substring(customTag.indexOf("#") + 1));
				} else {
					newTransaction.setActivityName(customTag);
					newTransaction.setActivityMethod("");
				}

				newTransaction.setActivityNameAndMethod(customTag);
				newTransaction.setTransactionName(location);
				newTransaction.setActivityStartTime(timeStamp);
				// newTransaction.setActivityId(activityId);

				if (transactionsList == null) {
					transactionsList = new ArrayList<Transaction>();
				}

				transactionsList.add(newTransaction);
				transactionDetail.setTransactions(transactionsList);
				perfTrackerBean.setTransactionDet(transactionDetail);

			} else { // Either status is 'C' or 'E'
				// Get the transaction object (Activity) from the list and
				// record its endTime & status
				for (Transaction transaction : transactionsList) {
					// if(activityId.equals(transaction.getActivityId())) {
					transaction.setActivityEndTime(timeStamp);
					transaction.setActivityRunStatus(status);
					// }
				}
			}
		} else {
			transactionDetail.setRunStatus(status);
			transactionDetail.setEndTime(timeStamp);

			StringBuffer debugMsg = new StringBuffer();
			debugMsg.append(transactionDetail.getDebugMsg());

			if (transactionsList != null) {
				for (Transaction transaction : transactionsList) {
					debugMsg.append("\n\n\tActivity: ").append(transaction.getTransactionName())
							.append("\n\t" + transaction.getTransactionName() + " Name: ")
							.append(transaction.getActivityName())
							.append("\n\t" + transaction.getTransactionName() + " Method: ")
							.append(transaction.getActivityMethod()).append("\n\tRun Status: ")
							.append(transaction.getActivityRunStatus()).append("\n\tElapsed time: ")
							.append(Long.parseLong(transaction.getActivityEndTime())
									- Long.parseLong(transaction.getActivityStartTime()))
							.append(" milliseconds");
				}
			}

			debugMsg.append("\n\nMain Ended").append("\nMain Run Status ").append(status)
					.append("\nMain Total Elapsed time: ").append(Long.parseLong(transactionDetail.getEndTime())
							- Long.parseLong(transactionDetail.getStartTime()))
					.append(" milliseconds").append("\n");

			transactionDetail.setDebugMsg(debugMsg.toString());

		}

	}

	@SuppressWarnings("unchecked")
	public static void addPerfTrack(PerformanceTrackingBean perfTrackerBean, String location, String status,
			String timeStamp, String customTag, String activityId) {

		if (perfTrackerBean != null && perfTrackerBean.getPerfRecord() == null) {
			initPerfTrack(perfTrackerBean, perfTrackerBean.getServiceName());

		}

		TransactionDetails transactionDetail = perfTrackerBean.getTransactionDet();
		ArrayList<Transaction> transactionsList = null;

		Transaction newTransaction = null;

		if (transactionDetail != null) {
			transactionsList = transactionDetail.getTransactions();
		}

		ConcurrentLinkedQueue<String> tracker = null;

		if (perfTrackerBean.getPerfRecord() instanceof ConcurrentLinkedQueue<?>) {
			tracker = (ConcurrentLinkedQueue<String>) perfTrackerBean.getPerfRecord();
		}

		if (tracker != null) {
			StringBuilder message = new StringBuilder(300);
			message.append(location);
			message.append("#");
			if (!status.isEmpty()) {
				message.append(status);
				message.append("#");
			}
			message.append(timeStamp);
			message.append("#");
			message.append(customTag);
			tracker.add(message.toString());
		}

		if (!location.equals("Main")) {
			if (status.equalsIgnoreCase("I")) {
				newTransaction = new Transaction();
				if (customTag.indexOf("#") > 0) {
					newTransaction.setActivityName(customTag.substring(0, customTag.indexOf("#")));
					newTransaction.setActivityMethod(customTag.substring(customTag.indexOf("#") + 1));
				} else {
					newTransaction.setActivityName(customTag);
					newTransaction.setActivityMethod("");
				}

				newTransaction.setActivityNameAndMethod(customTag);
				newTransaction.setTransactionName(location);
				newTransaction.setActivityStartTime(timeStamp);
				newTransaction.setActivityId(activityId);

				if (transactionsList == null) {
					transactionsList = new ArrayList<Transaction>();
				}

				transactionsList.add(newTransaction);
				transactionDetail.setTransactions(transactionsList);
				perfTrackerBean.setTransactionDet(transactionDetail);

			} else { // Either status is 'C' or 'E'
				// Get the transaction object (Activity) from the list and
				// record its endTime & status
				for (Transaction transaction : transactionsList) {
					if (activityId.equals(transaction.getActivityId())) {
						transaction.setActivityEndTime(timeStamp);
						transaction.setActivityRunStatus(status);
					}
				}
			}
		} else {
			transactionDetail.setRunStatus(status);
			transactionDetail.setEndTime(timeStamp);

			StringBuffer debugMsg = new StringBuffer();
			debugMsg.append(transactionDetail.getDebugMsg());

			if (transactionsList != null) {
				for (Transaction transaction : transactionsList) {
					debugMsg.append("\n\n\tActivity: ").append(transaction.getTransactionName())
							.append("\n\t" + transaction.getTransactionName() + " Name: ")
							.append(transaction.getActivityName())
							.append("\n\t" + transaction.getTransactionName() + " Method: ")
							.append(transaction.getActivityMethod()).append("\n\tRun Status: ")
							.append(transaction.getActivityRunStatus()).append("\n\tElapsed time: ")
							.append(Long.parseLong(transaction.getActivityEndTime())
									- Long.parseLong(transaction.getActivityStartTime()))
							.append(" milliseconds");
				}
			}

			debugMsg.append("\n\nMain Ended").append("\nMain Run Status ").append(status)
					.append("\nMain Total Elapsed time: ").append(Long.parseLong(transactionDetail.getEndTime())
							- Long.parseLong(transactionDetail.getStartTime()))
					.append(" milliseconds").append("\n");

			transactionDetail.setDebugMsg(debugMsg.toString());

		}

	}

	public static void logTracker(HttpServletRequest request) {

		logger.debug("In...:logTracker");
		addAdditionalPerfTrack(request, request.getContentLength(),
				Integer.parseInt((String) request.getAttribute("resMsgSize").toString()));

		ConcurrentLinkedQueue<String> tracker = (ConcurrentLinkedQueue<String>) request
				.getAttribute(CommonNames.PERF_RECORD);
		try {

			if (tracker != null) {
				StringBuilder message = new StringBuilder(tracker.size() * 100);
				message.append(tracker.poll());
				while (!tracker.isEmpty()) {
					message.append("|");
					message.append(tracker.poll());
				}
				logger.info("Performance Log:\n" + message.toString());

				if (System.getProperty("csiEnable") != null && System.getProperty("csiEnable").equals("true")) {
					request.setAttribute(CommonNames.PERFORMANCE_LOG, message.toString());
				}

			}

			else {
				logger.error("No performance record to log");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void logTracker(PerformanceTrackingBean perfTrackerBean, HttpServletRequest request) {

		logger.debug("In...:logTracker");

		String respMsgSize = (String) perfTrackerBean.getResponseMsgSize();

		if (respMsgSize != null) {
			addAdditionalPerfTrack(perfTrackerBean, perfTrackerBean.getRequestContentLen(),
					Integer.parseInt(respMsgSize));
		} else {
			addAdditionalPerfTrack(perfTrackerBean, perfTrackerBean.getRequestContentLen(), 0);
		}

		ConcurrentLinkedQueue<String> tracker = null;

		if (perfTrackerBean.getPerfRecord() instanceof ConcurrentLinkedQueue<?>) {
			tracker = (ConcurrentLinkedQueue<String>) perfTrackerBean.getPerfRecord();
		}

		try {
			if (tracker != null) {
				StringBuilder message = new StringBuilder(tracker.size() * 100);
				message.append(tracker.poll());
				while (!tracker.isEmpty()) {
					message.append("|");
					message.append(tracker.poll());
				}
				logger.info("Performance Log:\n" + message.toString());

				if (System.getProperty("csiEnable") != null && System.getProperty("csiEnable").equals("true")) {
					request.setAttribute(CommonNames.PERFORMANCE_LOG, message.toString());
				}

				//logger.debug(perfTrackerBean.getTransactionDet().getDebugMsg());

			} else {
				logger.error("No performance record to log");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void addAdditionalPerfTrack(HttpServletRequest request, int reqMsgSize, int respMsgSize) {
		ConcurrentLinkedQueue<String> tracker = (ConcurrentLinkedQueue<String>) request
				.getAttribute(CommonNames.PERF_RECORD);
		AuditRecord ar = (AuditRecord) request.getAttribute(CommonNames.AUDIT_RECORD);
		String rc = "";
		String reqSize = "";
		if (reqMsgSize != -1) {
			reqSize = Integer.toString(reqMsgSize);
		}
		PerformanceTrackingBean perfTrackerBean = (PerformanceTrackingBean) request
				.getAttribute(PERFORMANCE_TRACKER_BEAN);

		String clientSentTime = perfTrackerBean.getClientSentTime();
		rc = "RecordSizes#ReqMsgSize=" + reqSize + "#RespMsgSize=" + respMsgSize + "#ClientDME2Lookup="
				+ perfTrackerBean.getClientDME2Lookup() + "#ClientSentTime=" + clientSentTime + "#Cluster="
				+ ar.getCluster() + "#Vtier=" + ar.getVtier() + "#ClientApp=" + ar.getClientApp() + "#Mode="
				+ ar.getMode() + "#InstanceName=" + ar.getInstanceName() + "#HostIPAddress=" + ar.getHostIPAddress()
				+ "#HostName=" + ar.getHostName();

		if (!(ar.getResponseCode().equals(CommonNames.CSI_SUCCESS_RESPONSE_CODE))) {

			rc = rc + "#FaultEntity=" + UtilLib.ifNullThenEmpty(ar.getFaultEntity()) + "#ExternalFaultCode="
					+ UtilLib.ifNullThenEmpty(ar.getExternalFaultCode()) + "#ExternalFaultDescription="
					+ UtilLib.ifNullThenEmpty(ar.getExternalFaultDescription());
		}
		rc = rc + "#ResponseCode=" + UtilLib.ifNullThenEmpty(ar.getResponseCode()) + "#ResponseDescription="
				+ UtilLib.ifNullThenEmpty(ar.getResponseDescription());

		tracker.add(rc);
	}

	public static void addAdditionalPerfTrack(PerformanceTrackingBean perfTrackerBean, int reqMsgSize,
			int respMsgSize) {

		ConcurrentLinkedQueue<String> tracker = null;

		if (perfTrackerBean.getPerfRecord() instanceof ConcurrentLinkedQueue<?>) {
			tracker = (ConcurrentLinkedQueue<String>) perfTrackerBean.getPerfRecord();
		}

		AuditRecord ar = (AuditRecord) perfTrackerBean.getAuditRecord();

		TransactionDetails transactionDetail = perfTrackerBean.getTransactionDet();
		StringBuffer debugMsg = new StringBuffer();
		debugMsg.append(transactionDetail.getDebugMsg());

		if (Boolean.getBoolean(CommonNames.ENABLE_TRAIL_LOGGING)) {
			debugMsg.append(getTrailLog(perfTrackerBean.getTrailLog()));
		}

		String rc = "";
		String reqSize = "";
		if (reqMsgSize != -1) {
			reqSize = Integer.toString(reqMsgSize);
		}

		rc = "RecordSizes#ReqMsgSize=" + reqSize + "#RespMsgSize=" + respMsgSize + "#ClientDME2Lookup="
				+ perfTrackerBean.getClientDME2Lookup() + "#ClientSentTime=" + perfTrackerBean.getClientSentTime()
				+ "#Cluster=" + (ar != null ? ar.getCluster() : null) + "#Vtier=" + (ar != null ? ar.getVtier() : null)
				+ "#ClientApp=" + (ar != null ? ar.getClientApp() : null) + "#Mode="
				+ (ar != null ? ar.getMode() : null) + "#InstanceName=" + (ar != null ? ar.getInstanceName() : null)
				+ "#HostIPAddress=" + (ar != null ? ar.getHostIPAddress() : null) + "#HostName="
				+ (ar != null ? ar.getHostName() : null);

		if (ar != null && ar.getResponseCode() != null
				&& !(ar.getResponseCode().equals(CommonNames.CSI_SUCCESS_RESPONSE_CODE))) {

			rc = rc + "#FaultEntity=" + UtilLib.ifNullThenEmpty(ar.getFaultEntity()) + "#ExternalFaultCode="
					+ UtilLib.ifNullThenEmpty(ar.getExternalFaultCode()) + "#ExternalFaultDescription="
					+ UtilLib.ifNullThenEmpty(ar.getExternalFaultDescription());

			debugMsg.append("\nFault Entity: ").append(UtilLib.ifNullThenEmpty(ar.getFaultEntity()))
					.append("\nExternal Fault Code: ").append(UtilLib.ifNullThenEmpty(ar.getExternalFaultCode()))
					.append("\nExternal Fault Description: ")
					.append(UtilLib.ifNullThenEmpty(ar.getExternalFaultDescription()));
		}
		rc = rc + "#ResponseCode=" + UtilLib.ifNullThenEmpty(ar != null ? ar.getResponseCode() : null)
				+ "#ResponseDescription=" + UtilLib.ifNullThenEmpty(ar != null ? ar.getResponseDescription() : null);

		tracker.add(rc);

		debugMsg.append("\nResponse Code: ").append(UtilLib.ifNullThenEmpty(ar != null ? ar.getResponseCode() : null));
		debugMsg.append("\nResponse Description: ")
				.append(UtilLib.ifNullThenEmpty(ar != null ? ar.getResponseDescription() : null)).append("\n");

		transactionDetail.setDebugMsg(debugMsg.toString());

	}

	public static String getTrailLog(ConcurrentLinkedQueue<MessageHistory> messageHistoryList) {
		StringBuilder log = new StringBuilder();
		for (MessageHistory hist : messageHistoryList) {
			for (int i = 0; i < hist.getLevel(); i++) {
				log.append("-");
			}
			log.append(hist.getLabel());
			log.append(" took " + hist.getElapsed() + "(ms)");
			log.append("\n");
		}
		return log.toString();
	}

}

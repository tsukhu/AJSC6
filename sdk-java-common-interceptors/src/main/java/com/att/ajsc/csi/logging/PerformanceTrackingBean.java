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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.att.ajsc.csilogging.util.MessageHistory;

public class PerformanceTrackingBean {

	String serviceName;
	String method;
	String startTime;
	String userID;
	String userName;
	String conversationId;
	String uniqueTransactionId;
	String originalMessageId;
	String originatorId;
	String clientApp;
	String originationSystemVersion;
	AuditRecord auditRecord;
	Object perfRecord; // a concurrent queue that holds the performance records
	int requestContentLen;
	String responseMsgSize;
	String ttl;
	String seqNumber;
	String totalInSequence;
	String clientDME2Lookup;
	String callType;
	String clientSentTime;

	public String getClientSentTime() {
		return clientSentTime;
	}

	public void setClientSentTime(String clientSentTime) {
		this.clientSentTime = clientSentTime;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	boolean isAsync;
	TransactionDetails transactionDet;

	// TODO: create separate bean to store the trail log info
	ConcurrentLinkedQueue<MessageHistory> trailLog = new ConcurrentLinkedQueue<MessageHistory>();
	Map<String, String> lastProcessedRouteMap = new ConcurrentHashMap<>();
	int level = 0;

	String authorization;

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getClientDME2Lookup() {
		return clientDME2Lookup;
	}

	public void setClientDME2Lookup(String clientDME2Lookup) {
		this.clientDME2Lookup = clientDME2Lookup;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getUniqueTransactionId() {
		return uniqueTransactionId;
	}

	public void setUniqueTransactionId(String uniqueTransactionId) {
		this.uniqueTransactionId = uniqueTransactionId;
	}

	public String getOriginalMessageId() {
		return originalMessageId;
	}

	public void setOriginalMessageId(String originalMessageId) {
		this.originalMessageId = originalMessageId;
	}

	public String getOriginatorId() {
		return originatorId;
	}

	public void setOriginatorId(String originatorId) {
		this.originatorId = originatorId;
	}

	public String getClientApp() {
		return clientApp;
	}

	public void setClientApp(String clientApp) {
		this.clientApp = clientApp;
	}

	public String getOriginationSystemVersion() {
		return originationSystemVersion;
	}

	public void setOriginationSystemVersion(String originationSystemVersion) {
		this.originationSystemVersion = originationSystemVersion;
	}

	public AuditRecord getAuditRecord() {
		return auditRecord;
	}

	public void setAuditRecord(AuditRecord auditRecord) {
		this.auditRecord = auditRecord;
	}

	public Object getPerfRecord() {
		return perfRecord;
	}

	public void setPerfRecord(Object perfRecord) {
		this.perfRecord = perfRecord;
	}

	public int getRequestContentLen() {
		return requestContentLen;
	}

	public void setRequestContentLen(int requestContentLen) {
		this.requestContentLen = requestContentLen;
	}

	public String getResponseMsgSize() {
		return responseMsgSize;
	}

	public void setResponseMsgSize(String responseMsgSize) {
		this.responseMsgSize = responseMsgSize;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public String getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(String seqNumber) {
		this.seqNumber = seqNumber;
	}

	public String getTotalInSequence() {
		return totalInSequence;
	}

	public void setTotalInSequence(String totalInSequence) {
		this.totalInSequence = totalInSequence;
	}

	public boolean isAsync() {
		return isAsync;
	}

	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}

	public TransactionDetails getTransactionDet() {
		return transactionDet;
	}

	public void setTransactionDet(TransactionDetails transactionDet) {
		this.transactionDet = transactionDet;
	}

	public ConcurrentLinkedQueue<MessageHistory> getTrailLog() {
		return trailLog;
	}

	public void setTrailLog(ConcurrentLinkedQueue<MessageHistory> trailLog) {
		this.trailLog = trailLog;
	}

	public Map<String, String> getLastProcessedRoute() {
		return lastProcessedRouteMap;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}

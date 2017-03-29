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
package com.att.ajsc.logging.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.att.ajsc.logging.json.AuditLogRecord;
import com.att.ajsc.logging.json.MajorPerformanceTxnInbound;
import com.att.ajsc.logging.json.MajorPerformanceTxnOutbound;
import com.att.ajsc.logging.json.MinorPerformanceTxnInbound;
import com.att.ajsc.logging.json.MinorPerformanceTxnOutbound;
import com.att.ajsc.logging.json.MinorPerformanceTxnPair;
import com.att.ajsc.logging.json.PerformanceLogRecord;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	public static String convertDotToJsonStringFlattened(Object jsonObject) {
		ObjectMapper jsonMapper = new ObjectMapper();

		String jsonInString = null;

		try {
			jsonInString = jsonMapper.writeValueAsString(jsonObject);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonInString;

	}

	private static void debugConvertDotToJsonStringFlattenedAndPrettyPrint(Object jsonObject) {
		ObjectMapper jsonMapper = new ObjectMapper();

		try {
			System.out.println("\n");

			// Convert object to JSON string
			String json = jsonMapper.writeValueAsString(jsonObject);
			System.out.println("Flattened View:<" + json + " >");

			System.out.println("\n");

			// Convert object to JSON string and pretty print
			json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			System.out.println("Pretty Printed View:<" + json + " >");

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Test the out; Write to the Console
	 */
	public static void main(String[] args) {
		JsonUtils.debugConvertDotToJsonStringFlattenedAndPrettyPrint(createMockAuditLogRecord());
		
		JsonUtils.debugConvertDotToJsonStringFlattenedAndPrettyPrint(createMockErrorAuditLogRecord());

		JsonUtils.debugConvertDotToJsonStringFlattenedAndPrettyPrint(createMockPerformanceLogRecord());
	}

	public static AuditLogRecord createMockAuditLogRecord() {

		AuditLogRecord auditLogRecord = new AuditLogRecord();

		auditLogRecord.setApplicationId("mywireless");


		auditLogRecord.setCluster("QA2");

		auditLogRecord.setConversationid("mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9");

		/*
		 * auditLogRecord.setElapsedTime("41988");
		 * 
		 * auditLogRecord.setExternalFaultCode(externalFaultCode);
		 * 
		 * auditLogRecord.setExternalFaultDescription(externalFaultDescription);
		 * 
		 * auditLogRecord.setFaultEntity(faultEntity);
		 * 
		 * auditLogRecord.setFaultLevel(faultLevel);
		 * 
		 * auditLogRecord.setFaultSequenceNumber(faultSequenceNumber);
		 */

		auditLogRecord.setHttpMethod("POST");

		auditLogRecord.setInitiatedTimestamp("2016-09-19 00:00:00.000");

		auditLogRecord.setNodeName(
				"ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-356904");

		auditLogRecord.setOriginalMessageId("ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4");

		auditLogRecord.setRequestURL("/twinningrelation/v1/mssi/relation");

		auditLogRecord.setResponseCode("0");

		auditLogRecord.setResponseDescription("Success");

		auditLogRecord.setTransactionName("twinningrelation-createTwinningRelation");

		auditLogRecord.setTransactionStatus("C");

		auditLogRecord.setUniqueTransactionId("ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4");

		auditLogRecord.setCluster("Q206A");

		auditLogRecord.setVtier("Amits-MacBook-Pro-2");

		return auditLogRecord;

	}

	public static AuditLogRecord createMockErrorAuditLogRecord() {

		AuditLogRecord auditLogRecord = new AuditLogRecord();

		auditLogRecord.setApplicationId("mywireless");

		auditLogRecord.setCluster("QA2");

		auditLogRecord.setConversationid("mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9");

		auditLogRecord.setElapsedTime("41988");

		auditLogRecord.setHttpMethod("POST");

		auditLogRecord.setInitiatedTimestamp("2016-09-19 00:00:00.000");

		auditLogRecord.setNodeName(
				"ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-356904");

		auditLogRecord.setOriginalMessageId("ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4");

		auditLogRecord.setRequestURL("/twinningrelation/v1/mssi/relation");

		auditLogRecord.setResponseCode("0");

		auditLogRecord.setResponseDescription("Success");


		auditLogRecord.setTransactionName("twinningrelation-createTwinningRelation");

		auditLogRecord.setTransactionStatus("C");

		auditLogRecord.setUniqueTransactionId("ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4");

		auditLogRecord.setTransactionStatus("E");

		auditLogRecord.setEndTimestamp("2016-09-19 00:00:00.000");

		auditLogRecord.setResponseCode("200");

		auditLogRecord.setResponseDescription("System Configuration Error");


		return auditLogRecord;

	}

	public static PerformanceLogRecord createMockPerformanceLogRecord() {
		PerformanceLogRecord performanceLogRecord = new PerformanceLogRecord();


		performanceLogRecord.setConversationId("mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9");

		performanceLogRecord.setHttpMethod("GET");
		
		
		performanceLogRecord.setUniqueTransactionId("transactionId_" + UUID.randomUUID().toString());
		
		performanceLogRecord.setOriginatorId("originatorId_" + UUID.randomUUID().toString());
		
		performanceLogRecord.setServiceName("InquireOrderData");
		
		performanceLogRecord.setCluster("Q23A");
		
		performanceLogRecord.setNodeName("ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-35690");
		
		performanceLogRecord.setRequestMsgSize("20");
		
		performanceLogRecord.setResponseMsgSize("10");
		
		performanceLogRecord.setResponseCode("0");
		
		performanceLogRecord.setResponseDescription("Success");
		
		performanceLogRecord.setTransactionStatus("C");

		performanceLogRecord.setMajorPerformanceTxnInbound(createMockMajorPerfTxnInbound());

		performanceLogRecord.setMajorPerformanceTxnOutbound(createMockMajorPerfTxnOutbound());

		performanceLogRecord.setMinorPerformanceTxnPairs(createMockMinorPerfTxnPairs());

		return performanceLogRecord;
	}
	
	public static PerformanceLogRecord createMockPerformanceLogRecordFailed() {
		PerformanceLogRecord performanceLogRecord = new PerformanceLogRecord();

		performanceLogRecord.setConversationId("mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9");

		performanceLogRecord.setHttpMethod("GET");
		
		performanceLogRecord.setUniqueTransactionId("transactionId_" + UUID.randomUUID().toString());
		
		performanceLogRecord.setOriginatorId("originatorId_" + UUID.randomUUID().toString());
		
		performanceLogRecord.setServiceName("InquireDeviceDetails");
		
		performanceLogRecord.setCluster("Q23A");
		
		performanceLogRecord.setNodeName("ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-35690");
		
		performanceLogRecord.setRequestMsgSize("20");
		
		performanceLogRecord.setResponseMsgSize("10");
		
		performanceLogRecord.setResponseCode("300");
		
		performanceLogRecord.setTransactionStatus("E");
		
		performanceLogRecord.setFaultEntity("CCRR");
		
		performanceLogRecord.setExternalFaultCode("-20002");
		
		performanceLogRecord.setExternalFaultDescription("Subscriber Number is invalid");
		
		performanceLogRecord.setResponseDescription("Subscriber Number is invalid");

		performanceLogRecord.setMajorPerformanceTxnInbound(createMockMajorPerfTxnInbound());

		performanceLogRecord.setMajorPerformanceTxnOutbound(createMockMajorPerfTxnOutbound());

		performanceLogRecord.setMinorPerformanceTxnPairs(createMockMinorPerfTxnPairs());

		return performanceLogRecord;
	}

	public static MajorPerformanceTxnInbound createMockMajorPerfTxnInbound() {

		MajorPerformanceTxnInbound majorPerformanceTxnInbound = new MajorPerformanceTxnInbound();

		majorPerformanceTxnInbound.setServiceName("InquireDeviceDetails");

		majorPerformanceTxnInbound.setStartTime("2016-09-19 00:00:00.000");

		majorPerformanceTxnInbound.setTransactionStatus("I");

		return majorPerformanceTxnInbound;

	}

	public static MajorPerformanceTxnOutbound createMockMajorPerfTxnOutbound() {
		MajorPerformanceTxnOutbound majorPerformanceTxnOutbound = new MajorPerformanceTxnOutbound();

		majorPerformanceTxnOutbound.setServiceName("InquireDeviceDetails");

		majorPerformanceTxnOutbound.setEndTime("2016-09-19 00:00:00.000");

		majorPerformanceTxnOutbound.setTransactionStatus("C");

		return majorPerformanceTxnOutbound;

	}

	public static Set<MinorPerformanceTxnPair> createMockMinorPerfTxnPairs() {

		Set<MinorPerformanceTxnPair> minorPerformanceTxnPairs = new HashSet<MinorPerformanceTxnPair>();

		MinorPerformanceTxnPair minorPerformanceTxnPair1 = new MinorPerformanceTxnPair();

		minorPerformanceTxnPair1.setMinorPerformanceTxnInbound(createMockMinorPerfTxnInbound());

		minorPerformanceTxnPair1.setMinorPerformanceTxnOutbound(createMockMinorPerfTxnOutbound());

		minorPerformanceTxnPairs.add(minorPerformanceTxnPair1);

		MinorPerformanceTxnPair minorPerformanceTxnPair2 = new MinorPerformanceTxnPair();

		minorPerformanceTxnPair2.setMinorPerformanceTxnInbound(createMockMinorPerfTxnInbound());

		minorPerformanceTxnPair2.setMinorPerformanceTxnOutbound(createMockMinorPerfTxnOutbound());

		minorPerformanceTxnPair2.getMinorPerformanceTxnInbound().setAdapterDataSourceType("CCRRDB");

		minorPerformanceTxnPair2.getMinorPerformanceTxnOutbound().setAdapterDataSourceType("CCRRDB");

		minorPerformanceTxnPairs.add(minorPerformanceTxnPair2);

		return minorPerformanceTxnPairs;

	}

	public static MinorPerformanceTxnInbound createMockMinorPerfTxnInbound() {

		MinorPerformanceTxnInbound minorPerformanceTxnInbound = new MinorPerformanceTxnInbound();

		minorPerformanceTxnInbound.setAdapterDataSourceType("CCRR");

		minorPerformanceTxnInbound.setServiceName("InquireDeviceDetails");

		minorPerformanceTxnInbound.setStartTime("2016-09-19 00:00:00.000");

		minorPerformanceTxnInbound.setTransactionStatus("I");

		return minorPerformanceTxnInbound;

	}

	public static MinorPerformanceTxnOutbound createMockMinorPerfTxnOutbound() {
		MinorPerformanceTxnOutbound minorPerformanceTxnOutbound = new MinorPerformanceTxnOutbound();

		minorPerformanceTxnOutbound.setAdapterDataSourceType("CCRR");

		minorPerformanceTxnOutbound.setServiceName("InquireDeviceDetails");

		minorPerformanceTxnOutbound.setEndTime("2016-09-19 00:00:15.000");

		minorPerformanceTxnOutbound.setTransactionStatus("C");

		return minorPerformanceTxnOutbound;

	}

	// @formatter:off
	/*
	 * Example AuditRecord With Error
	 */
	/*
	 *<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<AuditRecord xmlns="http://att.com/m2e/csi/logging/AuditRecord.xsd">
	    <InstanceName>ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-36588</InstanceName>
	    <ApplicationId>edstwphone</ApplicationId>
	    <OriginalMessageId>edstwphone[INQUIRE_TWINNING_RELATION].1471711691644</OriginalMessageId>
	    <UniqueTransactionId>ServiceGateway368769@q22csg1c11_d8018480-420d-4052-86c3-e9cbf8d9e435</UniqueTransactionId>
	    <OriginatorId>edstwphone</OriginatorId>
	    <Subject>CW.pub.spm2.twinningrelation-getTwinningRelationByCTN.response</Subject>
	    <ConversationId>edstwphone~CNG-CSI~5b88f5b4-63b9-4a5f-b943-44bd3985287f</ConversationId>
	    <OriginationSystemId>N/A</OriginationSystemId>
	    <OriginationSystemVersion>1</OriginationSystemVersion>
	    <OriginationSystemName>edstwphone</OriginationSystemName>
	    <SourceClass>com.att.ajsc.csi.logging.CsiLoggingUtils</SourceClass>
	    <SourceMethod>AuditRecord</SourceMethod>
	    <TransactionName>twinningrelation-getTwinningRelationByCTN</TransactionName>
	    <TransactionStatus>E</TransactionStatus>
	    <HostIPAddress>130.9.201.50</HostIPAddress>
	    <HostName>q22a-m2efed01-q101csi30m3.vci.att.com</HostName>
	    <ResponseCode>300</ResponseCode>
	    <ResponseDescription>Twinning relations Not Found</ResponseDescription>
	    <FaultTimestamp>2016-08-20T16:48:14.119Z</FaultTimestamp>
	    <FaultLevel>ERROR</FaultLevel>
	    <FaultCode>CPS.SVC-1047</FaultCode>
	    <FaultDescription>Twinning relation(s) not found.</FaultDescription>
	    <ExternalFaultCode>404</ExternalFaultCode>
	    <ExternalFaultDescription>&lt;RequestError xmlns="http://csi.cingular.com/CSI/Namespaces/Rest/RequestError.xsd"&gt;
		&lt;ServiceException&gt;
			&lt;MessageId&gt;SVC9999&lt;/MessageId&gt;
			&lt;Text&gt;An internal error has occurred&lt;/Text&gt;
		&lt;/ServiceException&gt;
		&lt;/RequestError&gt;</ExternalFaultDescription>
	    <FaultEntity>CPS</FaultEntity>
	    <InitiatedTimestamp>2016-08-20T16:48:12.034Z</InitiatedTimestamp>
	    <ElapsedTime>2087</ElapsedTime>
	    <Mode></Mode>
	    <ServiceKeyData1></ServiceKeyData1>
	    <ServiceKeyData2></ServiceKeyData2>
	    <Cluster>Q22A</Cluster>
	    <ClientApp>ServiceGateway~368769@q22csg1c11~Q22A</ClientApp>
	    <Vtier>q22a-m2efed01-q101csi30m3</Vtier>
	    <ClientIP>135.213.89.235</ClientIP>
	    <HttpMethod>GET</HttpMethod>
	    <RequestURL>/twinningrelation/v1/cloudoemsi/relations/ctn</RequestURL>
	</AuditRecord>
	 */

	/*
	 * Example of AuditRecord Successful
	 */
	/*
	 * ""2016-08-23 09:33:04,123
		 [mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9]
		 INFO
		  com.att.ajsc.csi.logging.AuditRecordLogging
		 - ************************** Audit Log **************************
		<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		<AuditRecord xmlns="http://att.com/m2e/csi/logging/AuditRecord.xsd">
		<InstanceName>ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-356904</InstanceName>
		<ApplicationId>mywireless</ApplicationId>
		<OriginalMessageId>ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4</OriginalMessageId>
		<UniqueTransactionId>ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4</UniqueTransactionId>
		<OriginatorId>mywireless</OriginatorId>
		<Subject>CW.pub.spm2.twinningrelation-createTwinningRelation.response</Subject>
		<ConversationId>mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9</ConversationId>
		<OriginationSystemId>N/A</OriginationSystemId>
		<OriginationSystemVersion>1</OriginationSystemVersion>
		<OriginationSystemName>mywireless</OriginationSystemName>
		<SourceClass>com.att.ajsc.csi.logging.CsiLoggingUtils</SourceClass>
		<SourceMethod>AuditRecord</SourceMethod>
		<TransactionName>twinningrelation-createTwinningRelation</TransactionName>
		<TransactionStatus>C</TransactionStatus>
		<HostIPAddress>130.9.201.50</HostIPAddress>
		<HostName>q22a-m2efed01-q101csi30m3.vci.att.com</HostName>
		<ResponseCode>0</ResponseCode>
		<ResponseDescription>Success</ResponseDescription>
		<InitiatedTimestamp>2016-09-19 00:00:00.000</InitiatedTimestamp>
		<ElapsedTime>41988</ElapsedTime>
		<Mode>
		</Mode>
		<ServiceKeyData1>
		</ServiceKeyData1>
		<ServiceKeyData2>
		</ServiceKeyData2>
		<Cluster>Q22A</Cluster>
		<ClientApp>ServiceGateway~764220@q22csg1c10~Q22A</ClientApp>
		<Vtier>q22a-m2efed01-q101csi30m3</Vtier>
		<ClientIP>135.213.40.137</ClientIP>
		<HttpMethod>POST</HttpMethod>
		<RequestURL>/twinningrelation/v1/mssi/relation</RequestURL>
		</AuditRecord>
	 */
	
	/*
	 * Example of Performance Log Successful
	 */
	/*
	 * ""2016-08-23 09:33:04,127 [mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9] INFO  com.att.ajsc.csi.logging.PerformanceTracking - ************************** Performance Log **************************
twinningrelation-createTwinningRelation <--serviceName
#AJSC-CSI <--clientApp
#ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-356904 <--instanceName
#mywireless~CNG-CSI~bf64f910-9dfe-4e28-b9ab-8e1335245fe9 <-csi_converstation_id
#2016-08-23T09:32:22.124-05:00 <--start_time
#mywireless <--csi_user_name
#ServiceGateway764220@q22csg1c10_fe00e1b0-1121-4ed9-82bb-61eed01c76f4 <--csi_unique_transaction_id

|Main
#I
#1471962742124
#twinningrelation-createTwinningRelation

	|InvokeAdapter
	#I
	#19351482858
	#CCRRCPS
	#getTwinningRelation

	|InvokeAdapter
	#C
	#19351483063
	#CCRRCPS
	#getTwinningRelation

	|InvokeService
	#I
	#19351483487
	#InquireSubscriberProfile

	|InvokeService
	#C
	#19351490748
	#InquireSubscriberProfile

	|InvokeAdapter
	#I
	#19351490866
	#CCRRCPS
	#getUsrTC

	|InvokeAdapter
	#C
	#19351491073
	#CCRRCPS
	#getUsrTC

	|InvokeAdapter
	#I
	#19351491176
	#CCRRCPS
	#getTwinningCode

	|InvokeAdapter
	#C
	#19351491372
	#CCRRCPS
	#getTwinningCode

	|InvokeAdapter
	#I
	#19351491447
	#CCRRCPS
	#getTwinningRelation

	|InvokeAdapter
	#C
	#19351491616
	#CCRRCPS
	#getTwinningRelation

	|InvokeAdapter
	#I
	#19351491653
	#CCRRCPS
	#getTwinningRelation

	|InvokeAdapter
	#C
	#19351491760
	#CCRRCPS
	#getTwinningRelation

	|InvokeService
	#I
	#19351492057
	#InquireAccountProfile

	|InvokeService
	#C
	#19351502348
	#InquireAccountProfile

	|InvokeService
	#I
	#19351502732
	#InquireDeviceDetails

	|InvokeService
	#C
	#19351504077
	#InquireDeviceDetails

	|InvokeAdapter
	#I
	#19351504251
	#CCRRCPS
	#getTwinningRelation

	|InvokeAdapter
	#C
	#19351504365
	#CCRRCPS
	#getTwinningRelation

	|InvokeService
	#I
	#19351504450
	#InquireAccountProfile

	|InvokeService
	#C
	#19351513726
	#InquireAccountProfile

	|InvokeService
	#I
	#19351514016
	#UpdateSubscriberProfile

	|InvokeService
	#C
	#19351520111
	#UpdateSubscriberProfile

	|InvokeAdapter
	#I
	#19351520209
	#CCRRCPS
	#updTwinningRelationDME2

	|InvokeAdapter
	#C
	#19351520406
	#CCRRCPS
	#updTwinningRelationDME2

	|InvokeAdapter
	#I
	#19351520470
	#CCRRCPS
	#deleteTwinningCode

	|InvokeAdapter
	#C
	#19351520848
	#CCRRCPS
	#deleteTwinningCode

	|InvokeAdapter
	#I
	#19351523994
	#CCRRCPS
	#insertTwinningEvent

	|InvokeAdapter
	#C
	#19351524367
	#CCRRCPS
	#insertTwinningEvent

|Main
#C
#1471962784112
#twinningrelation-createTwinningRelation

|RecordSizes
#ReqMsgSize=625
#RespMsgSize=204


#ClientDME2Lookup=http://CommonProvisioningServices-TwinningRelation-v1.nme.cpsvc.api.att.com/restservices/twinningrelation/v1/mssi/relation?version=1&envContext=TEST&Partner=mywireless&stickySelectorKey=Q22A
#ClientSentTime=2016-09-19 00:00:00.000
((MDC))#Cluster=Q22A
((MDC))#Vtier=q22a-m2efed01-q101csi30m3
((MDC))#ClientApp=ServiceGateway~764220@q22csg1c10~Q22A
XXXX #Mode=
((MDC))#InstanceName=ajsc:com.att.api.cpsvc.nme.CommonProvisioningServices-TwinningRelation-1.8.7-Q22A-q22a-m2efed01-q101csi30m3.vci.att.com-356904
((MDC))#HostIPAddress=130.9.201.50
((MDC))#HostName=q22a-m2efed01-q101csi30m3.vci.att.com
#ResponseCode=0
#ResponseDescription=Success
	 */

}

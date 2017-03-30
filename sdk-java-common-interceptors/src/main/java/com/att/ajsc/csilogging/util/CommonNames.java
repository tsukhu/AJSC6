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

public class CommonNames {
	public static final String ERROR = "ERROR";
	// Definitions for extension CSI HTTP header values
	public static final String CSI_VERSION = "X-CSI-Version";
	public static final String CSI_ORIGINAL_VERSION = "X-CSI-OriginalVersion";
	public static final String CSI_CONVERSATION_ID = "X-CSI-ConversationId";
	public static final String CSI_UNIQUE_TXN_ID = "X-CSI-UniqueTransactionId";
	public static final String CSI_MESSAGE_ID = "X-CSI-MessageId";
	public static final String CSI_TIME_TO_LIVE = "X-CSI-TimeToLive";
	public static final String CSI_SEQUENCE_NUMBER = "X-CSI-SequenceNumber";
	public static final String CSI_TOTAL_IN_SEQUENCE = "X-CSI-TotalInSequence";
	public static final String CSI_ORIGINATOR_ID = "X-CSI-OriginatorId";
	public static final String CSI_DATE_TIME_STAMP = "X-CSI-DateTimeStamp";
	public static final String CSI_CLIENT_APP = "X-CSI-ClientApp";
	public static final String CSI_CLIENT_DME2_LOOKUP = "X-CSI-ClientDME2Lookup";
	public static final String CALL_TYPE = "CALL_TYPE";

	public static final String CAET_CingularErrorCategory = "X-CAET-CingularErrorCategory";

	public static final String CAET_CingularErrorDescription = "X-CAET-CingularErrorDescription";

	public static final String CAET_FaultEntity = "X-CAET-FaultEntity";

	public static final String CAET_FaultCode = "X-CAET-FaultCode";

	public static final String CAET_FaultDesc = "X-CAET-FaultDesc";

	public static final String CAET_RestHTTPStatusCode = "X-CAET-RestHTTPStatusCode";

	public static final String CAET_RestErrorDescription = "X-CAET-RestErrorDescription";

	public static final String AJSC_CAET_APPID = "AJSC_CAET_APPID";

	public static final String AJSC_CAET_ERRORCODE = "AJSC_CAET_ERRORCODE";

	public static final String AJSC_CAET_FAULT_ENTITY = "AJSC_CAET_FAULT_ENTITY";

	public static final String AJSC_CAET_MESSAGE_TEXT = "AJSC_CAET_MESSAGE_TEXT";

	public static final String AJSC_CAET_IS_REST_SERVICE = "AJSC_CAET_IS_REST_SERVICE";

	public static final String CAET_RestErrorCode = "X-CAET-RestErrorCode";

	public static final String CAET_CingularErrorCode = "X-CAET-CingularErrorCode";

	// Headers for CAET
	public static final String CAET_FAULT_CODE = "X-CAET-FaultCode";
	public static final String CAET_FAULT_DESC = "X-CAET-FaultDesc";
	public static final String CAET_FAULT_ENTITY = "X-CAET-FaultEntity";

	// Other request headers to access
	public static final String HTTP_LOCATION = "Location";
	public static final String HTTP_AUTHORIZATION = "Authorization";
	public static final String HTTP_ACCEPT = "accept";
	public static final String JSONP = "jsonp";
	public static final String NONSP = "nonsp";

	// Definitions for content type handling and request attributes
	public static final String ERROR_BODY_TYPE = "ERROR_BODY";
	public static final String REQUEST_BODY_TYPE = "REQUEST_BODY";
	public static final String RESPONSE_BODY_TYPE = "RESPONSE_BODY";
	public static final String BODY_TYPE_XML = "XML";
	public static final String BODY_TYPE_JSON = "JSON";
	public static final String REQUEST_CONTENT_WILDCARD = "*/*";
	public static final String REQUEST_CONTENT_XML = "application/xml";
	public static final String REQUEST_CONTENT_JSON = "application/json";
	public static final String RESPONSE_CONTENT_XML = "application/xml;charset=utf-8";
	public static final String RESPONSE_CONTENT_JSON = "application/json;charset=utf-8";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String NO_CACHE = "no-cache,no-store";
	public static final String ATTR_TIME_TO_LIVE = "TIME_TO_LIVE";
	public static final String ATTR_START_TIME = "START_TIME";
	public static final String ROUTE_ENDPOINT_BEGIN_TIME = "BEGIN_TIME";
	public static final String ENDPOINT_NAME = "ENDPOINT_NAME";
	public static final String ATTR_TTL_DEFAULT = "60000";
	public static final String RESPONSE_BODY_TEXT = "RESPONSE_BODY_TEXT";
	public static final String CSI_USER_NAME = "USER_NAME";
	public static final String CSI_MOCK_USER_NAME = "ajscUser";

	public static final String CSI_PASSWORD = "PASSWORD";
	public static final int MAX_URI_LENGTH = 2048;

	// Other general stuff for schema, logging, etc.
	public static final String DOT_XSD = ".xsd";
	public static final String REQUEST_TAG = "Request";
	public static final String RESPONSE_TAG = "Response";

	public static final String INFO_TAG = "Info";

	public static final String CONTIVO_TRANSFORM_PACKAGE = "com.cingular.csi.transforms";
	public static final String DME2_TAG = "DME2";
	public static final String HYDRA_TAG = "HYDRA";
	public static final String CSI_M2E_LOGGER = "CSI_M2E_LOGGER";
	public static final String AUDIT_RECORD = "AUDIT_RECORD";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String M2E_CSI_RESTFUL = "M2ECSIRestful";
	public static final String PERF_RECORD = "PERF_RECORD";

	public static final String DOT = ".";
	public static final String EMPTY_STRING = "";

	// fault codes
	public static final String CSI_AUTH_ERROR = "100";
	public static final String CSI_SERVICE_UNAVAIL_ERROR = "200";
	public static final String CSI_DATA_ERROR = "300";
	public static final String CSI_REQUEST_XML_ERROR = "400";
	public static final String CSI_BUS_PROC_ERROR = "500";
	public static final String CSI_UNKNOWN_ERROR = "900";
	public static final String CSI_SUCCESS_RESPONSE_CODE = "0";
	public static final String CSI_SUCCESS = "Success";

	// Error numbers
	public static final String CSI_GENERIC_AUTH_ERROR = "10000000001";
	public static final String CSI_GENERIC_SERVICE_UNAVAIL_ERROR = "20000000001";
	public static final String CSI_GENERIC_REQUEST_ERROR = "40000000001";
	public static final String CSI_GENERIC_UNKNOWN_ERROR = "90000000001";

	// Interceptor constants
	public static final String REQUEST_START_TIME = "REQUEST_START_TIME";
	// con
	public static final String COMPONENT_TYPE_RESTLET = "rest";
	public static final String COMPONENT_TYPE_SERVLET = "servlet";
	public static final String SOACLOUD_NAMESPACE = "SOACLOUD_NAMESPACE";
	public static final String AUDIT_LOGGER_NAME = "AuditRecord";
	public static final String AJSC_CSI_RESTFUL = "AjscCsiRestful";
	public static final String SOURCE_CLASS = "com.att.ajsc.csi.logging.CsiLoggingUtils";
	public static final String ENABLE_TRAIL_LOGGING = "enableTrailLogging";
	public static final String ENABLE_TRAIL_SUMMARY = "enableTrailLoggingSummary";
	public static final String AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP = "AFT_DME2_CLIENT_REQ_SEND_TIMESTAMP";
	public static final String HTTP_HEADER_SERVICE_NAME = "X-CSI-ServiceName";
	public static final String HTTP_HEADER_METHOD_NAME = "X-CSI-MethodName";

	public static final String CONVERSATION_ID = "conversationId";
	public static final String UNIQUE_TXN_ID = "uniqueTransactionId";
	public static final String CORRELATION_ID = "correlationId";
	public static final String MESSAGE_ID = "messageId";
	public static final String IP_ADDRESS = "ipaddress";

	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";
	public static final String START_TIME = "startTime";
	public static final String ATT_CONVERSATION_ID = "X-ATT-ConversationId";
	public static final String ATT_MESSAGE_ID = "X-ATT-MessageId";
	public static final String ATT_ORIGINATOR_ID = "X-ATT-OriginatorId";
	public static final String ATT_TIME_TO_LIVE = "X-ATT-TimeToLive";
	public static final String ATT_UNIQUE_TXN_ID = "X-ATT-Transaction-Id";
	public static final String AUDIT_LOG = "audit_log";
	public static final String PERFORMANCE_LOG = "performance_log";
	public static final String CSI_ENABLE = "csiEnable";
	public static final String RESPONSE_ENTITY = "response_entity";
	public static final String SEND_400 = "send_400";
	public static final String REST_NAME_NORMALIZED_NO_PATTERN_MATCH = "NO MATCH";
	public static final String SERVLET_ENDPOINT = "ServletEndpoint";
	public static final String RESTLET_ENDPOINT = "RestletEndpoint";
	public static final String ROUTE_OFFER = "routeOffer";
	public static final String APP_NAME = "appName";
	public static final String APP_VERSION = "appVersion";
	public static final String RESPONSE_LENGTH = "responseLength";
	public static final String REST_NAME_HEADER="X-CSI-REST_NAME_NORMALIZED";
}

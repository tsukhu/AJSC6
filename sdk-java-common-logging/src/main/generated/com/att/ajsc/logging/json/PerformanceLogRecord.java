
package com.att.ajsc.logging.json;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * PerformanceLogRecord
 * <p>
 * The message body of an performance log
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "startTime",
    "conversationId",
    "uniqueTransactionId",
    "serviceName",
    "requestMsgSize",
    "responseMsgSize",
    "httpMethod",
    "userID",
    "originatorId",
    "cluster",
    "vtier",
    "nodeName",
    "transactionStatus",
    "responseCode",
    "responseDescription",
    "faultEntity",
    "externalFaultCode",
    "externalFaultDescription",
    "majorPerformanceTxnInbound",
    "majorPerformanceTxnOutbound",
    "minorPerformanceTxnPairs",
    "required"
})
public class PerformanceLogRecord {

    /**
     * 
     * 
     */
    @JsonProperty("startTime")
    @JsonPropertyDescription("")
    private String startTime;
    /**
     * 
     * 
     */
    @JsonProperty("conversationId")
    @JsonPropertyDescription("")
    private String conversationId;
    /**
     * 
     * 
     */
    @JsonProperty("uniqueTransactionId")
    @JsonPropertyDescription("")
    private String uniqueTransactionId;
    /**
     * 
     * 
     */
    @JsonProperty("serviceName")
    @JsonPropertyDescription("")
    private String serviceName;
    /**
     * 
     * 
     */
    @JsonProperty("requestMsgSize")
    @JsonPropertyDescription("")
    private String requestMsgSize;
    /**
     * 
     * 
     */
    @JsonProperty("responseMsgSize")
    @JsonPropertyDescription("")
    private String responseMsgSize;
    /**
     * 
     * 
     */
    @JsonProperty("httpMethod")
    @JsonPropertyDescription("")
    private String httpMethod;
    /**
     * 
     * 
     */
    @JsonProperty("userID")
    @JsonPropertyDescription("")
    private String userID;
    /**
     * 
     * 
     */
    @JsonProperty("originatorId")
    @JsonPropertyDescription("")
    private String originatorId;
    /**
     * 
     * 
     */
    @JsonProperty("cluster")
    @JsonPropertyDescription("")
    private String cluster;
    /**
     * 
     * 
     */
    @JsonProperty("vtier")
    @JsonPropertyDescription("")
    private String vtier;
    /**
     * 
     * 
     */
    @JsonProperty("nodeName")
    @JsonPropertyDescription("")
    private String nodeName;
    /**
     * 
     * 
     */
    @JsonProperty("transactionStatus")
    @JsonPropertyDescription("")
    private String transactionStatus;
    /**
     * 
     * 
     */
    @JsonProperty("responseCode")
    @JsonPropertyDescription("")
    private String responseCode;
    /**
     * 
     * 
     */
    @JsonProperty("responseDescription")
    @JsonPropertyDescription("")
    private String responseDescription;
    /**
     * 
     * 
     */
    @JsonProperty("faultEntity")
    @JsonPropertyDescription("")
    private String faultEntity;
    /**
     * 
     * 
     */
    @JsonProperty("externalFaultCode")
    @JsonPropertyDescription("")
    private String externalFaultCode;
    /**
     * 
     * 
     */
    @JsonProperty("externalFaultDescription")
    @JsonPropertyDescription("")
    private String externalFaultDescription;
    /**
     * MajorPerformanceTransactionInbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
     * 
     */
    @JsonProperty("majorPerformanceTxnInbound")
    @JsonPropertyDescription("")
    private MajorPerformanceTxnInbound majorPerformanceTxnInbound;
    /**
     * MajorPerformanceTransaction
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
     * 
     */
    @JsonProperty("majorPerformanceTxnOutbound")
    @JsonPropertyDescription("")
    private MajorPerformanceTxnOutbound majorPerformanceTxnOutbound;
    @JsonProperty("minorPerformanceTxnPairs")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<MinorPerformanceTxnPair> minorPerformanceTxnPairs = new LinkedHashSet<MinorPerformanceTxnPair>();
    @JsonProperty("required")
    private Object required;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * 
     * @return
     *     The startTime
     */
    @JsonProperty("startTime")
    public String getStartTime() {
        return startTime;
    }

    /**
     * 
     * 
     * @param startTime
     *     The startTime
     */
    @JsonProperty("startTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 
     * 
     * @return
     *     The conversationId
     */
    @JsonProperty("conversationId")
    public String getConversationId() {
        return conversationId;
    }

    /**
     * 
     * 
     * @param conversationId
     *     The conversationId
     */
    @JsonProperty("conversationId")
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * 
     * 
     * @return
     *     The uniqueTransactionId
     */
    @JsonProperty("uniqueTransactionId")
    public String getUniqueTransactionId() {
        return uniqueTransactionId;
    }

    /**
     * 
     * 
     * @param uniqueTransactionId
     *     The uniqueTransactionId
     */
    @JsonProperty("uniqueTransactionId")
    public void setUniqueTransactionId(String uniqueTransactionId) {
        this.uniqueTransactionId = uniqueTransactionId;
    }

    /**
     * 
     * 
     * @return
     *     The serviceName
     */
    @JsonProperty("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 
     * 
     * @param serviceName
     *     The serviceName
     */
    @JsonProperty("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 
     * 
     * @return
     *     The requestMsgSize
     */
    @JsonProperty("requestMsgSize")
    public String getRequestMsgSize() {
        return requestMsgSize;
    }

    /**
     * 
     * 
     * @param requestMsgSize
     *     The requestMsgSize
     */
    @JsonProperty("requestMsgSize")
    public void setRequestMsgSize(String requestMsgSize) {
        this.requestMsgSize = requestMsgSize;
    }

    /**
     * 
     * 
     * @return
     *     The responseMsgSize
     */
    @JsonProperty("responseMsgSize")
    public String getResponseMsgSize() {
        return responseMsgSize;
    }

    /**
     * 
     * 
     * @param responseMsgSize
     *     The responseMsgSize
     */
    @JsonProperty("responseMsgSize")
    public void setResponseMsgSize(String responseMsgSize) {
        this.responseMsgSize = responseMsgSize;
    }

    /**
     * 
     * 
     * @return
     *     The httpMethod
     */
    @JsonProperty("httpMethod")
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * 
     * 
     * @param httpMethod
     *     The httpMethod
     */
    @JsonProperty("httpMethod")
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * 
     * 
     * @return
     *     The userID
     */
    @JsonProperty("userID")
    public String getUserID() {
        return userID;
    }

    /**
     * 
     * 
     * @param userID
     *     The userID
     */
    @JsonProperty("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * 
     * 
     * @return
     *     The originatorId
     */
    @JsonProperty("originatorId")
    public String getOriginatorId() {
        return originatorId;
    }

    /**
     * 
     * 
     * @param originatorId
     *     The originatorId
     */
    @JsonProperty("originatorId")
    public void setOriginatorId(String originatorId) {
        this.originatorId = originatorId;
    }

    /**
     * 
     * 
     * @return
     *     The cluster
     */
    @JsonProperty("cluster")
    public String getCluster() {
        return cluster;
    }

    /**
     * 
     * 
     * @param cluster
     *     The cluster
     */
    @JsonProperty("cluster")
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    /**
     * 
     * 
     * @return
     *     The vtier
     */
    @JsonProperty("vtier")
    public String getVtier() {
        return vtier;
    }

    /**
     * 
     * 
     * @param vtier
     *     The vtier
     */
    @JsonProperty("vtier")
    public void setVtier(String vtier) {
        this.vtier = vtier;
    }

    /**
     * 
     * 
     * @return
     *     The nodeName
     */
    @JsonProperty("nodeName")
    public String getNodeName() {
        return nodeName;
    }

    /**
     * 
     * 
     * @param nodeName
     *     The nodeName
     */
    @JsonProperty("nodeName")
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * 
     * 
     * @return
     *     The transactionStatus
     */
    @JsonProperty("transactionStatus")
    public String getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * 
     * 
     * @param transactionStatus
     *     The transactionStatus
     */
    @JsonProperty("transactionStatus")
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    /**
     * 
     * 
     * @return
     *     The responseCode
     */
    @JsonProperty("responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * 
     * 
     * @param responseCode
     *     The responseCode
     */
    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * 
     * 
     * @return
     *     The responseDescription
     */
    @JsonProperty("responseDescription")
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * 
     * 
     * @param responseDescription
     *     The responseDescription
     */
    @JsonProperty("responseDescription")
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * 
     * 
     * @return
     *     The faultEntity
     */
    @JsonProperty("faultEntity")
    public String getFaultEntity() {
        return faultEntity;
    }

    /**
     * 
     * 
     * @param faultEntity
     *     The faultEntity
     */
    @JsonProperty("faultEntity")
    public void setFaultEntity(String faultEntity) {
        this.faultEntity = faultEntity;
    }

    /**
     * 
     * 
     * @return
     *     The externalFaultCode
     */
    @JsonProperty("externalFaultCode")
    public String getExternalFaultCode() {
        return externalFaultCode;
    }

    /**
     * 
     * 
     * @param externalFaultCode
     *     The externalFaultCode
     */
    @JsonProperty("externalFaultCode")
    public void setExternalFaultCode(String externalFaultCode) {
        this.externalFaultCode = externalFaultCode;
    }

    /**
     * 
     * 
     * @return
     *     The externalFaultDescription
     */
    @JsonProperty("externalFaultDescription")
    public String getExternalFaultDescription() {
        return externalFaultDescription;
    }

    /**
     * 
     * 
     * @param externalFaultDescription
     *     The externalFaultDescription
     */
    @JsonProperty("externalFaultDescription")
    public void setExternalFaultDescription(String externalFaultDescription) {
        this.externalFaultDescription = externalFaultDescription;
    }

    /**
     * MajorPerformanceTransactionInbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
     * 
     * @return
     *     The majorPerformanceTxnInbound
     */
    @JsonProperty("majorPerformanceTxnInbound")
    public MajorPerformanceTxnInbound getMajorPerformanceTxnInbound() {
        return majorPerformanceTxnInbound;
    }

    /**
     * MajorPerformanceTransactionInbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
     * 
     * @param majorPerformanceTxnInbound
     *     The majorPerformanceTxnInbound
     */
    @JsonProperty("majorPerformanceTxnInbound")
    public void setMajorPerformanceTxnInbound(MajorPerformanceTxnInbound majorPerformanceTxnInbound) {
        this.majorPerformanceTxnInbound = majorPerformanceTxnInbound;
    }

    /**
     * MajorPerformanceTransaction
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
     * 
     * @return
     *     The majorPerformanceTxnOutbound
     */
    @JsonProperty("majorPerformanceTxnOutbound")
    public MajorPerformanceTxnOutbound getMajorPerformanceTxnOutbound() {
        return majorPerformanceTxnOutbound;
    }

    /**
     * MajorPerformanceTransaction
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
     * 
     * @param majorPerformanceTxnOutbound
     *     The majorPerformanceTxnOutbound
     */
    @JsonProperty("majorPerformanceTxnOutbound")
    public void setMajorPerformanceTxnOutbound(MajorPerformanceTxnOutbound majorPerformanceTxnOutbound) {
        this.majorPerformanceTxnOutbound = majorPerformanceTxnOutbound;
    }

    /**
     * 
     * @return
     *     The minorPerformanceTxnPairs
     */
    @JsonProperty("minorPerformanceTxnPairs")
    public Set<MinorPerformanceTxnPair> getMinorPerformanceTxnPairs() {
        return minorPerformanceTxnPairs;
    }

    /**
     * 
     * @param minorPerformanceTxnPairs
     *     The minorPerformanceTxnPairs
     */
    @JsonProperty("minorPerformanceTxnPairs")
    public void setMinorPerformanceTxnPairs(Set<MinorPerformanceTxnPair> minorPerformanceTxnPairs) {
        this.minorPerformanceTxnPairs = minorPerformanceTxnPairs;
    }

    /**
     * 
     * @return
     *     The required
     */
    @JsonProperty("required")
    public Object getRequired() {
        return required;
    }

    /**
     * 
     * @param required
     *     The required
     */
    @JsonProperty("required")
    public void setRequired(Object required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(startTime).append(conversationId).append(uniqueTransactionId).append(serviceName).append(requestMsgSize).append(responseMsgSize).append(httpMethod).append(userID).append(originatorId).append(cluster).append(vtier).append(nodeName).append(transactionStatus).append(responseCode).append(responseDescription).append(faultEntity).append(externalFaultCode).append(externalFaultDescription).append(majorPerformanceTxnInbound).append(majorPerformanceTxnOutbound).append(minorPerformanceTxnPairs).append(required).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PerformanceLogRecord) == false) {
            return false;
        }
        PerformanceLogRecord rhs = ((PerformanceLogRecord) other);
        return new EqualsBuilder().append(startTime, rhs.startTime).append(conversationId, rhs.conversationId).append(uniqueTransactionId, rhs.uniqueTransactionId).append(serviceName, rhs.serviceName).append(requestMsgSize, rhs.requestMsgSize).append(responseMsgSize, rhs.responseMsgSize).append(httpMethod, rhs.httpMethod).append(userID, rhs.userID).append(originatorId, rhs.originatorId).append(cluster, rhs.cluster).append(vtier, rhs.vtier).append(nodeName, rhs.nodeName).append(transactionStatus, rhs.transactionStatus).append(responseCode, rhs.responseCode).append(responseDescription, rhs.responseDescription).append(faultEntity, rhs.faultEntity).append(externalFaultCode, rhs.externalFaultCode).append(externalFaultDescription, rhs.externalFaultDescription).append(majorPerformanceTxnInbound, rhs.majorPerformanceTxnInbound).append(majorPerformanceTxnOutbound, rhs.majorPerformanceTxnOutbound).append(minorPerformanceTxnPairs, rhs.minorPerformanceTxnPairs).append(required, rhs.required).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}

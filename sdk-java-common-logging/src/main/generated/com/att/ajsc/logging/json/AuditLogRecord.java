
package com.att.ajsc.logging.json;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * AuditLogRecord
 * <p>
 * The message body of an audit log
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "description",
    "nodeName",
    "applicationId",
    "originalMessageId",
    "uniqueTransactionId",
    "conversationid",
    "transactionName",
    "transactionStatus",
    "responseCode",
    "responseDescription",
    "endTimestamp",
    "initiatedTimestamp",
    "elapsedTime",
    "clientIp",
    "cluster",
    "vtier",
    "httpMethod",
    "requestURL",
    "required"
})
public class AuditLogRecord {

    /**
     * Record Description
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("")
    private String description;
    /**
     * Name of the node
     * 
     */
    @JsonProperty("nodeName")
    @JsonPropertyDescription("")
    private String nodeName;
    /**
     * Application Id
     * 
     */
    @JsonProperty("applicationId")
    @JsonPropertyDescription("")
    private String applicationId;
    /**
     * OriginalMessage Id
     * 
     */
    @JsonProperty("originalMessageId")
    @JsonPropertyDescription("")
    private String originalMessageId;
    /**
     * Global Unique Id of the transaction.
     * 
     */
    @JsonProperty("uniqueTransactionId")
    @JsonPropertyDescription("")
    private String uniqueTransactionId;
    /**
     * This identifier is used to label a multi-message asynchronous communication between several applications/components.  If an application is not involved in a multi-message asynchronous communication, this argument can be null.  This field is also used to correlate this record to related exception logs in GEH.
     * 
     */
    @JsonProperty("conversationid")
    @JsonPropertyDescription("")
    private String conversationid;
    /**
     * Unique name of the interaction being invoked.
     * 
     */
    @JsonProperty("transactionName")
    @JsonPropertyDescription("")
    private String transactionName;
    /**
     * The current state of the transaction lifecycle.  Must be one of the following:(INITIATED, COMPLETE, ERROR, WARN)
     * 
     */
    @JsonProperty("transactionStatus")
    @JsonPropertyDescription("")
    private String transactionStatus;
    /**
     * Public response code.
     * 
     */
    @JsonProperty("responseCode")
    @JsonPropertyDescription("")
    private String responseCode;
    /**
     * Public response description.
     * 
     */
    @JsonProperty("responseDescription")
    @JsonPropertyDescription("")
    private String responseDescription;
    /**
     * Date/Timestamp of the fault.
     * 
     */
    @JsonProperty("endTimestamp")
    @JsonPropertyDescription("")
    private String endTimestamp;
    /**
     * The FaultTimestamp from the corresponding INITIATED audit log
     * 
     */
    @JsonProperty("initiatedTimestamp")
    @JsonPropertyDescription("")
    private String initiatedTimestamp;
    /**
     * The difference between the Initiated timestamp and the complete/error timstamp in milliseconds
     * 
     */
    @JsonProperty("elapsedTime")
    @JsonPropertyDescription("")
    private String elapsedTime;
    /**
     * The client IP address
     * 
     */
    @JsonProperty("clientIp")
    @JsonPropertyDescription("")
    private String clientIp;
    /**
     * The relevant virtual cluster / route offer for tracking purposes.
     * 
     */
    @JsonProperty("cluster")
    @JsonPropertyDescription("")
    private String cluster;
    /**
     * The vtier the engine is running on, if available.
     * 
     */
    @JsonProperty("vtier")
    @JsonPropertyDescription("")
    private String vtier;
    /**
     * The HTTP method (GET, POST, DELETE, etc.)
     * 
     */
    @JsonProperty("httpMethod")
    @JsonPropertyDescription("")
    private String httpMethod;
    /**
     * The request URL.
     * 
     */
    @JsonProperty("requestURL")
    @JsonPropertyDescription("")
    private String requestURL;
    @JsonProperty("required")
    private Object required;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Record Description
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Record Description
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Name of the node
     * 
     * @return
     *     The nodeName
     */
    @JsonProperty("nodeName")
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Name of the node
     * 
     * @param nodeName
     *     The nodeName
     */
    @JsonProperty("nodeName")
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Application Id
     * 
     * @return
     *     The applicationId
     */
    @JsonProperty("applicationId")
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Application Id
     * 
     * @param applicationId
     *     The applicationId
     */
    @JsonProperty("applicationId")
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * OriginalMessage Id
     * 
     * @return
     *     The originalMessageId
     */
    @JsonProperty("originalMessageId")
    public String getOriginalMessageId() {
        return originalMessageId;
    }

    /**
     * OriginalMessage Id
     * 
     * @param originalMessageId
     *     The originalMessageId
     */
    @JsonProperty("originalMessageId")
    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    /**
     * Global Unique Id of the transaction.
     * 
     * @return
     *     The uniqueTransactionId
     */
    @JsonProperty("uniqueTransactionId")
    public String getUniqueTransactionId() {
        return uniqueTransactionId;
    }

    /**
     * Global Unique Id of the transaction.
     * 
     * @param uniqueTransactionId
     *     The uniqueTransactionId
     */
    @JsonProperty("uniqueTransactionId")
    public void setUniqueTransactionId(String uniqueTransactionId) {
        this.uniqueTransactionId = uniqueTransactionId;
    }

    /**
     * This identifier is used to label a multi-message asynchronous communication between several applications/components.  If an application is not involved in a multi-message asynchronous communication, this argument can be null.  This field is also used to correlate this record to related exception logs in GEH.
     * 
     * @return
     *     The conversationid
     */
    @JsonProperty("conversationid")
    public String getConversationid() {
        return conversationid;
    }

    /**
     * This identifier is used to label a multi-message asynchronous communication between several applications/components.  If an application is not involved in a multi-message asynchronous communication, this argument can be null.  This field is also used to correlate this record to related exception logs in GEH.
     * 
     * @param conversationid
     *     The conversationid
     */
    @JsonProperty("conversationid")
    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }

    /**
     * Unique name of the interaction being invoked.
     * 
     * @return
     *     The transactionName
     */
    @JsonProperty("transactionName")
    public String getTransactionName() {
        return transactionName;
    }

    /**
     * Unique name of the interaction being invoked.
     * 
     * @param transactionName
     *     The transactionName
     */
    @JsonProperty("transactionName")
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    /**
     * The current state of the transaction lifecycle.  Must be one of the following:(INITIATED, COMPLETE, ERROR, WARN)
     * 
     * @return
     *     The transactionStatus
     */
    @JsonProperty("transactionStatus")
    public String getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * The current state of the transaction lifecycle.  Must be one of the following:(INITIATED, COMPLETE, ERROR, WARN)
     * 
     * @param transactionStatus
     *     The transactionStatus
     */
    @JsonProperty("transactionStatus")
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    /**
     * Public response code.
     * 
     * @return
     *     The responseCode
     */
    @JsonProperty("responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Public response code.
     * 
     * @param responseCode
     *     The responseCode
     */
    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Public response description.
     * 
     * @return
     *     The responseDescription
     */
    @JsonProperty("responseDescription")
    public String getResponseDescription() {
        return responseDescription;
    }

    /**
     * Public response description.
     * 
     * @param responseDescription
     *     The responseDescription
     */
    @JsonProperty("responseDescription")
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * Date/Timestamp of the fault.
     * 
     * @return
     *     The endTimestamp
     */
    @JsonProperty("endTimestamp")
    public String getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * Date/Timestamp of the fault.
     * 
     * @param endTimestamp
     *     The endTimestamp
     */
    @JsonProperty("endTimestamp")
    public void setEndTimestamp(String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    /**
     * The FaultTimestamp from the corresponding INITIATED audit log
     * 
     * @return
     *     The initiatedTimestamp
     */
    @JsonProperty("initiatedTimestamp")
    public String getInitiatedTimestamp() {
        return initiatedTimestamp;
    }

    /**
     * The FaultTimestamp from the corresponding INITIATED audit log
     * 
     * @param initiatedTimestamp
     *     The initiatedTimestamp
     */
    @JsonProperty("initiatedTimestamp")
    public void setInitiatedTimestamp(String initiatedTimestamp) {
        this.initiatedTimestamp = initiatedTimestamp;
    }

    /**
     * The difference between the Initiated timestamp and the complete/error timstamp in milliseconds
     * 
     * @return
     *     The elapsedTime
     */
    @JsonProperty("elapsedTime")
    public String getElapsedTime() {
        return elapsedTime;
    }

    /**
     * The difference between the Initiated timestamp and the complete/error timstamp in milliseconds
     * 
     * @param elapsedTime
     *     The elapsedTime
     */
    @JsonProperty("elapsedTime")
    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * The client IP address
     * 
     * @return
     *     The clientIp
     */
    @JsonProperty("clientIp")
    public String getClientIp() {
        return clientIp;
    }

    /**
     * The client IP address
     * 
     * @param clientIp
     *     The clientIp
     */
    @JsonProperty("clientIp")
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * The relevant virtual cluster / route offer for tracking purposes.
     * 
     * @return
     *     The cluster
     */
    @JsonProperty("cluster")
    public String getCluster() {
        return cluster;
    }

    /**
     * The relevant virtual cluster / route offer for tracking purposes.
     * 
     * @param cluster
     *     The cluster
     */
    @JsonProperty("cluster")
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    /**
     * The vtier the engine is running on, if available.
     * 
     * @return
     *     The vtier
     */
    @JsonProperty("vtier")
    public String getVtier() {
        return vtier;
    }

    /**
     * The vtier the engine is running on, if available.
     * 
     * @param vtier
     *     The vtier
     */
    @JsonProperty("vtier")
    public void setVtier(String vtier) {
        this.vtier = vtier;
    }

    /**
     * The HTTP method (GET, POST, DELETE, etc.)
     * 
     * @return
     *     The httpMethod
     */
    @JsonProperty("httpMethod")
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * The HTTP method (GET, POST, DELETE, etc.)
     * 
     * @param httpMethod
     *     The httpMethod
     */
    @JsonProperty("httpMethod")
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * The request URL.
     * 
     * @return
     *     The requestURL
     */
    @JsonProperty("requestURL")
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * The request URL.
     * 
     * @param requestURL
     *     The requestURL
     */
    @JsonProperty("requestURL")
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
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
        return new HashCodeBuilder().append(description).append(nodeName).append(applicationId).append(originalMessageId).append(uniqueTransactionId).append(conversationid).append(transactionName).append(transactionStatus).append(responseCode).append(responseDescription).append(endTimestamp).append(initiatedTimestamp).append(elapsedTime).append(clientIp).append(cluster).append(vtier).append(httpMethod).append(requestURL).append(required).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AuditLogRecord) == false) {
            return false;
        }
        AuditLogRecord rhs = ((AuditLogRecord) other);
        return new EqualsBuilder().append(description, rhs.description).append(nodeName, rhs.nodeName).append(applicationId, rhs.applicationId).append(originalMessageId, rhs.originalMessageId).append(uniqueTransactionId, rhs.uniqueTransactionId).append(conversationid, rhs.conversationid).append(transactionName, rhs.transactionName).append(transactionStatus, rhs.transactionStatus).append(responseCode, rhs.responseCode).append(responseDescription, rhs.responseDescription).append(endTimestamp, rhs.endTimestamp).append(initiatedTimestamp, rhs.initiatedTimestamp).append(elapsedTime, rhs.elapsedTime).append(clientIp, rhs.clientIp).append(cluster, rhs.cluster).append(vtier, rhs.vtier).append(httpMethod, rhs.httpMethod).append(requestURL, rhs.requestURL).append(required, rhs.required).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}


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
 * MinorPerformanceTransactionInbound
 * <p>
 * Subcomponent of the performanceLogRecord; Represents the inner inbound transaction of the request
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "downstreamCallType",
    "transactionStatus",
    "startTime",
    "adapterDataSourceType",
    "serviceName",
    "required"
})
public class MinorPerformanceTxnInbound {

    /**
     * placeholder value to indicate either: InvokeAdapter or InvokeService
     * 
     */
    @JsonProperty("downstreamCallType")
    @JsonPropertyDescription("")
    private String downstreamCallType;
    /**
     * The current state of the transaction lifecycle.  Must be one of the following:(INITIATED, COMPLETE, ERROR, WARN)
     * 
     */
    @JsonProperty("transactionStatus")
    @JsonPropertyDescription("")
    private String transactionStatus;
    /**
     * Julian Data Start Time
     * 
     */
    @JsonProperty("startTime")
    @JsonPropertyDescription("")
    private String startTime;
    /**
     * Abbreviated name of the Adapter Data Source Type
     * 
     */
    @JsonProperty("adapterDataSourceType")
    @JsonPropertyDescription("")
    private String adapterDataSourceType;
    /**
     * Name of the Service Requested
     * 
     */
    @JsonProperty("serviceName")
    @JsonPropertyDescription("")
    private String serviceName;
    @JsonProperty("required")
    private Object required;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * placeholder value to indicate either: InvokeAdapter or InvokeService
     * 
     * @return
     *     The downstreamCallType
     */
    @JsonProperty("downstreamCallType")
    public String getDownstreamCallType() {
        return downstreamCallType;
    }

    /**
     * placeholder value to indicate either: InvokeAdapter or InvokeService
     * 
     * @param downstreamCallType
     *     The downstreamCallType
     */
    @JsonProperty("downstreamCallType")
    public void setDownstreamCallType(String downstreamCallType) {
        this.downstreamCallType = downstreamCallType;
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
     * Julian Data Start Time
     * 
     * @return
     *     The startTime
     */
    @JsonProperty("startTime")
    public String getStartTime() {
        return startTime;
    }

    /**
     * Julian Data Start Time
     * 
     * @param startTime
     *     The startTime
     */
    @JsonProperty("startTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Abbreviated name of the Adapter Data Source Type
     * 
     * @return
     *     The adapterDataSourceType
     */
    @JsonProperty("adapterDataSourceType")
    public String getAdapterDataSourceType() {
        return adapterDataSourceType;
    }

    /**
     * Abbreviated name of the Adapter Data Source Type
     * 
     * @param adapterDataSourceType
     *     The adapterDataSourceType
     */
    @JsonProperty("adapterDataSourceType")
    public void setAdapterDataSourceType(String adapterDataSourceType) {
        this.adapterDataSourceType = adapterDataSourceType;
    }

    /**
     * Name of the Service Requested
     * 
     * @return
     *     The serviceName
     */
    @JsonProperty("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Name of the Service Requested
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
        return new HashCodeBuilder().append(downstreamCallType).append(transactionStatus).append(startTime).append(adapterDataSourceType).append(serviceName).append(required).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MinorPerformanceTxnInbound) == false) {
            return false;
        }
        MinorPerformanceTxnInbound rhs = ((MinorPerformanceTxnInbound) other);
        return new EqualsBuilder().append(downstreamCallType, rhs.downstreamCallType).append(transactionStatus, rhs.transactionStatus).append(startTime, rhs.startTime).append(adapterDataSourceType, rhs.adapterDataSourceType).append(serviceName, rhs.serviceName).append(required, rhs.required).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}


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
 * MajorPerformanceTransaction
 * <p>
 * Subcomponent of the performanceLogRecord; Represents the outer transaction of the request
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "transactionStatus",
    "endTime",
    "serviceName",
    "required"
})
public class MajorPerformanceTxnOutbound {

    /**
     * The current state of the transaction lifecycle.  Must be one of the following:(INITIATED, COMPLETE, ERROR, WARN)
     * 
     */
    @JsonProperty("transactionStatus")
    @JsonPropertyDescription("")
    private String transactionStatus;
    /**
     * Julian Data End Time
     * 
     */
    @JsonProperty("endTime")
    @JsonPropertyDescription("")
    private String endTime;
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
     * Julian Data End Time
     * 
     * @return
     *     The endTime
     */
    @JsonProperty("endTime")
    public String getEndTime() {
        return endTime;
    }

    /**
     * Julian Data End Time
     * 
     * @param endTime
     *     The endTime
     */
    @JsonProperty("endTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
        return new HashCodeBuilder().append(transactionStatus).append(endTime).append(serviceName).append(required).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MajorPerformanceTxnOutbound) == false) {
            return false;
        }
        MajorPerformanceTxnOutbound rhs = ((MajorPerformanceTxnOutbound) other);
        return new EqualsBuilder().append(transactionStatus, rhs.transactionStatus).append(endTime, rhs.endTime).append(serviceName, rhs.serviceName).append(required, rhs.required).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}

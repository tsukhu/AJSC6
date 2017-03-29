
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
 * MinorPerformanceTxnPair
 * <p>
 * Subcomponent of the performanceLogRecord; Represents a pair of inner transactions of the request
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "minorPerformanceTxnInbound",
    "minorPerformanceTxnOutbound",
    "required"
})
public class MinorPerformanceTxnPair {

    /**
     * MinorPerformanceTransactionInbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the inner inbound transaction of the request
     * 
     */
    @JsonProperty("minorPerformanceTxnInbound")
    @JsonPropertyDescription("")
    private MinorPerformanceTxnInbound minorPerformanceTxnInbound;
    /**
     * MinorPerformanceTransactionOutbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the inner outbound transaction of the request
     * 
     */
    @JsonProperty("minorPerformanceTxnOutbound")
    @JsonPropertyDescription("")
    private MinorPerformanceTxnOutbound minorPerformanceTxnOutbound;
    @JsonProperty("required")
    private Object required;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * MinorPerformanceTransactionInbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the inner inbound transaction of the request
     * 
     * @return
     *     The minorPerformanceTxnInbound
     */
    @JsonProperty("minorPerformanceTxnInbound")
    public MinorPerformanceTxnInbound getMinorPerformanceTxnInbound() {
        return minorPerformanceTxnInbound;
    }

    /**
     * MinorPerformanceTransactionInbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the inner inbound transaction of the request
     * 
     * @param minorPerformanceTxnInbound
     *     The minorPerformanceTxnInbound
     */
    @JsonProperty("minorPerformanceTxnInbound")
    public void setMinorPerformanceTxnInbound(MinorPerformanceTxnInbound minorPerformanceTxnInbound) {
        this.minorPerformanceTxnInbound = minorPerformanceTxnInbound;
    }

    /**
     * MinorPerformanceTransactionOutbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the inner outbound transaction of the request
     * 
     * @return
     *     The minorPerformanceTxnOutbound
     */
    @JsonProperty("minorPerformanceTxnOutbound")
    public MinorPerformanceTxnOutbound getMinorPerformanceTxnOutbound() {
        return minorPerformanceTxnOutbound;
    }

    /**
     * MinorPerformanceTransactionOutbound
     * <p>
     * Subcomponent of the performanceLogRecord; Represents the inner outbound transaction of the request
     * 
     * @param minorPerformanceTxnOutbound
     *     The minorPerformanceTxnOutbound
     */
    @JsonProperty("minorPerformanceTxnOutbound")
    public void setMinorPerformanceTxnOutbound(MinorPerformanceTxnOutbound minorPerformanceTxnOutbound) {
        this.minorPerformanceTxnOutbound = minorPerformanceTxnOutbound;
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
        return new HashCodeBuilder().append(minorPerformanceTxnInbound).append(minorPerformanceTxnOutbound).append(required).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MinorPerformanceTxnPair) == false) {
            return false;
        }
        MinorPerformanceTxnPair rhs = ((MinorPerformanceTxnPair) other);
        return new EqualsBuilder().append(minorPerformanceTxnInbound, rhs.minorPerformanceTxnInbound).append(minorPerformanceTxnOutbound, rhs.minorPerformanceTxnOutbound).append(required, rhs.required).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}

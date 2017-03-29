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
package com.att.ajsc.common.dto;

/**
 * A 'where' entry that the findWithFilters database search uses to map
 * desired properties and values into a query.
 */
public class WhereClause {
	
	private String fieldName;
	private String operator;
	private String value;
	
	public WhereClause() { }
	
	/**
	 * This constructor was created to satisfy jax-rs specification that
	 * states that an object must have a constructor that accepts a single 
	 * String argument. This argument must be in the following format:
	 * &lt;fieldName&gt;:&lt;operator&gt;:&lt;value&gt;. For example, 'priority:&lt;=:3'.
	 * 
	 * @param fieldOperatorAndValue the field name and isAsc in string format
	 */
	public WhereClause(String fieldOperatorAndValue) {
		String[] split = fieldOperatorAndValue.split("\\:");
		
		setFieldName(split[0]);
		setOperator(split[1]);
		setValue(split[2]);
	}

	public WhereClause(String fieldName, String operator, String value) {
		setFieldName(fieldName);
		setOperator(operator);
		setValue(value);
	}
	
	/**
	 * The field name of the filter entry; signifies an attribute's property name.
	 * 
	 * <p>
	 * For example, "id", "name", "created", etc.
	 * </p>
	 * 
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * The field name of the filter entry; signifies an attribute's property name.
	 * 
	 * <p>
	 * For example, "id", "name", "created", etc.
	 * </p>
	 * 
	 * @param fieldName the fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	/**
	 * The operator that is used to construct the where clause of a database query.
	 * 
	 * <p>
	 * For example, "&lt;", "&gt;", "=", "like", etc.
	 * </p>
	 * 
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * The operator that is used to construct the where clause of a database query.
	 * 
	 * <p>
	 * For example, "&lt;", "&gt;", "=", "like", etc.
	 * </p>
	 * 
	 * @param operator the operator
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * The value of the filter entry. It signifies an attribute's property value
	 * to match based on the operator. It's injected into a JPA query so values
	 * should follow that format.
	 * 
	 * <p>
	 * For example, a numeric value "1", a string "Object Name Here",
	 * timestamp "{ts '2014-04-01 12-45-52.325'}", etc.
	 * </p>
	 * 
	 * @return the value as a String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * The value of the filter entry; signifies an attribute's property value.
	 * 
	 * @param value the value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "WhereClause = {"
				+ "fieldName = " + fieldName + ", "
				+ "operator = " + operator + ", "
				+ "value = " + value 
				+ "}";
	}
}

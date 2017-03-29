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
 *	This class is used by the GenericDAO to create custom jpql queries.
 */
public class OrderBy {
	private String fieldName;
	private Boolean isDesc;

	public OrderBy() { }
	
	/**
	 * This constructor was created to satisfy jax-rs specification that
	 * states that an object must have a constructor that accepts a single 
	 * String argument. This argument must be in the following format:
	 * &lt;fieldName&gt;:&lt;(asc or desc)&gt;. For example, 'created:desc'.
	 * If the asc or desc part of the string is not specified, then the 
	 * we will default to asc.
	 * 
	 * @param fieldNameAndIsAsc the field name and isAsc in string format
	 */
	public OrderBy(String fieldNameAndIsAsc) {
		// Split the string based on the ':' delimiter.
		String[] split = fieldNameAndIsAsc.split("\\:");
		
		// Set the fieldName
		String fieldName = split[0];
		this.fieldName = fieldName;
		
		// Set the isAsc boolean
		Boolean isDesc = false;
		if (split.length > 1) {
			if (split[1].equalsIgnoreCase("desc"))
				isDesc = true;
		}
		this.isDesc = isDesc;
	}

	/**
	 * The field name for which the ordering will be based on.
	 * 
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * The field name for which the ordering will be based on.
	 * 
	 * @param fieldName the fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * A boolean to determine whether to order in ascending or
	 * descending fashion. True is for descending and false represents
	 * ascending.
	 * 
	 * @return the isDesc boolean
	 */
	public boolean isDesc() {
		return isDesc;
	}

	/**
	 * A boolean to determine whether to order in ascending or
	 * descending fashion. True is for descending and false represents
	 * ascending.
	 * 
	 * @param isDesc the isDesc boolean
	 */
	public void setIsDesc(boolean isDesc) {
		this.isDesc = isDesc;
	}
}

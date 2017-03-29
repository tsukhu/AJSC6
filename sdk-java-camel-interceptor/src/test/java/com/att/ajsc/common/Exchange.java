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
package com.att.ajsc.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.spi.UnitOfWork;

public class Exchange implements org.apache.camel.Exchange {

	private Map<String, Object> property = new HashMap<String, Object>();
	private Endpoint fromEndpoint;
	private Message in;
	private Message out;

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public ExchangePattern getPattern() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setPattern(ExchangePattern pattern) {

	}

	public Object getProperty(String name) {
		return property.get(name);
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Object getProperty(String name, Object defaultValue) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public <T> T getProperty(String name, Class<T> type) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public <T> T getProperty(String name, Object defaultValue, Class<T> type) {
		return null;
	}

	public void setProperty(String name, Object value) {
		property.put(name, value);
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Object removeProperty(String name) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean removeProperties(String pattern) {
		property.remove(pattern);
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean removeProperties(String pattern, String... excludePatterns) {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Map<String, Object> getProperties() {
		return null;
	}

	@Override
	public boolean hasProperties() {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Message getIn() {
		return in;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public <T> T getIn(Class<T> type) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setIn(Message in) {
		this.in = in;

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Message getOut() {
		return out;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public <T> T getOut(Class<T> type) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean hasOut() {
		return false;
	}

	public void setOut(Message out) {
		this.out = out;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Exception getException() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public <T> T getException(Class<T> type) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setException(Throwable t) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean isFailed() {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean isTransacted() {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Boolean isExternalRedelivered() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean isRollbackOnly() {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public CamelContext getContext() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public org.apache.camel.Exchange copy() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public org.apache.camel.Exchange copy(boolean safeCopy) {
		return null;
	}

	public Endpoint getFromEndpoint() {
		return fromEndpoint;
	}

	public void setFromEndpoint(Endpoint fromEndpoint) {
		this.fromEndpoint = fromEndpoint;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getFromRouteId() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setFromRouteId(String fromRouteId) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public UnitOfWork getUnitOfWork() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setUnitOfWork(UnitOfWork unitOfWork) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getExchangeId() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setExchangeId(String id) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void addOnCompletion(Synchronization onCompletion) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean containsOnCompletion(Synchronization onCompletion) {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void handoverCompletions(org.apache.camel.Exchange target) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public List<Synchronization> handoverCompletions() {
		return null;
	}

}

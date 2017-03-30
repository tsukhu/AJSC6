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
package com.att.ajsc.methodmapper.common;

public class RouteMatch {

	private int matchStrength;
	private String service;
	private HttpMethod httpMethod;
	private String logicalMethod;
	private String matchUri;
	private String requestURI;

	public RouteMatch(String service, HttpMethod httpMethod, String logicalMethod, String matchUri, String requestUri,
			int matchStrength) {
		super();
		this.service = service;
		this.httpMethod = httpMethod;
		this.logicalMethod = logicalMethod;
		this.matchUri = matchUri;
		this.requestURI = requestUri;
		this.matchStrength = matchStrength;
	}

	/**
	 * @return the httpMethod
	 */
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @return the target
	 */
	public String getLogicalMethod() {
		return logicalMethod;
	}

	/**
	 * @return the matchUri
	 */
	public String getMatchUri() {
		return matchUri;
	}

	/**
	 * @return the requestUri
	 */
	public String getRequestURI() {
		return requestURI;
	}

	public String getService() {
		return service;
	}

	public int getMatchStrength() {
		return matchStrength;
	}

}

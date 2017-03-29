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

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class Request implements ContainerRequestContext {

	private Map<String, Object> properties = new HashMap<String, Object>();
	private Response response;

	public Object getProperty(String name) {
		return properties.get(name);
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Collection<String> getPropertyNames() {
		return null;
	}

	public void setProperty(String name, Object object) {
		properties.put(name, object);
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void removeProperty(String name) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public UriInfo getUriInfo() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setRequestUri(URI requestUri) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setRequestUri(URI baseUri, URI requestUri) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public javax.ws.rs.core.Request getRequest() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getMethod() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setMethod(String method) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public MultivaluedMap<String, String> getHeaders() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getHeaderString(String name) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Date getDate() {

		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Locale getLanguage() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public int getLength() {
		return 0;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public MediaType getMediaType() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public List<MediaType> getAcceptableMediaTypes() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public List<Locale> getAcceptableLanguages() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Map<String, Cookie> getCookies() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean hasEntity() {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public InputStream getEntityStream() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setEntityStream(InputStream input) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public SecurityContext getSecurityContext() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setSecurityContext(SecurityContext context) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void abortWith(Response response) {
		this.response = response;

	}

	public int getErorCode() {
		if (response != null) {
			return response.getStatus();
		}
		return 0;
	}

}

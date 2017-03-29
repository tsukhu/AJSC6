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

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.StatusType;

public class Response implements ContainerResponseContext {

	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int code) {
		status = code;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public StatusType getStatusInfo() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setStatusInfo(StatusType statusInfo) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public MultivaluedMap<String, Object> getHeaders() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public MultivaluedMap<String, String> getStringHeaders() {
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
	public Set<String> getAllowedMethods() {
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
	public Map<String, NewCookie> getCookies() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public EntityTag getEntityTag() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Date getLastModified() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public URI getLocation() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Set<Link> getLinks() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean hasLink(String relation) {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */public Link getLink(String relation) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Builder getLinkBuilder(String relation) {
		return null;
	}

	public boolean hasEntity() {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Object getEntity() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Class<?> getEntityClass() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Type getEntityType() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setEntity(Object entity) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Annotation[] getEntityAnnotations() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public OutputStream getEntityStream() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setEntityStream(OutputStream outputStream) {

	}

}

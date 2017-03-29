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
package com.att.ajsc.common.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.att.ajsc.common.error.StatusResponse;

public abstract class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	protected List<StatusResponse> statuses = new ArrayList<StatusResponse>();

	public ServiceException(int code) {
		this(code, null);
	}
	
	public ServiceException(int code, String message) {
		this(code, message, null);
	}
	
	public ServiceException(int code, String message, String requestBody) {
		super(message);
		
		StatusResponse status = new StatusResponse();
		status.setCode(code);
		status.setMessage(message);
		status.setRequestBody(requestBody);
		statuses.add(status);
	}
	
	public void addStatus(StatusResponse status) {
		statuses.add(status);
	}
	
	public int getOverallStatusCode() {
		int highestCode = 0;
		
		for(StatusResponse status : statuses) {
			if(status.getCode() > highestCode) {
				highestCode = status.getCode();
			}
		}
		
		return highestCode;
	}
	
	public Response toResponse() {
		int overallStatusCode = getOverallStatusCode();
		
		Response response = Response.status(overallStatusCode)
        		.entity(statuses)
        		.type(MediaType.APPLICATION_JSON)
        		.build();
		
		return response;
	}
	
	public WebApplicationException getRestException() {
    	return new WebApplicationException(toResponse());
	}
	
	public String toString() {
		return statuses.toString();
	}
}

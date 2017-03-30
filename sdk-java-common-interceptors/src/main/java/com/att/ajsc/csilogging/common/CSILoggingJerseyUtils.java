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
package com.att.ajsc.csilogging.common;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import com.att.ajsc.methodmapper.common.MethodMapperConstants;

public class CSILoggingJerseyUtils {

	public static void setRequestAttributesFromRequestContext(ContainerRequestContext requestContext,
			HttpServletRequest request) {
		if (requestContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_SERVICE_NAME) != null) {
			request.setAttribute(MethodMapperConstants.HTTP_HEADER_SERVICE_NAME,
					requestContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_SERVICE_NAME));
		}
		if (requestContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_METHOD_NAME) != null) {
			request.setAttribute(MethodMapperConstants.HTTP_HEADER_METHOD_NAME,
					requestContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_METHOD_NAME));
		}
		if (requestContext.getHeaderString(MethodMapperConstants.REST_NAME_NORMALIZED_HEADER) != null) {
			request.setAttribute(MethodMapperConstants.REST_NAME_NORMALIZED_HEADER,
					requestContext.getHeaderString(MethodMapperConstants.REST_NAME_NORMALIZED_HEADER));
		}
	}

	public static void setRequestAttributesFromResponseContext(ContainerResponseContext responseContext,
			HttpServletRequest request) {

		if (responseContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_SERVICE_NAME) != null) {
			request.setAttribute(MethodMapperConstants.HTTP_HEADER_SERVICE_NAME,
					responseContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_SERVICE_NAME));
		}
		if (responseContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_METHOD_NAME) != null) {
			request.setAttribute(MethodMapperConstants.HTTP_HEADER_METHOD_NAME,
					responseContext.getHeaderString(MethodMapperConstants.HTTP_HEADER_METHOD_NAME));
		}
		if (responseContext.getHeaderString(MethodMapperConstants.REST_NAME_NORMALIZED_HEADER) != null) {
			request.setAttribute(MethodMapperConstants.REST_NAME_NORMALIZED_HEADER,
					responseContext.getHeaderString(MethodMapperConstants.REST_NAME_NORMALIZED_HEADER));
		}
	}

}

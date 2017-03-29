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
package com.att.ajsc.introscope.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import com.att.ajsc.common.si.interceptors.AjscPreInterceptor;
import com.att.ajsc.introscope.IntroscopeEventNotifierImpl;

public class IntroscopeSiPreInterceptor extends AjscPreInterceptor {

	public static final String TRANSACTION_ID = "X-ATT-Transaction-Id";
	public static final String MOCK_USER_NAME = "ajscUser";
	public static final String CONVERSATION_ID = "X-ATT-ConversationId";
	public static final String USER_ID = "x-UserId";
	public static final String SERVICE_NAME = "X-CSI-MethodName";


	@Context
	private HttpServletRequest servletRequest;

	public IntroscopeSiPreInterceptor() {
		setPosition(Integer.MIN_VALUE+3);
	}

	@Override
	public Message<?> allowOrReject(Message<?> message, MessageChannel channel) {
		introscopeEntry(message);
		return message;
	}

	/**
	 * 
	 * @param message
	 */
	private void introscopeEntry(Message<?> message) {
		String conversationId = null;
		String serviceName = "N/A";
		String uniqueTransactionId = null;
		String userNme = null;

		if (message.getHeaders().get(SERVICE_NAME) != null) {
			serviceName = message.getHeaders().get(SERVICE_NAME).toString();
		}
		if (message.getHeaders().get(CONVERSATION_ID) != null) {
			conversationId = message.getHeaders().get(CONVERSATION_ID).toString();
		}
		if (message.getHeaders().get(USER_ID) != null) {
			userNme = String.valueOf(message.getHeaders().get(USER_ID));
		} else {
			userNme = MOCK_USER_NAME;
		}
		uniqueTransactionId = message.getHeaders().get(TRANSACTION_ID).toString();

		IntroscopeEventNotifierImpl.instrumentIntroscopeSynchEntryPoint(serviceName, conversationId,
				uniqueTransactionId, userNme);
	}

}

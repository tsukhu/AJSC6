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

import org.apache.camel.Exchange;

import com.att.ajsc.common.interceptors.AjscPostInterceptor;
import com.att.ajsc.introscope.IntroscopeEventNotifierImpl;
import com.att.ajsc.introscope.IntroscopeUtil;

public class IntroscopeCamelPostInterceptor extends AjscPostInterceptor {

	public static final String TRANSACTION_ID = "X-ATT-Transaction-Id";
	public static final String MOCK_USER_NAME = "ajscUser";
	public static final String CONVERSATION_ID = "X-ATT-ConversationId";
	public static final String ORIGINATOR_ID = "X-ATT-OriginatorId";
	public static final String USER_ID = "x-UserId";

	public IntroscopeCamelPostInterceptor() {
		setPosition(Integer.MAX_VALUE - 2);
	}

	@Override
	public boolean allowOrReject(Exchange exchange) {

		introscopeExit(exchange);

		return true;
	}

	/**
	 * 
	 * @param exchange
	 */
	private void introscopeExit(Exchange exchange) {
		String conversationId = null;
		String serviceName = "N/A";
		String uniqueTransactionId = null;
		String userNme = null;

		serviceName = IntroscopeUtil.getServiceName(exchange);
		if (exchange.getIn().getHeader(CONVERSATION_ID) != null) {
			conversationId = exchange.getIn().getHeader(CONVERSATION_ID).toString();
		}
		if (exchange.getIn().getHeader(USER_ID) != null) {
			userNme = String.valueOf(exchange.getIn().getHeader(USER_ID));
		} else {
			userNme = MOCK_USER_NAME;
		}
		uniqueTransactionId = exchange.getIn().getHeader(TRANSACTION_ID).toString();
		IntroscopeEventNotifierImpl.instrumentIntroscopeSynchExitPoint(serviceName, conversationId, uniqueTransactionId,
				userNme);
	}

}

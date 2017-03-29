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
package com.att.ajsc.common.utility;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.error.HttpErrorCode;
import com.att.ajsc.common.exception.ServerErrorException;
import com.att.ajsc.common.exception.ServiceException;

@Component
public class TransactionUtility {
	
	
	private static Logger log = LoggerFactory.getLogger(TransactionUtility.class);
	private static final String TRANSACTION_TIMEOUT_IN_SECONDS = "serviceName.transaction.timeoutInSeconds";
	
	@Value("${" + TRANSACTION_TIMEOUT_IN_SECONDS + ":900}")
	private Integer transactionTimeoutInSeconds;
	
	public UserTransaction getUserTransaction() throws ServiceException {
		Context ctx = null;
		UserTransaction utx = null;
		try {
			ctx = new InitialContext();
			utx = (UserTransaction) ctx.lookup("javax.transaction.UserTransaction");
			utx.setTransactionTimeout(transactionTimeoutInSeconds);
		} catch (Exception e) {
			log.error("Failed to get UserTransaction from web context.", e);
			throw new ServerErrorException(HttpErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.name());
		}
		
		return utx;
	}
}

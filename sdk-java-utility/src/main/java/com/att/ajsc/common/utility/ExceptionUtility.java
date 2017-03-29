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

import javax.ws.rs.core.Response.Status;

import com.att.ajsc.common.error.ErrorCode;
import com.att.ajsc.common.error.ErrorDetails;
import com.att.ajsc.common.error.StatusResponse;
import com.att.ajsc.common.exception.BadRequestException;
import com.att.ajsc.common.exception.ConflictException;
import com.att.ajsc.common.exception.NotFoundException;
import com.att.ajsc.common.exception.ServerErrorException;
import com.att.ajsc.common.exception.ServiceException;

public class ExceptionUtility {

	public static ServiceException getException(int errorCode, String errorText) {
		
		ServiceException exception = null;
		if (errorCode == Status.BAD_REQUEST.getStatusCode())
			exception = new BadRequestException(errorText);
		else if (errorCode == Status.NOT_FOUND.getStatusCode())
			exception = new NotFoundException(errorText);
		else if (errorCode == Status.CONFLICT.getStatusCode())
			exception = new ConflictException(errorText);
		else
			exception = new ServerErrorException(errorText);
		
		return exception;
	}

	public static StatusResponse getFaultType(ErrorDetails error, Throwable cause) {
		
		String errorText = error.getMessage() + ": ";
		int errorCode = ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code();
		
		try {
			// First, we'll sift through all of the exception layers until we get the root cause.
			Throwable serverCause = cause.getCause();
			Throwable persistenceCause = serverCause.getCause();
			Throwable rootCause = persistenceCause.getCause();
			
			errorText += rootCause.getMessage();
			if (errorText.contains("ORA-00001"))
				errorCode = ErrorCode.ERROR_400_BAD_REQUEST.code();
			else if (errorText.contains("ORA-00904"))
				errorCode = ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code();
			else if (errorText.contains("ORA-00936"))
				errorCode = ErrorCode.ERROR_400_BAD_REQUEST.code();
			else if (errorText.contains("ORA-00942"))
				errorCode = ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code();
			else if (errorText.contains("ORA-01400"))
				errorCode = ErrorCode.ERROR_400_BAD_REQUEST.code();
			else if (errorText.contains("ORA-01407"))
				errorCode = ErrorCode.ERROR_400_BAD_REQUEST.code();
			else if (errorText.contains("ORA-02049"))
				errorCode = ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code();
			else if (errorText.contains("ORA-02289"))
				errorCode = ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code();
			else if (errorText.contains("ORA-02291"))
				errorCode = ErrorCode.ERROR_400_BAD_REQUEST.code();
			else if (errorText.contains("ORA-02292"))
				errorCode = ErrorCode.ERROR_400_BAD_REQUEST.code();
			else
				errorCode = ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code();
		} catch (Throwable th) {
			errorText += cause.getMessage();
		}
		
		StatusResponse status = new StatusResponse();
		status.setCode(errorCode);
		status.setMessage(errorText);
		
		return status;
	}
}

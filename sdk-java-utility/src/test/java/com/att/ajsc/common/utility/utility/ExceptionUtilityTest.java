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
package com.att.ajsc.common.utility.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.att.ajsc.common.error.ErrorCode;
import com.att.ajsc.common.error.ErrorDetails;
import com.att.ajsc.common.error.StatusResponse;
import com.att.ajsc.common.exception.BadRequestException;
import com.att.ajsc.common.exception.ConflictException;
import com.att.ajsc.common.exception.NotFoundException;
import com.att.ajsc.common.exception.ServerErrorException;
import com.att.ajsc.common.exception.ServiceException;
import com.att.ajsc.common.utility.ExceptionUtility;

public class ExceptionUtilityTest {

	@Test
	public void testGetBadRequestException() {
		int errorCode = Status.BAD_REQUEST.getStatusCode();
		String originalErrorText = "Bad Request ";
		String errorText = "101";

		ServiceException serviceException = ExceptionUtility.getException(
				errorCode, errorText);
		assertNotNull(serviceException);
		assertTrue(serviceException instanceof BadRequestException);

		assertEquals(originalErrorText + errorText,
				serviceException.getMessage());
	}
	
	@Test
	public void testGetNotFoundException() {
		int errorCode = Status.NOT_FOUND.getStatusCode();
		String originalErrorText = "Not Found ";
		String errorText = "102";

		ServiceException serviceException = ExceptionUtility.getException(
				errorCode, errorText);
		assertNotNull(serviceException);
		assertTrue(serviceException instanceof NotFoundException);

		assertEquals(originalErrorText + errorText,
				serviceException.getMessage());
	}
	
	@Test
	public void testGetConflictException() {
		int errorCode = Status.CONFLICT.getStatusCode();
		String originalErrorText = "Data conflicting ";
		String errorText = "103";

		ServiceException serviceException = ExceptionUtility.getException(
				errorCode, errorText);
		assertNotNull(serviceException);
		assertTrue(serviceException instanceof ConflictException);

		assertEquals(originalErrorText + errorText,
				serviceException.getMessage());
	}
	
	@Test
	public void testGetInternalServerErrorException() {
		int errorCode = 1010101; // Testing a random Integer
		String errorText = "104";

		ServiceException serviceException = ExceptionUtility.getException(
				errorCode, errorText);
		assertNotNull(serviceException);
		assertTrue(serviceException instanceof ServerErrorException);

		assertEquals(errorText, serviceException.getMessage());
	}

	@Test
	public void testGetFaultTypeBadRequest00001() {
		
		String errorMsg = "ORA-00001";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_400_BAD_REQUEST.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}

	@Test
	public void testGetFaultTypeInternalServerError00904() {
		
		String errorMsg = "ORA-00904";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}
	
	@Test
	public void testGetFaultTypeBadRequest00936() {
		
		String errorMsg = "ORA-00936";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_400_BAD_REQUEST.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}

	@Test
	public void testGetFaultTypeInternalServerError00942() {
		
		String errorMsg = "ORA-00942";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}
	
	@Test
	public void testGetFaultTypeBadRequest01400() {
		
		String errorMsg = "ORA-01400";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_400_BAD_REQUEST.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}
	
	@Test
	public void testGetFaultTypeBadRequest01407() {
		
		String errorMsg = "ORA-01407";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_400_BAD_REQUEST.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}

	@Test
	public void testGetFaultTypeInternalServerError02049() {
		
		String errorMsg = "ORA-02049";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}

	@Test
	public void testGetFaultTypeInternalServerError02289() {
		
		String errorMsg = "ORA-02289";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}
	
	@Test
	public void testGetFaultTypeBadRequest02291() {
		
		String errorMsg = "ORA-02291";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_400_BAD_REQUEST.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}
	
	@Test
	public void testGetFaultTypeBadRequest02292() {
		
		String errorMsg = "ORA-02292";
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_400_BAD_REQUEST.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}

	@Test
	public void testGetFaultTypeInternalServerError09870() {
		
		String errorMsg = "ORA-09870"; // Testing a random error message
		//create a cause based on errorMsg;
		Throwable cause = createCause(errorMsg);
		
		StatusResponse status = ExceptionUtility.getFaultType(ErrorDetails.FAILED_TO_SAVE_ENTITY, cause);
		assertNotNull(status);

		assertEquals(ErrorCode.ERROR_500_INTERNAL_SERVER_ERROR.code(), status.getCode());
		assertEquals(ErrorDetails.FAILED_TO_SAVE_ENTITY.getMessage()+ ": " +errorMsg, status.getMessage());		
	}
	
	private Throwable createCause(String rootCauseMsg) {
		Throwable rootCause = new Throwable (rootCauseMsg);
		Throwable persistenceCause = new Throwable (rootCause);
		Throwable serverCause = new Throwable(persistenceCause);
		Throwable cause = new Throwable(serverCause);
		return cause;
	}
}

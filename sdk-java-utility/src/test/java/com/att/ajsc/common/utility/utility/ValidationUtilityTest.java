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

import org.junit.Test;

import com.att.ajsc.common.exception.BadRequestException;
import com.att.ajsc.common.exception.ServiceException;
import com.att.ajsc.common.utility.ValidationUtility;

public class ValidationUtilityTest {

	@Test
	public void testValidateNotNull() {
		Object value = null;
		String fieldName = null;
		try {
			ValidationUtility.validateNotNull(value, fieldName);
		} catch (ServiceException e) {
			assertEquals(
					"Bad Request The field  is null, it must have a value.",
					e.getMessage());
		}
	}

	@Test
	public void testValidateIdById() {
		String id = null;
		try {
			ValidationUtility.validateId(id);
		} catch (ServiceException e) {
			assertEquals(
					"Bad Request The  ID number is null, it must be of type String.",
					e.getMessage());
		}
	}

	@Test
	public void testValidateIdByIdType() {
		String id = null;
		String type = null;
		try {
			ValidationUtility.validateId(id, type);
		} catch (ServiceException e) {
			assertEquals(
					"Bad Request The  ID number is null, it must be of type String.",
					e.getMessage());
		}
	}

	@Test
	public void testValidateLongId() {
		Long id = null;
		try {
			ValidationUtility.validateId(id);
		} catch (ServiceException e) {
			assertEquals(
					"Bad Request The  ID number is null, it must be of type Long.",
					e.getMessage());
		}
	}

	@Test
	public void testValidateLongIdType() {
		Long id = null;
		String type = null;
		try {
			ValidationUtility.validateId(id, type);
		} catch (ServiceException e) {
			assertEquals(
					"Bad Request The  ID number is null, it must be of type Long.",
					e.getMessage());
		}
	}

	// Bhaskar : There is no validation in these methods. Instead just String
	// message
	// is returned, need to get back to these methods as needed.
	/*
	 * @Test public void testNullObjectMessage() { fail("Not yet implemented");
	 * }
	 * 
	 * @Test public void testNullFieldMessage() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testIntLessThanZeroMessage() {
	 * fail("Not yet implemented"); }
	 * 
	 * @Test public void testNullListMessage() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testEmptyListMessage() { fail("Not yet implemented"); }
	 */

	@Test
	public void testValidateDate() {
		String date = "some_date";
		String fieldName = null;
		StringBuilder errMsg = new StringBuilder();

		try {
			ValidationUtility.validateDate(date, fieldName);
		} catch (ServiceException e) {
			errMsg.append("Bad Request The  Date '").append(date);
			errMsg.append("' is not in ISO 8601 format.");
			
			assertEquals(errMsg.toString(), e.getMessage());
		}
	}

	@Test
	public void testValidateRangeValueMinMaxFieldnameString() {
		String value = "50";
		String minimum = "10";
		String maximum = "20";
		String fieldName = null;
		StringBuilder errMsg = new StringBuilder();
		
		try {
			
			ValidationUtility.validateRange(value, minimum, maximum, fieldName);
		} catch (BadRequestException e) {
			
			errMsg.append("Bad Request Value of ").append(value.toString());
			errMsg.append(" is greater than maximum, thus not in range of [");
			errMsg.append(minimum.toString()).append(", ")
					.append(maximum.toString()).append("]");

			assertEquals(errMsg.toString(), e.getMessage());
		}		
	}

	@Test
	public void testValidateRangeValueMinMaxFieldnameInteger() {
		Integer value = 50;
		Integer minimum = 10;
		Integer maximum = 20;
		String fieldName = null;
		StringBuilder errMsg = new StringBuilder();
		try {
			ValidationUtility.validateRange(value, minimum, maximum, fieldName);
		} catch (BadRequestException e) {
			errMsg.append("Bad Request Value of ").append(value.toString());
			errMsg.append(" is greater than maximum, thus not in range of [");
			errMsg.append(minimum.toString()).append(", ")
					.append(maximum.toString()).append("]");

			assertEquals(errMsg.toString(), e.getMessage());
		}
	}

	@Test
	public void testValidateRangeValueMinMaxFieldnameLong() {
		Long value = 50L;
		Long minimum = 10L;
		Long maximum = 20L;
		String fieldName = null;
		StringBuilder errMsg = new StringBuilder();
		try {
			ValidationUtility.validateRange(value, minimum, maximum, fieldName);
		} catch (BadRequestException e) {
			errMsg.append("Bad Request Value of ").append(value.toString());
			errMsg.append(" is greater than maximum, thus not in range of [");
			errMsg.append(minimum.toString()).append(", ")
					.append(maximum.toString()).append("]");

			assertEquals(errMsg.toString(), e.getMessage());
		}
	}

	@Test
	public void testValidateNotEmptyString() {
		String value = "";
		String fieldName = null;
		
		try {
			ValidationUtility.validateNotEmptyString(value, fieldName);
		} catch (BadRequestException e) {

			assertEquals(
					"Bad Request The  is an empty string, it must have a value.",
					e.getMessage());
		}		
	}

	@Test
	public void testValidateBooleanString() {
		String value = null;
		String fieldName = null;
		
		try {
			ValidationUtility.validateBooleanString(value, fieldName);
		} catch (BadRequestException e) {

			assertEquals(
					"Bad Request The field  is null, it must have a boolean value.",
					e.getMessage());
		}
	}

	@Test
	public void testValidateStringRegularExpression() {
		String value = "att";
		String fieldName = null;
		String regEx = null;

		try {
			ValidationUtility.validateStringRegularExpression(value, fieldName, regEx);
		} catch (BadRequestException e) {

			assertEquals(
					"Bad Request The  does not match the regular expression ''",
					e.getMessage());
		}
	}

	@Test
	public void testValidateStringCamelCase() {
		String value = "CamelCAse";
		String fieldName = null;

		try {
			ValidationUtility.validateStringCamelCase(value, fieldName);
		} catch (BadRequestException e) {

			assertEquals(
					"Bad Request The field  is not in camelCase format",
					e.getMessage());
		}		
	}

	@Test
	public void testValidateStringUnderscoreSeparated() {
		String value = "_ATT";
		String fieldName = null;

		try {
			ValidationUtility.validateStringUnderscoreSeparated(value, fieldName);
		} catch (BadRequestException e) {

			assertEquals(
					"Bad Request The field  is not in lower_case format",
					e.getMessage());
		}		
	}

	@Test
	public void testValidateEmailString() {
		String email = "_ATT";
		String fieldName = null;
		StringBuilder errMsg = new StringBuilder();

		try {
			ValidationUtility.validateEmailString(email, fieldName);
		} catch (BadRequestException e) {
			
			errMsg.append("Bad Request The  value ").append(email);
			errMsg.append(" is not in RFC822 email format");

			assertEquals(errMsg.toString(), e.getMessage());
		}
	}

}

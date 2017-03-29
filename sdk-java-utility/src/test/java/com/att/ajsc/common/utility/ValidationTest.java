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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.ajsc.common.exception.BadRequestException;

public class ValidationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void booleanStringTest() {
		try {
			ValidationUtility.validateBooleanString("", "test");
		} catch(Exception e) {
			assertEquals(
					"The field test is not a boolean string, "
					+ "it must have a boolean value.",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateBooleanString("", null);
		} catch(Exception e) {
			assertEquals(
					"The field is not a boolean string, "
					+ "it must have a boolean value.",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateBooleanString("true", null);
			ValidationUtility.validateBooleanString("false", null);
			ValidationUtility.validateBooleanString("True", null);
			ValidationUtility.validateBooleanString("False", null);
		} catch(Exception e) {
			fail("This should pass. " + e.getMessage());
		}
	}

	@Test
	public void dateTest() {
		String dateString = "20151104T09:20:22Z";
		try {
			ValidationUtility.validateDate(dateString, "test");
		} catch (BadRequestException e) {
			assertEquals(
					"The test Date '" + dateString
					+ "' is not in ISO 8601 format.",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateDate(dateString, null);
		} catch (BadRequestException e) {
			assertEquals(
					"The Date '" + dateString
					+ "' is not in ISO 8601 format.",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateDate("2015-11-04T09:20:22Z", "test");
		} catch (BadRequestException e) {
			fail("This should pass. " + e.getMessage());
		}
	}

	@Test
	public void emailTest() {
		String validEmailString = "user@domain.ext";
		String invalidEmailString = "user@ domain.ext";
		try {
			ValidationUtility.validateEmailString(invalidEmailString, "test");
		} catch (BadRequestException e) {
			assertEquals(
					"The test value " + invalidEmailString
					+ " is not in RFC822 email format",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateEmailString(invalidEmailString, null);
		} catch (BadRequestException e) {
			assertEquals(
					"The value " + invalidEmailString
					+ " is not in RFC822 email format",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateEmailString(validEmailString, "test");
		} catch (BadRequestException e) {
			fail("This should pass. " + e.getMessage());
		}
	}

	@Test
	public void idTest() {
		String validIdString = java.util.UUID.randomUUID().toString();
		String invalidIdString = null;
		try {
			ValidationUtility.validateId(invalidIdString, "test");
		} catch (Exception e) {
			assertEquals(
					"Bad Request The test ID number is null, it must be of type String.",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateId(invalidIdString, null);
		} catch (Exception e) {
			assertEquals(
					"Bad Request The  ID number is null, it must be of type String.",
					e.getMessage());
		}
		
		try {
			invalidIdString = " ";
			ValidationUtility.validateId(invalidIdString, "test");
		} catch (Exception e) {
			assertEquals(
					"Bad Request The test ID number is empty, it must be set.",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateId(validIdString, "test");
		} catch (Exception e) {
			fail("This should pass. " + e.getMessage());
		}
	}

	@Test
	public void rangeTest() {
		long value = 1;
		long minimum = 0;
		long maximum = 2;
		
		try {
			ValidationUtility.validateRange(value, minimum, maximum, "test");
		} catch (Exception e) {
			fail("This should pass. " + e.getMessage());
		}
		
		try {
			value = maximum + 1;
			ValidationUtility.validateRange(value, minimum, maximum, "test");
		} catch (Exception e) {
			assertEquals(
					"Bad Request Value of " + value +
					" is greater than maximum, thus not in range of [" +
					minimum + ", " + maximum + "]",
					e.getMessage());
		}
		
		try {
			value = minimum - 1;
			ValidationUtility.validateRange(value, minimum, maximum, "test");
		} catch (Exception e) {
			assertEquals(
					"Bad Request Value of " + value +
					" is less than the minimum, thus not in range of [" +
					minimum + ", " + maximum + "]",
					e.getMessage());
		}
	}

	@Test
	public void camelCaseTest() {
		String validString = "camelCaseTest";
		String invalidString = "camel_case_test";
		try {
			ValidationUtility.validateStringCamelCase(invalidString, "test");
		} catch (Exception e) {
			assertEquals(
					"Bad Request The field test is not in camelCase format",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateStringCamelCase(invalidString, null);
		} catch (Exception e) {
			assertEquals(
					"Bad Request The field  is not in camelCase format",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateStringCamelCase(validString, "test");
		} catch (Exception e) {
			fail("This should pass. " + e.getMessage());
		}
	}

	@Test
	public void underscoreTest() {
		String validString = "underscore_case_test";
		String invalidString = "camelCaseTest";
		try {
			ValidationUtility.validateStringUnderscoreSeparated(invalidString, "test");
		} catch (Exception e) {
			assertEquals(
					"Bad Request The field test is not in lower_case format",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateStringUnderscoreSeparated(invalidString, null);
		} catch (Exception e) {
			assertEquals(
					"Bad Request The field  is not in lower_case format",
					e.getMessage());
		}
		
		try {
			ValidationUtility.validateStringUnderscoreSeparated(validString, "test");
		} catch (Exception e) {
			fail("This should pass. " + e.getMessage());
		}
	}
}

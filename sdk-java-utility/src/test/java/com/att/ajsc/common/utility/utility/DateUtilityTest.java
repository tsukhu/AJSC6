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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.Test;

import com.att.ajsc.common.utility.DateUtility;

public class DateUtilityTest {

	@Test
	public void testGetNowAsXMLGregorianCalendar() throws DatatypeConfigurationException {
		Date newDt1 = new Date();
		XMLGregorianCalendar xGregCal =
				DateUtility.getNowAsXMLGregorianCalendar();
		Date newDt2 = new Date();
		assertNotNull(xGregCal);
		assertTrue(xGregCal instanceof XMLGregorianCalendar);
		assertTrue(xGregCal.toGregorianCalendar().getTime().getTime() >= newDt1.getTime());
		assertTrue(xGregCal.toGregorianCalendar().getTime().getTime() <= newDt2.getTime());
	}

	@Test
	public void testGetNowAsXMLGregorianCalendarQuietly() throws DatatypeConfigurationException {
		XMLGregorianCalendar xGregCal = DateUtility.getNowAsXMLGregorianCalendarQuietly();
		assertNotNull(xGregCal);
		assertTrue(xGregCal instanceof XMLGregorianCalendar);
	}

	@Test
	public void testToXMLGregorianCalendar() throws DatatypeConfigurationException {
		Date newDt = new Date();
		XMLGregorianCalendar gregCal = DateUtility.toXMLGregorianCalendar(newDt);
		assertNotNull(gregCal);
		assertTrue(gregCal instanceof XMLGregorianCalendar);
		assertEquals(newDt, gregCal.toGregorianCalendar().getTime());
	}

	@Test
	public void testToCalendar() throws DatatypeConfigurationException {
		GregorianCalendar gregCal = new GregorianCalendar();
		XMLGregorianCalendar xGregCal =
				DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
		Calendar calendarNow = DateUtility.toCalendar(xGregCal);
		assertNotNull(calendarNow);
		assertTrue(calendarNow instanceof Calendar);
		assertEquals(gregCal.getTime(), calendarNow.getTime());
	}

	@Test
	public void testToDateXMLGregorianCalendar() throws DatatypeConfigurationException {
		GregorianCalendar gregCal = new GregorianCalendar();
		XMLGregorianCalendar xGregCal =
				DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
		Date dateNow = DateUtility.toDate(xGregCal);
		assertNotNull(dateNow);
		assertTrue(dateNow instanceof Date);
	}

	@Test
	public void testNowAsCalendar() {
		Calendar newCal = Calendar.getInstance();
		Calendar calendarNow = DateUtility.nowAsCalendar();
		assertNotNull(calendarNow);
		assertTrue(calendarNow instanceof Calendar);
		assertEquals(newCal, calendarNow);
	}

	@Test
	public void testNowAsDate() {
		Date newDt = new Date();
		Date dateNow = DateUtility.nowAsDate();
		assertNotNull(dateNow);
		assertTrue(dateNow instanceof Date);
		assertEquals(newDt, dateNow);
	}

	@Test
	public void testNowInMilliseconds() {
		Long timeInMillis = DateUtility.nowInMilliseconds();
		assertNotNull(timeInMillis);
		assertTrue(new Date(timeInMillis) instanceof Date);
		assertEquals(new DateTime(timeInMillis).getMillis(), timeInMillis.longValue());
	}

	@Test
	public void testToDateLong() {
		Date testDate = new Date();
		Long timeInMillis = testDate.getTime();

		Date dateVal = DateUtility.toDate(timeInMillis);
		assertNotNull(dateVal);
		assertTrue(dateVal instanceof Date);
		assertEquals(testDate, dateVal);
	}

	@Test
	public void testToLong() {
		Date dateVal = new Date();
		Long dateAsLong = DateUtility.toLong(dateVal);
		assertNotNull(dateAsLong);
		assertTrue(new DateTime(dateAsLong) instanceof DateTime);
		assertEquals(dateVal, new DateTime(dateAsLong).toDate());
	}

	@Test
	public void testToDateString() {
		DateTime testDate = new DateTime();
		String dateAsIsoString = testDate.toString();
		Date dateVal = DateUtility.toDate(dateAsIsoString);
		assertNotNull(dateVal);
		assertTrue(dateVal instanceof Date);
		assertEquals(testDate.toDate(), dateVal);
	}

	@Test
	public void testToIsoString() {
		Date dateVal = new Date();
		String dateAsIsoString = DateUtility.toIsoString(dateVal);
		assertNotNull(dateAsIsoString);
		assertTrue(new DateTime(dateAsIsoString) instanceof DateTime);
		assertEquals(dateVal, new DateTime(dateAsIsoString).toDate());
	}

	@Test
	public void testNowAsIsoString() {
		String dateNowAsIsoString = DateUtility.nowAsIsoString();
		assertNotNull(dateNowAsIsoString);
		assertTrue(new DateTime(dateNowAsIsoString) instanceof DateTime);
		assertEquals(new DateTime(dateNowAsIsoString).toString(), dateNowAsIsoString);
	}
}

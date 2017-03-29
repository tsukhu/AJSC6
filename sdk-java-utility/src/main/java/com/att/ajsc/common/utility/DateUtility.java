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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

public class DateUtility {

	public static final Long SECOND = 1000L;
	public static final Long MINUTE = 60 * SECOND;
	public static final Long HOUR = 60 * MINUTE;
	public static final String isoDateFormat = "yyyy-MM-dd’T'HH:mm:ss.SSSZ";
	
    public static XMLGregorianCalendar getNowAsXMLGregorianCalendar()
    		throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(
        		new GregorianCalendar());
    }
    public static XMLGregorianCalendar getNowAsXMLGregorianCalendarQuietly() {
    	try {
    		return getNowAsXMLGregorianCalendar();
    	} catch(Throwable t) {
    		return null;
    	}
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date)
    		throws DatatypeConfigurationException {
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTime(date);
    	return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    public static Calendar toCalendar(XMLGregorianCalendar xmlDate) {
    	return xmlDate == null ? null : xmlDate.toGregorianCalendar();
    }

    public static Date toDate(XMLGregorianCalendar xmlDate) {
    	return xmlDate == null ? null : xmlDate.toGregorianCalendar().getTime();
    }
    
	public static Calendar nowAsCalendar() {
		return Calendar.getInstance();
	}

	public static Date nowAsDate() {
		return nowAsCalendar().getTime();
	}

	public static long nowInMilliseconds() {
		return nowAsDate().getTime();
	}

	public static Date toDate(Long dateInMilliseconds) {
		return (dateInMilliseconds == null) ?
				null : new Date(dateInMilliseconds);
	}

	public static Long toLong(Date date) {
		return (date == null) ?
				null : new Long(date.getTime());
	}

	public static Date toDate(
			String dateAsIsoString
			) {
		DateTime dateTime = new DateTime(dateAsIsoString);
		return dateTime.toDate();
	}

	public static String toIsoString(
			Date date
			) {
		DateTime dateTime = new DateTime(date);
		return dateTime.toString();
	}

	public static String nowAsIsoString() {
		DateTime dateTime = new DateTime();
		return dateTime.toString();
	}
}

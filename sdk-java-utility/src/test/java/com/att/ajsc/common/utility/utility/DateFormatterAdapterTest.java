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

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import com.att.ajsc.common.utility.DateFormatterAdapter;

public class DateFormatterAdapterTest {

	private DateFormatterAdapter dateFormatterAdapter = new DateFormatterAdapter();

	@Test
	public void testUnmarshal() {

		DateTime testDate = new DateTime();
		String dateAsIsoString = testDate.toString();

		Date dateVal;
		try {
			
			dateVal = dateFormatterAdapter.unmarshal(dateAsIsoString);
			assertNotNull(dateVal);
			assertTrue(dateVal instanceof Date);
			assertEquals(testDate.toDate(), dateVal);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Test
	public void testMarshal() {

		Date dateVal = new Date();
		try {
			
			String dateAsIsoString = dateFormatterAdapter.marshal(dateVal);
			assertNotNull(dateAsIsoString);
			assertTrue(new DateTime(dateAsIsoString) instanceof DateTime);
			assertEquals(dateVal, new DateTime(dateAsIsoString).toDate());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}

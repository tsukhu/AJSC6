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
package com.att.ajsc.common.interceptors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.att.ajsc.common.Interceptor;
import com.att.ajsc.common.PreInterceptor1;
import com.att.ajsc.common.PreInterceptor2;
import com.att.ajsc.common.Request;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PreInterceptorTest.configuration.class })
public class PreInterceptorTest {

	@Autowired
	ApplicationContext context;

	@Configuration
	static class configuration {

		@Bean(name = "interceptor")
		public PreInterceptor getPreInterceptor() {
			PreInterceptor interceptor = spy(new PreInterceptor());
			return interceptor;
		}

		@Bean(name = "interceptor1")
		public PreInterceptor1 getPreInterceptor1() {
			PreInterceptor1 interceptor1 = spy(new PreInterceptor1());
			return interceptor1;
		}

		@Bean(name = "interceptor2")
		public PreInterceptor2 getPreInterceptor2() {
			PreInterceptor2 interceptor2 = spy(new PreInterceptor2());
			return interceptor2;
		}
	}

	@Test
	public void matchingInterceptorTest() throws Exception {

		Request mockRequest = mock(Request.class);
		UriInfo uriInfo = mock(UriInfo.class);
		MockEnvironment mockEnvironment = new MockEnvironment();
		mockEnvironment.setProperty("com.att.ajsc.common.interceptors.PreInterceptor.url", "/path");

		when(uriInfo.getAbsolutePath()).thenReturn(new URI("/path"));
		when(mockRequest.getUriInfo()).thenReturn(uriInfo);

		Interceptor interceptor = new Interceptor();
		interceptor.filter(mockRequest, mockEnvironment, context);
		((PreInterceptor) verify(context.getBean("interceptor"), times(1))).allowOrReject(mockRequest);

	}

	@Test
	public void notMatchingInterceptorTest() throws Exception {

		Request mockRequest = mock(Request.class);
		UriInfo uriInfo = mock(UriInfo.class);
		MockEnvironment mockEnvironment = new MockEnvironment();
		mockEnvironment.setProperty("com.att.ajsc.common.interceptors.PreInterceptor.url", "/incorrectpath");

		when(uriInfo.getAbsolutePath()).thenReturn(new URI("/path"));
		when(mockRequest.getUriInfo()).thenReturn(uriInfo);

		Interceptor interceptor = new Interceptor();
		interceptor.filter(mockRequest, mockEnvironment, context);

		((PreInterceptor) verify(context.getBean("interceptor"), times(0))).allowOrReject(mockRequest);

	}

	@Test
	public void interceptorsInvokingOrderTest() throws Exception {

		Request mockRequest = spy(new Request());
		UriInfo uriInfo = mock(UriInfo.class);
		MockEnvironment mockEnvironment = new MockEnvironment();
		when(uriInfo.getAbsolutePath()).thenReturn(new URI("/path"));
		when(mockRequest.getUriInfo()).thenReturn(uriInfo);

		Interceptor interceptor = new Interceptor();
		interceptor.filter(mockRequest, mockEnvironment, context);

		((PreInterceptor1) verify(context.getBean("interceptor1"), times(1))).allowOrReject(mockRequest);
		((PreInterceptor2) verify(context.getBean("interceptor2"), times(1))).allowOrReject(mockRequest);
		assertEquals(mockRequest.getProperty("filterInvokingOrder").toString(), "12");

	}

}

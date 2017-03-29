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
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = EnvironmentUtilityTest.TestConfig.class)
public class EnvironmentUtilityTest {

	private static final String BUILDNAME = "WHATSTHIS";
	private static final String VERSION = "MYVERSION";
	private static final String ARTIFACT = "ARTIFACT";

	static class TestConfig{

		@Bean
		public EnvironmentUtility eu(){
			return new EnvironmentUtility();
		}
		
		@Bean
		public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
			PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
			ppc.setIgnoreResourceNotFound(true);
			Properties p = new Properties();
			p.setProperty("info.build.name",BUILDNAME);
			p.setProperty("info.build.version",VERSION);
			p.setProperty("info.build.artifact",ARTIFACT);
			ppc.setProperties(p);
			return ppc;
		}
		
	}
	
	

	@Autowired
	EnvironmentUtility environmentUtility;
	
	@Test
	public void applicationNameTest() {
		assertEquals(BUILDNAME,environmentUtility.getApplicationName());
	}
	
	@Test
	public void versionTest() {
		assertEquals(VERSION,environmentUtility.getVersion());
	}
	
	@Test
	public void applicationServerTest() {
		String value = environmentUtility.getApplicationServer();
		assertNotNull(value);
	}
	
	@Test
	public void hostNameTest() {
		String value = environmentUtility.getHostName();
		assertNotNull(value);
	}
	
	@Test
	public void ipTest() {
		String value = environmentUtility.getIpAddress();
		assertNotNull(value);
	}
	
	@Test
	public void jvmTest() {
		String value = environmentUtility.getJvm();
//		assertNotNull(value);
	}
}

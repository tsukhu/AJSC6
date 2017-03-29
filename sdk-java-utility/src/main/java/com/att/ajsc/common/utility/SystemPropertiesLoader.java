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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class SystemPropertiesLoader {

	public static void addSystemProperties() {

		InputStream input = null;
		Properties properties = new Properties();

		try {

			if (System.getenv("system_properties_path") != null) {
				input = new FileInputStream(System.getenv("system_properties_path"));
			} else {
				input = new SystemPropertiesLoader().getClass().getResourceAsStream("/system.properties");

			}
			properties.load(input);

			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				String value = properties.getProperty(key);
				System.setProperty(key, value);
			}
		}

		catch (Exception e) {

			if (System.getenv("system_properties_path") != null) {
				System.out.println("error loading the properties from the system properties path "
						+ System.getenv("system_properties_path"));
			} else {
				System.out.println("error loading the properties from system.properties ");
			}

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					System.out.println("error loading the properties from system.properties ");
				}
			}
		}

		if (System.getenv("com_att_eelf_logging_path") != null) {
			System.setProperty("com.att.eelf.logging.path", System.getenv("com_att_eelf_logging_path"));
			System.setProperty("logging.config", System.getenv("com_att_eelf_logging_path") + "/logback.xml");
		}

	}

}

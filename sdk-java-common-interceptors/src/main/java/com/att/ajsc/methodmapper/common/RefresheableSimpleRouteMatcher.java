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
package com.att.ajsc.methodmapper.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

public class RefresheableSimpleRouteMatcher {
	private static AtomicReference<RouteMatcher> sm = new AtomicReference<RouteMatcher>();
	private static Logger logger = LoggerFactory.getLogger(RefresheableSimpleRouteMatcher.class);

	public static void refresh(File file, String contextpath) throws Exception {

		logger.info("Refresh of service spec from file " + file.getAbsolutePath());

		ObjectMapper om = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		TypeReference<HashMap<String, ArrayList<HashMap<String, String>>>> typeRef = new TypeReference<HashMap<String, ArrayList<HashMap<String, String>>>>() {
		};

		logger.info("Reading file...");
		HashMap<String, ArrayList<HashMap<String, String>>> serviceSpec = om.readValue(file, typeRef);

		logger.info("Creating new matcher...");
		SimpleRouteMatcher newMatcher = new SimpleRouteMatcher();

		for (Entry<String, ArrayList<HashMap<String, String>>> e : serviceSpec.entrySet()) {
			String service = e.getKey();
			logger.debug("Processing service - " + service);
			// System.out.println("***** Processing service - " + service);
			for (HashMap<String, String> h : e.getValue()) {

				String method = h.get("method");
				String url = contextpath + h.get("url");
				logger.info("-------------------------url----------------:"+url);
				String logicalName = service + "-" + h.get("logicalName");
				String type = h.get("type");
				String serviceName = h.get("serviceName");
				newMatcher.parseValidateAddRoute(service, method, url, logicalName, type, serviceName);
			}
		}
		sm.set(newMatcher);
	}

	public static RouteMatcher getRouteMatcher() {
		return sm.get();
	}
}

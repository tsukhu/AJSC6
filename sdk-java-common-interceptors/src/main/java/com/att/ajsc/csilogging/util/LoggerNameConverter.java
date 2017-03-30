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
package com.att.ajsc.csilogging.util;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class LoggerNameConverter {

	private static final String SLASH_TOKEN = "/";
	private static final Pattern SLASH_PATTERN = java.util.regex.Pattern.compile(SLASH_TOKEN);
	private static final String PERIOD_TOKEN = ".";


	public static String convertNormalizedName(HttpServletRequest request, String servicename) {

		String normalizedName = null;
		String result = null;

		if (request.getHeader("X-CSI-REST_NAME_NORMALIZED") != null
				&& !(request.getHeader("X-CSI-REST_NAME_NORMALIZED")
						.equalsIgnoreCase(CommonNames.REST_NAME_NORMALIZED_NO_PATTERN_MATCH))
				&& request.getHeader("X-CSI-REST_NAME_NORMALIZED").equalsIgnoreCase(servicename)) {
			normalizedName = request.getHeader("X-CSI-REST_NAME_NORMALIZED");
			if (normalizedName != null) {
				result = SLASH_PATTERN.matcher(normalizedName).replaceAll(PERIOD_TOKEN);
				if (result != null) {
					return result;
				}
			}

		}

		return servicename;

	}

}

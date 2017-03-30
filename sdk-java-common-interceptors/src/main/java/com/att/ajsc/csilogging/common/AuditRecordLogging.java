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
package com.att.ajsc.csilogging.common;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.ajsc.csi.logging.AuditRecord;
import com.att.ajsc.csi.logging.PerformanceTrackingBean;
import com.att.ajsc.csilogging.util.CommonNames;
import com.att.ajsc.csilogging.util.JAXBmar;

public class AuditRecordLogging {

	static final Logger logger = LoggerFactory.getLogger(AuditRecordLogging.class);
	private static JAXBmar mar;

	static {
		mar = JAXBmar.instance(AuditRecord.class);
	}

	public static void auditLogResult(AuditRecord ar, HttpServletRequest request) {
		try {
			logger.debug("In...:auditLogResult");
			if (ar != null) {
				StringWriter sw = new StringWriter();

				try {
					logger.debug("Inside try, going for Marshalling ");

					mar.marshal(ar, sw);

				} catch (Exception e) {
					logger.error("Marshalling Ex " + e.toString());
				}

				logger.debug("Marshalling completed ");

				logger.info(" Audit Log: \n" + sw.toString());

				if (System.getProperty("csiEnable") != null && System.getProperty("csiEnable").equals("true")) {


					if ((request.getAttribute(CommonNames.CALL_TYPE)) != null
							&& (request.getAttribute(CommonNames.CALL_TYPE)).equals("DIRECT")) {

						logger.info("DIRECT CALL:");

						logger.info(sw.toString());

					} else {

						logger.info("GATEWAY CALL:");

						logger.info(sw.toString());
					}

					request.setAttribute(CommonNames.AUDIT_LOG, sw.toString());
				}
			} else {
				logger.error("No audit record to log");
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public static void auditLogResult(AuditRecord ar, PerformanceTrackingBean perfBean, HttpServletRequest request) {
		try {
			logger.debug("In...:auditLogResult Perf Bean");

			if (ar != null) {
				StringWriter sw = new StringWriter();

				try {
					logger.debug("Inside try, going for Marshalling ");

					mar.marshal(ar, sw);

				} catch (Exception e) {
					logger.error("Marshalling Ex " + e.toString());
				}

				logger.debug("Marshalling completed ");

				logger.info("************************** Audit Log **************************\n" + sw.toString());
				if (System.getProperty("csiEnable") != null && System.getProperty("csiEnable").equals("true")) {

					if (perfBean.getCallType().trim().equals("DIRECT")) {

						logger.info("***************************DIRECT CALL***************************");

						logger.info(sw.toString());

					} else {

						logger.info("***************************GATEWAY CALL***************************");
						logger.info(sw.toString());
					}
					request.setAttribute(CommonNames.AUDIT_LOG, sw.toString());
				}
			} else {
				logger.error("No audit record to log");
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
}

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

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@RefreshScope
@Component
public class QueueConnector {

	static final EELFLogger logger = AjscEelfManager.getInstance().getLogger(QueueConnector.class);

	private static final String YES = "yes";

	@Value("${JMS_WMQ_AUDIT_DESTINATION_NAME:}")
	private String auditDestinationName;
	@Value("${JMS_WMQ_PERF_DESTINATION_NAME:}")
	private String perfDestinationName;
	@Value("${JMS_WMQ_CONNECTION_FACTORY_NAME:}")
	private String connectionFactoryName;
	@Value("${JMS_WMQ_PROVIDER_URL:}")
	private String providerURL;
	@Value("${JMS_WMQ_INITIAL_CONNECTION_FACTORY_NAME:}")
	private String initialContextFactoryName;
	@Value("${csiEnable:false}")
	private Boolean csiEnable;
	@Value("${com.att.aft.config.file:}")
	private String aftPath;
	@Value("${restart.perform.queue:false}")
	private String restartPerformanceQueue;
	@Value("${restart.audit.queue:false}")
	private String restartAuditQueue;

	private QueueConnection auditQueueConnection;
	private QueueSession auditQueueSession;
	private QueueSender auditQueueSender;

	private QueueConnection pefQueueConnection;
	private QueueSession pefQueueSession;
	private QueueSender pefQueueSender;

	private int PERF_SUCCESSIVE_FAILURE_LIMIT = 3;
	private int AUDIT_SUCCESSIVE_FAILURE_LIMIT = 3;

	private int perfSuccessiveFailureCount = 0;
	private int auditSuccessiveFailureCount = 0;

	@PostConstruct
	public void init() {

		if (csiEnable && StringUtils.isNotEmpty(initialContextFactoryName)
				&& StringUtils.isNotEmpty(connectionFactoryName) && StringUtils.isNotEmpty(providerURL)) {

			if (StringUtils.isNotEmpty(System.getenv(("com_att_aft_config_file")))) {
				System.setProperty("com.att.aft.config.file", System.getenv("com_att_aft_config_file"));
			} 

			if (StringUtils.isEmpty(System.getProperty("com.att.aft.config.file"))) {
				logger.error("Environment or System properties dont have the property com.att.aft.config.file");
				return;
			}

			QueueConnectionFactory queueConnectionFactory;
			InitialContext jndi = null;
			ConnectionFactory connectionFactory = null;
			try {

				Properties env = new Properties();
				env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactoryName);
				env.put(Context.PROVIDER_URL, providerURL);
				jndi = new InitialContext(env);
				connectionFactory = (ConnectionFactory) jndi.lookup(connectionFactoryName);
				queueConnectionFactory = (QueueConnectionFactory) connectionFactory;
				if (StringUtils.isNotEmpty(auditDestinationName)) {
					auditQueueConnection = queueConnectionFactory.createQueueConnection();
					auditQueueSession = auditQueueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
					Queue auditQueue = (Queue) auditQueueSession.createQueue(auditDestinationName);
					auditQueueSender = auditQueueSession.createSender(auditQueue);
					auditQueueConnection.start();
					logger.info("*************CONNECTED :" + auditDestinationName + "*************");

				}

				if (StringUtils.isNotEmpty(perfDestinationName)) {
					pefQueueConnection = queueConnectionFactory.createQueueConnection();
					pefQueueSession = pefQueueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
					Queue perfQueue = (Queue) pefQueueSession.createQueue(perfDestinationName);
					pefQueueSender = pefQueueSession.createSender(perfQueue);
					pefQueueConnection.start();
					logger.info("*************CONNECTED :" + perfDestinationName + "*************");
				}

			} catch (Exception e) {
				logger.error("Error while connecting to the Queue" + e);
			}
		}

	}

	@Async
	public void sendAuditLog(String auditLog) {

		if (auditQueueSender != null && auditQueueSession != null) {
			TextMessage msg;
			try {
				msg = auditQueueSession.createTextMessage();
				msg.setText(auditLog);
				logger.info("Sending audit log to the CSI");
				auditQueueSender.send(msg);
				auditSuccessiveFailureCount = 0;
			} catch (Exception e) {

				logger.error("Error while sending audit log to the Queue" + e);

				auditSuccessiveFailureCount++;
				if (auditSuccessiveFailureCount > AUDIT_SUCCESSIVE_FAILURE_LIMIT) {
					try {
						if (auditQueueSession != null) {
							auditQueueSession.close();
						}
						if (auditQueueConnection != null) {
							auditQueueConnection.close();
						}
					} catch (Exception exception) {
						logger.error("Error while closing performance log queue connections", exception);
					}

					auditQueueSession = null;
					auditQueueSender = null;
					auditQueueConnection = null;

				}
			}

		}

		else if (auditQueueSession == null && auditQueueSender == null
				&& YES.equals(restartAuditQueue)) {

			init();
			sendPerformanceLog(auditLog);
		}

	}

	@Async
	public void sendPerformanceLog(String perfLog) {

		if (pefQueueSession != null && pefQueueSender != null) {
			TextMessage msg;
			try {
				msg = pefQueueSession.createTextMessage();
				msg.setText(perfLog);
				logger.info("sending performance log to the CSI");
				pefQueueSender.send(msg);
				perfSuccessiveFailureCount = 0;
			} catch (Exception e) {

				logger.error("Error while sending performance log to the Queue" + e);

				perfSuccessiveFailureCount++;
				if (perfSuccessiveFailureCount > PERF_SUCCESSIVE_FAILURE_LIMIT) {
					try {
						if (pefQueueSession != null) {
							pefQueueSession.close();
						}
						if (pefQueueConnection != null) {
							pefQueueConnection.close();
						}
					} catch (Exception exception) {
						logger.error("Error while closing performance log queue connections", exception);
					}

					pefQueueSender = null;
					pefQueueSender = null;
					pefQueueConnection = null;
				}
			}

		}

		else if (pefQueueSession == null && pefQueueSender == null
				&& YES.equals(System.getenv(restartPerformanceQueue))) {
			logger.info("restarting the queues");
			init();
			sendPerformanceLog(perfLog);
		}

	}

}

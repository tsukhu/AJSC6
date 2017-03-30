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

import static com.att.ajsc.csilogging.util.UtilLib.isNullOrEmpty;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemParams {

	private String pid_ = "N/A";
	private String cluster_ = "N/A";
	private String routeOffer_ = "N/A";
	private String instanceName_ = "N/A";
	private String appName_ = "N/A";
	private String appVersion_ = "N/A";
	private String ipAddress_ = "N/A";
	private String hostName_ = "N/A";
	private String vtier_ = "N/A";
	private String env_ = "N/A";
	private String namespace;
	private String serviceVersion;
	private String serviceName;

	private static final String PID = "Pid";
	private static final String LRM_HOST = "lrmHost";
	private static final String ROUTE_OFFER = "routeOffer";
	private static final String APP_NAME = "appName";
	private static final String APP_VERSION = "appVersion";
	private static final String LRM_RO = "lrmRO";
	private static final String LRM_ENV = "lrmEnv";
	private static final String HYDRA_APPL_CTXT = "VERSION_HYDRAAPPLDATA_ENVCONTEXT";
	private static final String SOACLOUD_ROUTE_OFFER = "SOACLOUD_ROUTE_OFFER";
	private static SystemParams sparams_ = null;

	public SystemParams() {
		try {
			int i = -1;

			pid_ = System.getProperty(PID, "N/A");

			if (System.getProperty(APP_NAME) != null) {

				appName_ = System.getProperty(APP_NAME);
			}
			env_ = System.getProperty(LRM_ENV, "N/A");

			hostName_ = System.getProperty(LRM_HOST);
			if (isNullOrEmpty(hostName_)) {
				try {
					hostName_ = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException uhe) {
					hostName_ = "N/A";
				}
			}
			i = hostName_.indexOf('.');
			if (i > 0)
				vtier_ = hostName_.substring(0, i);
			else
				vtier_ = hostName_;

			try {
				ipAddress_ = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException uhe) {
				ipAddress_ = "N/A";
			}
			if (System.getProperty(APP_VERSION) == null) {

				appVersion_ = System.getProperty(APP_VERSION, "N/A");
			}

			i = appVersion_.indexOf('.');
			if (i > 0)
				appVersion_ = appVersion_.substring(0, i);

			cluster_ = System.getProperty(LRM_RO, "N/A");

			if (System.getProperty(ROUTE_OFFER) == null) {

				routeOffer_ = System.getProperty(SOACLOUD_ROUTE_OFFER, "N/A");

			} else {
				routeOffer_ = System.getProperty(ROUTE_OFFER, "N/A");

			}
			String versionApplEnv = System.getProperty(HYDRA_APPL_CTXT);
			if (!isNullOrEmpty(versionApplEnv)) {
				String splits[] = versionApplEnv.split("\\/");

				if (splits != null && splits.length == 3) {
					if ("N/A".equals(appVersion_)) {
						appVersion_ = splits[0];
						i = appVersion_.indexOf('.');
						if (i > 0)
							appVersion_ = appVersion_.substring(0, i);
					}
					if ("N/A".equals(routeOffer_))
						routeOffer_ = splits[1];
					if ("N/A".equals(cluster_))
						cluster_ = splits[1];
				}
			}

			serviceName = appName_ + "-" + appVersion_;

			StringBuilder sb = new StringBuilder();
			sb.append("ajsc:");
			sb.append(System.getProperty(APP_NAME, "N/A"));
			sb.append("-");
			sb.append(System.getProperty(APP_VERSION, "N/A"));
			sb.append("-");
			sb.append(routeOffer_);
			sb.append("-");
			sb.append(hostName_);
			sb.append("-");
			sb.append(pid_);
			instanceName_ = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public static SystemParams instance() {
		if (sparams_ == null) {
			synchronized (SystemParams.class) {
				if (sparams_ == null)
					sparams_ = new SystemParams();
			}
		}
		return sparams_;
	}

	public String getPid() {
		return pid_;
	}

	public String getCluster() {
		return cluster_;
	}

	public String getRouteOffer() {
		return routeOffer_;
	}

	public String getInstanceName() {
		return instanceName_;
	}

	public String getAppName() {
		return appName_;
	}

	public String getAppVersion() {
		return appVersion_;
	}

	public String getIpAddress() {
		return ipAddress_;
	}

	public String getHostName() {
		return hostName_;
	}

	public String getVtier() {
		return vtier_;
	}

	public String getEnvContext() {
		return env_;
	}

	// USE ONLY FOR TESTING
	public static void deleteInstance() {
		sparams_ = null;
	}

}

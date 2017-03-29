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
package com.att.ajsc.introscope;

import java.util.EventObject;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntroscopeEventNotifierImpl implements IntroscopeEventNotifier {
	public static HttpServletRequest request = null;

	public enum CallType {
		ENTRY, EXIT
	};

	private final static Logger logger = LoggerFactory.getLogger(IntroscopeEventNotifierImpl.class);

	public static String SERVICENAME = "serviceName";
	public static String CONVERSATIONID = "conversationId";
	public static String UNIQUEID = "uniqueID";
	public static String USERID = "userID";

	// Holds the default values thru the setter
	public static String serviceName = "N/A";
	public static String conversationId = "N/A";
	public static String uniqueID = "N/A";
	public static String userID = "N/A";

	private static IntroscopeEventNotifierImpl classInstance = null;

	public static IntroscopeEventNotifierImpl getInstance() {
		if (classInstance == null) {
			classInstance = new IntroscopeEventNotifierImpl();
		}
		return classInstance;
	}

	public boolean isEnabled(EventObject event) {
		return true;
	}

	/**
	 * called by the compute service when creates an instance of this class to set default values
	 * @param serviceName
	 * @param conversationId
	 * @param uniqueID
	 * @param userID
	 */
	public void setDefaults(String serviceName, String conversationId, String uniqueID, String userID) {

		IntroscopeEventNotifierImpl.serviceName = serviceName;
		IntroscopeEventNotifierImpl.conversationId = conversationId;
		IntroscopeEventNotifierImpl.uniqueID = uniqueID;
		IntroscopeEventNotifierImpl.userID = userID;

	}

	/**
	 * Introscope instrumentation methods
	 * 
	 * @param serviceName
	 * @param conversationId
	 * @param uniqueID
	 * @param userID
	 */
	public static void instrumentIntroscopeSynchEntryPoint(String serviceName, String conversationId, String uniqueID,
			String userID) {
		System.out.println("********************* instrumentIntroscopeSynchEntryPoint ********************");
		logger.info("\nIntroscopeSynchEntryPoint: " + " ConversationID:" + conversationId + ", Service Name:"
				+ serviceName + ", uniqueID:" + uniqueID + ", userID:" + userID);
	}

	/**
	 * when a response is created , on the way out this method will be called
	 * 
	 * @param serviceName
	 * @param conversationId
	 * @param uniqueID
	 * @param userID
	 */
	public static void instrumentIntroscopeSynchExitPoint(String serviceName, String conversationId, String uniqueID,
			String userID) {
		System.out.println("********************* instrumentIntroscopeSynchExitPoint ********************");
		logger.info("\nIntroscopeSynchExitPoint: " + " ConversationID:" + conversationId + ", Service Name:"
				+ serviceName + ", uniqueID:" + uniqueID + ", userID:" + userID);
	}

}

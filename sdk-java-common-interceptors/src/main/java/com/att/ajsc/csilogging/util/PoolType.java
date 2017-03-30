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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * A PoolType provides the implementation for binding common characteristics
 * with their associated ObjectPools.
 */
public class PoolType implements DynamicMBean {

	/*
	 * Constants for the PoolType MBean attributes/operations.
	 */
	public static final String MINIMUM_POOL_SIZE = "minimumPoolSize";
	public static final String SET_MINIMUM_POOL_SIZE = "setMinimumPoolSize";

	/*
	 * The default minimum size for ObjectPool's of this type.
	 */
	public static final int DEFAULT_MINIMUM_SIZE = 1;

	/*
	 * A collection of all created PoolType's.
	 */
	private static HashMap<String, PoolType> s_poolTypes = new HashMap<String, PoolType>();

	/*
	 * The name of the PoolType.
	 */
	private String _name;

	/*
	 * The minimum pool size of this PoolType.
	 */
	private AtomicInteger _minPoolSize = new AtomicInteger(DEFAULT_MINIMUM_SIZE);

	/*
	 * The management interface exposed by the PoolManager.
	 */
	private MBeanInfo _mbeanInfo;

	/**
	 * Returns a PoolType associated with the supplied name - sharing if already
	 * existent, or creating if necessary.
	 * 
	 * @param name
	 *            The name of the PoolType to associate with.
	 * @return The PoolType associated with the supplied name.
	 */
	public static PoolType instance(String name) {
		synchronized (s_poolTypes) {
			if (s_poolTypes.containsKey(name)) {
				return s_poolTypes.get(name);
			} else {
				PoolType poolType = new PoolType(name);
				s_poolTypes.put(name, poolType);
				return poolType;
			}
		}
	}

	/**
	 * Returns a PoolType associated with the supplied name - sharing if already
	 * existent, or creating if necessary.
	 * 
	 * @param name
	 *            The name of the PoolType to associate with.
	 * @param minPoolSize
	 *            The minimum size of the pool associated with this PoolType.
	 * @return The PoolType associated with the supplied name.
	 * 
	 * @throws Exception
	 *             Conflicting minpoolsize
	 */
	public static PoolType instance(String name, int minPoolSize) throws Exception {
		synchronized (s_poolTypes) {
			if (s_poolTypes.containsKey(name)) {
				PoolType poolType = s_poolTypes.get(name);
				if (poolType.getMinPoolSize() == minPoolSize) {
					return poolType;
				} else {
					throw new Exception("Conflicting minPoolSize values for PoolType - " + name);
				}
			} else {
				PoolType poolType = new PoolType(name, minPoolSize);
				s_poolTypes.put(name, poolType);
				return poolType;
			}
		}
	}

	/**
	 * Constructs a new PoolType with the supplied name, assigns the PoolType
	 * the appropriate minimum pool size, and registers the PoolType's MBean.
	 * 
	 * @param name
	 *            The name of the PoolType to create.
	 */
	private PoolType(String name) {
		_name = name;

		// determine the minimum pool size
		String minPoolSize = System.getProperty(name + ".min.pool.size");
		if (minPoolSize != null) {
			try {
				int minSize = Integer.parseInt(minPoolSize);
				if (minSize >= 0) {
					_minPoolSize.set(minSize);
				}
			} catch (NumberFormatException e) {
				// use default
			}
		}

		// register the MBean
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName poolTypeName = new ObjectName("com.att.ajsc:name=" + name + ",type=PoolTypes");
			mbs.registerMBean(this, poolTypeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a new PoolType with the supplied name, assigns the PoolType
	 * the appropriate minimum pool size, and registers the PoolType's MBean.
	 * 
	 * @param name
	 *            The name of the PoolType to create.
	 */
	private PoolType(String name, int minPoolSize) {
		_name = name;

		if (minPoolSize >= 0) {
			_minPoolSize.set(minPoolSize);
		}

		// register the MBean
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName poolTypeName = new ObjectName("com.att.ajsc:name=" + name + ",type=PoolTypes");
			mbs.registerMBean(this, poolTypeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the minimum pool size for this PoolType.
	 * 
	 * @return The PoolType's minimum pool size.
	 */
	public int getMinPoolSize() {
		return _minPoolSize.get();
	}

	/**
	 * Returns the name of this PoolType.
	 * 
	 * @return The PoolType's name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Provides lazy instantiation of the PoolType's MBeanInfo, yet allows for
	 * reuse.
	 * 
	 * @return The PoolType's MBeanInfo object.
	 */
	public MBeanInfo getMBeanInfo() {
		if (_mbeanInfo == null) {
			_mbeanInfo = createMBeanInfo();
		}
		return _mbeanInfo;
	}

	/**
	 * Privately used function to create and return the MBeanInfo Object
	 * associated with this PoolType.
	 * 
	 * @return The PoolType's MBeanInfo object.
	 */
	private MBeanInfo createMBeanInfo() {
		return new MBeanInfo(getClass().getName(), "Responsible for associating characteristics of common pool types.",
				createMBeanAttributeInfo(), null, createMBeanOperationInfo(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 * 
	 * Global JMX Attribute Getter
	 */
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (attribute.equals(MINIMUM_POOL_SIZE)) {
			return String.valueOf(_minPoolSize);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String,
	 * java.lang.Object[], java.lang.String[])
	 * 
	 * Global JMX Operation Invoker
	 */
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		if (SET_MINIMUM_POOL_SIZE.equals(actionName)) {
			return setMinimumPoolSize((Integer) params[0]);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 * 
	 * N/A
	 */
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.
	 * AttributeList)
	 * 
	 * N/A
	 */
	public AttributeList setAttributes(AttributeList attributes) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 * 
	 * N/A
	 */
	public AttributeList getAttributes(String[] attributes) {
		return null;
	}

	/**
	 * Exposes desired attributes via JMX.
	 * 
	 * @return An array of MBeanAttributeInfo Objects.
	 */
	private MBeanAttributeInfo[] createMBeanAttributeInfo() {
		List<MBeanAttributeInfo> attributes = new ArrayList<MBeanAttributeInfo>();
		attributes.add(new MBeanAttributeInfo(MINIMUM_POOL_SIZE, String.class.getName(),
				"Retrieve the minimum pool size for object pools associated with this type.", true, false, false));
		return (MBeanAttributeInfo[]) attributes.toArray(new MBeanAttributeInfo[attributes.size()]);
	}

	/**
	 * Exposes desired operations via JMX.
	 * 
	 * @return An array of MBeanOperationInfo Objects.
	 */
	private MBeanOperationInfo[] createMBeanOperationInfo() {
		List<MBeanOperationInfo> ops = new ArrayList<MBeanOperationInfo>();
		ops.add(createSetMinimumPoolSize());
		return (MBeanOperationInfo[]) ops.toArray(new MBeanOperationInfo[ops.size()]);
	}

	/**
	 * Defines the implementation for the setMinimumPoolSize operation.
	 * 
	 * @return The MBeanOperationInfo Object supporting the setMinimumPoolSize
	 *         operation.
	 */
	private MBeanOperationInfo createSetMinimumPoolSize() {
		MBeanParameterInfo[] params = new MBeanParameterInfo[1];
		params[0] = new MBeanParameterInfo("minimumPoolSize", int.class.getName(),
				"The minimum size a pool can be reaped down to during a cleanup task.");
		return new MBeanOperationInfo(SET_MINIMUM_POOL_SIZE,
				"Reset the minimum pool size for object pools associated with this pool type.", params,
				String.class.getName(), MBeanOperationInfo.ACTION);
	}

	/**
	 * Provides the implementation for setting the pool type minimum pool size
	 * via JMX.
	 * 
	 * @param size
	 *            The new minimum pool size.
	 * @return A status message.
	 */
	public String setMinimumPoolSize(int size) {
		if (size >= 0) {
			_minPoolSize.set(size);
			return "Minimum pool size for object pools registered to the " + _name
					+ " pool type was successfully set to " + size;
		} else {
			return "Unable to set minimum pool size to a value less than 0";
		}
	}

}
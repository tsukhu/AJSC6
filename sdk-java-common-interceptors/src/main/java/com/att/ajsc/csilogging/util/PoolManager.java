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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

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
 * The PoolManager is responsible for invoking each registered ObjectPool's
 * clean method over a configurable interval.
 */
public class PoolManager implements DynamicMBean {

	/*
	 * Constants for the PoolManager MBean attributes/operations.
	 */
	public static final String POOL_MANAGER_EVICTION_INTERVAL = "poolManagerEvictionInterval";
	public static final String SET_POOL_MANAGER_EVICTION_INTERVAL = "setPoolManagerEvictionInterval";
	public static final String CLEAN_OBJECT_POOLS = "cleanObjectPools";
	public static final String REGISTERED_POOLS = "registeredPools";

	/*
	 * Minimum/Default intervals for the eviction TimerTask of the PoolManager.
	 */
	public static final long MINIMUM_INTERVAL = 60000;
	public static final long DEFAULT_INTERVAL = 120000;

	/*
	 * Default process wide shared PoolManger name.
	 */
	public static final String DEFAULT_POOL_MANAGER_NAME = "Default";

	/*
	 * A cache of PoolManager objects available for reuse.
	 */
	private static HashMap<String, PoolManager> s_poolManagerCache = new HashMap<String, PoolManager>();

	/*
	 * The PoolManager's name;
	 */
	private String _name;

	/*
	 * The collection of ObjectPool's managed by the PoolManager.
	 */
	private List<ObjectPool> _objectPools;

	/*
	 * The Timer for the PoolManager.
	 */
	private Timer _timer;

	/*
	 * The PoolManager's Timer interval.
	 */
	private AtomicLong _timerInterval = new AtomicLong(DEFAULT_INTERVAL);

	/*
	 * A lock to synchronize on Timer changes to prevent concurrent access.
	 */
	private Object _timerLock = new Object();

	/*
	 * The management interface exposed by the PoolManager.
	 */
	private MBeanInfo _mbeanInfo;

	/**
	 * Returns the default PoolManager instance (creating it if necessary).
	 * 
	 * @return The default PoolManager instance.
	 */
	public static PoolManager getOrCreate() {
		synchronized (s_poolManagerCache) {
			if (s_poolManagerCache.containsKey(DEFAULT_POOL_MANAGER_NAME)) {
				return s_poolManagerCache.get(DEFAULT_POOL_MANAGER_NAME);
			} else {
				PoolManager poolManager = new PoolManager(DEFAULT_POOL_MANAGER_NAME);
				s_poolManagerCache.put(DEFAULT_POOL_MANAGER_NAME, poolManager);
				return poolManager;
			}
		}
	}

	/**
	 * Returns the PoolManager instance associated with the supplied name
	 * (creating it if necessary).
	 * 
	 * @param poolManagerName
	 *            The name of the PoolManager instance to return.
	 * @return The PoolManager instance associated with the supplied name.
	 */
	public static PoolManager getOrCreate(String poolManagerName) {
		if (poolManagerName == null || poolManagerName.isEmpty()) {
			poolManagerName = DEFAULT_POOL_MANAGER_NAME;
		}
		synchronized (s_poolManagerCache) {
			if (s_poolManagerCache.containsKey(poolManagerName)) {
				return s_poolManagerCache.get(poolManagerName);
			} else {
				PoolManager poolManager = new PoolManager(poolManagerName);
				s_poolManagerCache.put(poolManagerName, poolManager);
				return poolManager;
			}
		}
	}

	/**
	 * Adds the supplied ObjectPool to the list of ObjectPool's managed by this
	 * PoolManager.
	 * 
	 * @param objectPool
	 *            The ObjectPool to be managed.
	 */
	public void add(ObjectPool objectPool) {
		_objectPools.add(objectPool);
	}

	/**
	 * Constructs and starts the process PoolManager and registers it's MBean.
	 */
	private PoolManager(String name) {
		_name = name;
		_objectPools = Collections.synchronizedList(new ArrayList<ObjectPool>());

		// determine the eviction interval
		String evictionIntervalMillis;
		if (DEFAULT_POOL_MANAGER_NAME.equals(name)) {
			evictionIntervalMillis = System.getProperty("poolmanager.eviction.interval.millis");
		} else {
			evictionIntervalMillis = System.getProperty(name + ".poolmanager.eviction.interval.millis");
		}
		if (evictionIntervalMillis != null) {
			try {
				long interval = Long.parseLong(evictionIntervalMillis);
				if (interval > MINIMUM_INTERVAL) {
					_timerInterval.set(interval);
				} else {
					_timerInterval.set(MINIMUM_INTERVAL);
				}
			} catch (NumberFormatException e) {
				// use default
			}
		}

		// start the TimerTask using the eviction interval
		setPoolManagerEvictionInterval(_timerInterval.get());

		// register the MBean
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName poolManagerName = new ObjectName("com.att.ajsc:name=" + name + ",type=PoolManagers");
			mbs.registerMBean(this, poolManagerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Provides the TimerTask implementation for the PoolManager.
	 * 
	 * Simply invokes the clean method on all registered ObjectPools over the
	 * specified interval.
	 */
	private class EvictorTask extends TimerTask {
		public void run() {
			cleanObjectPools();
		}
	}

	/**
	 * Provides lazy instantiation of the PoolManager's MBeanInfo, yet allows
	 * for reuse.
	 * 
	 * @return The PoolManager's MBeanInfo object.
	 */
	public MBeanInfo getMBeanInfo() {
		if (_mbeanInfo == null) {
			_mbeanInfo = createMBeanInfo();
		}
		return _mbeanInfo;
	}

	/**
	 * Privately used function to create and return the MBeanInfo object
	 * associated with the PoolManager.
	 * 
	 * @return The PoolManager's MBeanInfo object.
	 */
	private MBeanInfo createMBeanInfo() {
		return new MBeanInfo(getClass().getName(), "Responsible for invoking the cleanup process of registered pools.",
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
		if (POOL_MANAGER_EVICTION_INTERVAL.equals(attribute)) {
			return String.valueOf(_timerInterval);
		} else if (REGISTERED_POOLS.equals(attribute)) {
			return String.valueOf(_objectPools.size());
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
		if (SET_POOL_MANAGER_EVICTION_INTERVAL.equals(actionName)) {
			return setPoolManagerEvictionInterval((Long) params[0]);
		} else if (CLEAN_OBJECT_POOLS.equals(actionName)) {
			return cleanObjectPools();
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
		attributes.add(new MBeanAttributeInfo(POOL_MANAGER_EVICTION_INTERVAL, String.class.getName(),
				"Retrieve the interval time used between registered pool cleanup tasks.", true, false, false));
		attributes.add(new MBeanAttributeInfo(REGISTERED_POOLS, String.class.getName(),
				"Retrieve the number of pools registered to the PoolManager.", true, false, false));
		return (MBeanAttributeInfo[]) attributes.toArray(new MBeanAttributeInfo[attributes.size()]);
	}

	/**
	 * Exposes desired operations via JMX.
	 * 
	 * @return An array of MBeanOperationInfo Objects.
	 */
	private MBeanOperationInfo[] createMBeanOperationInfo() {
		List<MBeanOperationInfo> ops = new ArrayList<MBeanOperationInfo>();
		ops.add(createSetPoolManagerEvictionInterval());
		ops.add(createCleanObjectPools());
		return (MBeanOperationInfo[]) ops.toArray(new MBeanOperationInfo[ops.size()]);
	}

	/**
	 * Defines the implementation for the setPoolManagerEvictionInterval
	 * operation.
	 * 
	 * @return The MBeanOperationInfo Object supporting the
	 *         setPoolManagerEvictionInterval operation.
	 */
	private MBeanOperationInfo createSetPoolManagerEvictionInterval() {
		MBeanParameterInfo[] params = new MBeanParameterInfo[1];
		params[0] = new MBeanParameterInfo("evictionIntervalMillis", long.class.getName(),
				"Interval in millis between the start of evictor task executions.");
		return new MBeanOperationInfo(SET_POOL_MANAGER_EVICTION_INTERVAL,
				"Reset the timer task that launches the eviction process of all registered pools with the specified interval (in millis) between executions.",
				params, String.class.getName(), MBeanOperationInfo.ACTION);
	}

	/**
	 * Defines the implementation for using JMX to directly invoke the clean
	 * operation on registered ObjectPools.
	 * 
	 * @return The MBeanOperationInfo Object supporting the cleanObjectPools
	 *         operation.
	 */
	private MBeanOperationInfo createCleanObjectPools() {
		MBeanParameterInfo[] params = new MBeanParameterInfo[0];
		return new MBeanOperationInfo(CLEAN_OBJECT_POOLS,
				"Invokes the clean operation on all registered object pools..", params, String.class.getName(),
				MBeanOperationInfo.ACTION);
	}

	/**
	 * Provides the implementation for setting the pool manager eviction
	 * interval via JMX.
	 * 
	 * @param interval
	 *            The new interval in milliseconds.
	 * @return A status message.
	 */
	public String setPoolManagerEvictionInterval(long interval) {
		if (interval < MINIMUM_INTERVAL) {
			return "Unable to reschedule TimerTask for an interval less than " + MINIMUM_INTERVAL + " milliseconds.";
		}
		synchronized (_timerLock) {
			if (_timer != null) {
				_timer.cancel();
			}

			_timerInterval.set(interval);

			_timer = new Timer(true);
			_timer.schedule(new EvictorTask(), interval, interval);

			return "TimerTask successfully scheduled for every " + interval + " milliseconds";
		}
	}

	public String cleanObjectPools() {
		for (int i = 0; i < _objectPools.size(); i++) {
			_objectPools.get(i).clean();
		}

		return "The clean operation has been invoked on all registered pools";
	}

	/**
	 * Returns the name of the PoolManager.
	 * 
	 * @return The PoolManager's name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the PoolManager's timer interval in milliseconds.
	 * 
	 * @return The PoolManager's timer interval in milliseconds.
	 */
	public long getPoolManagerEvictionInterval() {
		return _timerInterval.get();
	}

	/**
	 * Returns the number of registered object pools.
	 * 
	 * @return The number of registered object pools.
	 */
	public int getRegisteredNumberOfPools() {
		return _objectPools.size();
	}

}
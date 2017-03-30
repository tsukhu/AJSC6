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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.modelmbean.DescriptorSupport;

public class ObjectPool implements DynamicMBean {

	/*
	 * Constants for the ObjectPool MBean attribute/operations
	 */
	public static final String CURRENT_POOL_SIZE = "currentPoolSize";
	public static final String AVAILABLE = "available";
	public static final String TOTAL_OBJECTS_CREATED = "totalObjectsCreated";
	public static final String TOTAL_OBJECTS_DESTROYED = "totalObjectsDestroyed";
	public static final String TOTAL_TIMES_ACQUIRED = "totalTimesAcquired";
	public static final String MAX_CONCURRENCY = "maxConcurrency";
	public static final String POOL_TYPE = "poolType";
	public static final String POOL_MANAGER = "poolManager";

	/*
	 * The Object pool.
	 */
	private ArrayList<Object> _pool = new ArrayList<Object>();

	/*
	 * The Object creator.
	 */
	private PoolableObjectCreator _poolableObjectCreator;

	/*
	 * The ObjectPool's PootType.
	 */
	private PoolType _poolType;

	/*
	 * The ObjectPool's PoolManager.
	 */
	private PoolManager _poolManager;

	/*
	 * The ObjectPool's minimum pool size over the last interval.
	 */
	private AtomicInteger _intervalMinPoolSize = new AtomicInteger(0);

	/*
	 * The number of Object alive (both in the pool and leased out).
	 */
	private AtomicInteger _objectsAlive = new AtomicInteger(0);

	/*
	 * The total number of Object's created during the life-span of the process.
	 */
	private AtomicLong _totalObjectsCreated = new AtomicLong(0);

	/*
	 * The total number of Object's destroyed during the life-span of the
	 * process.
	 */
	private AtomicLong _totalObjectsDestroyed = new AtomicLong(0);

	/*
	 * The total number of times an Object from this pool has been acquired.
	 */
	private AtomicLong _totalTimesAcquired = new AtomicLong(0);

	/*
	 * The maximum number of Objects leased out from the pool concurrently.
	 */
	private AtomicInteger _maxConcurrency = new AtomicInteger(0);

	/*
	 * The ManagementInterface exposed by the PoolManager.
	 */
	private MBeanInfo _mbeanInfo;

	/**
	 * Creates an ObjectPool with the specified PoolableObjectCreator and
	 * assigns it to the supplied PoolType (poolTypeName) and default
	 * poolManager.
	 * 
	 * The resulting ObjectPool is initialized to its minimum size and
	 * registered with the pool manager for cleanup invocations.
	 * 
	 * @param poolTypeName
	 *            The name of the PoolType to associate the ObjectPool with.
	 * @param poolableObjectCreator
	 *            The PoolableObjectCreator used to create new Objects.
	 */
	public ObjectPool(String poolTypeName, PoolableObjectCreator poolableObjectCreator) {
		_poolableObjectCreator = poolableObjectCreator;
		_poolType = PoolType.instance(poolTypeName);
		_intervalMinPoolSize.set(_poolType.getMinPoolSize());

		initialize();

		_poolManager = PoolManager.getOrCreate();
		_poolManager.add(this);
	}

	/**
	 * Creates an ObjectPool with the specified PoolableObjectCreator and
	 * assigns it to the supplied PoolType (poolTypeName) and poolManager
	 * (poolManagerName).
	 * 
	 * The resulting ObjectPool is initialized to its minimum size and
	 * registered with the pool manager for cleanup invocations.
	 * 
	 * @param poolTypeName
	 *            The name of the PoolType to associate the ObjectPool with.
	 * @param poolManagerName
	 *            The name of the PoolManager to register the ObjectPool with.
	 * @param poolableObjectCreator
	 *            The PoolableObjectCreator used to create new Objects.
	 */
	public ObjectPool(String poolTypeName, String poolManagerName, PoolableObjectCreator poolableObjectCreator) {
		_poolableObjectCreator = poolableObjectCreator;
		_poolType = PoolType.instance(poolTypeName);
		_intervalMinPoolSize.set(_poolType.getMinPoolSize());

		initialize();

		_poolManager = PoolManager.getOrCreate(poolManagerName);
		_poolManager.add(this);
	}

	/**
	 * Initializes the ObjectPool to its minimum size based on the attribute
	 * from its associated PoolType.
	 */
	private void initialize() {
		synchronized (_pool) {
			for (int i = 0; i < _poolType.getMinPoolSize(); i++) {
				release(create());
			}
		}
	}

	/**
	 * Creates and returns a new Object.
	 * 
	 * @return The newly created Object.
	 */
	private Object create() {
		Object object = _poolableObjectCreator.createPoolableObject();
		_objectsAlive.incrementAndGet();
		_totalObjectsCreated.incrementAndGet();
		return object;
	}

	/**
	 * Returns the supplied Object back to the pool.
	 * 
	 * @param object
	 *            The Object to return to the pool.
	 */
	public void release(Object object) {
		synchronized (_pool) {
			_pool.add(object);
		}
	}

	/**
	 * Returns an Object of the pool (one from the pool if it exists, otherwise,
	 * creates a new one).
	 * 
	 * @return An Object of the pool.
	 * 
	 */
	public Object acquirePoolableObject() {
		_totalTimesAcquired.incrementAndGet();
		synchronized (_pool) {
			if (!_pool.isEmpty()) {
				_intervalMinPoolSize.set(Math.min(_intervalMinPoolSize.get(), _pool.size() - 1));
				_maxConcurrency.set(Math.max(_maxConcurrency.get(), _objectsAlive.get() - _pool.size() + 1));
				return _pool.remove(0);
			}
		}
		_maxConcurrency.set(Math.max(_maxConcurrency.get(), _objectsAlive.get() + 1));
		return create();
	}

	/**
	 * Invoked by the PoolManager over a specified interval to shrink the pool
	 * if necessary.
	 * 
	 * Rules: 1. We never shrink below the minimum pool size 2. We never shrink
	 * lower then the maxConcurrncy used over the last interval plus 1
	 * 
	 * If minimum pool size was increased during the last interval via JMX, we
	 * adjust accordingly.
	 * 
	 */
	public void clean() {
		synchronized (_pool) {
			int maxAllowed = _objectsAlive.get() - _poolType.getMinPoolSize();
			int maxReapableObjects = _poolType.getMinPoolSize() == 0 ? _intervalMinPoolSize.get()
					: _intervalMinPoolSize.get() - 1;
			int difference = maxAllowed > maxReapableObjects ? maxReapableObjects : maxAllowed;
			for (int i = 0; i < difference; i++) {
				if (!_pool.isEmpty()) {
					_pool.remove(0);
					_objectsAlive.decrementAndGet();
					_totalObjectsDestroyed.incrementAndGet();
				} else {
					break;
				}
			}

			if (_objectsAlive.get() < _poolType.getMinPoolSize()) {
				int gap = _poolType.getMinPoolSize() - _objectsAlive.get();
				for (int i = 0; i < gap; i++) {
					release(create());
				}

			}
			_intervalMinPoolSize.set(_pool.size());
		}
	}

	/**
	 * Registers the ObjectPool's MBean.
	 * 
	 * @param name
	 *            The name of the ObjectPool.
	 * @param objectPool
	 *            The ObjectPool to register.
	 */
	public static void registerManagement(String name, ObjectPool objectPool) {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName objectPoolName = new ObjectName("com.att.ajsc:name=" + name + ",type=ObjectPools");
			mbs.registerMBean(objectPool, objectPoolName);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		if (attribute.equals(AVAILABLE)) {
			return String.valueOf(_pool.size());
		} else if (attribute.equals(CURRENT_POOL_SIZE)) {
			return String.valueOf(_objectsAlive);
		} else if (attribute.equals(TOTAL_OBJECTS_CREATED)) {
			return String.valueOf(_totalObjectsCreated);
		} else if (attribute.equals(TOTAL_OBJECTS_DESTROYED)) {
			return String.valueOf(_totalObjectsDestroyed);
		} else if (attribute.equals(TOTAL_TIMES_ACQUIRED)) {
			return String.valueOf(_totalTimesAcquired);
		} else if (attribute.equals(MAX_CONCURRENCY)) {
			return String.valueOf(_maxConcurrency);
		} else if (attribute.equals(POOL_TYPE)) {
			return _poolType.getName();
		} else if (attribute.equals(POOL_MANAGER)) {
			return _poolManager.getName();
		} else {
			return null;
		}
	}

	/**
	 * Provides lazy instantiation of the ObjectPool's MBeanInfo, yet allows for
	 * reuse.
	 * 
	 * @return The ObjectPool's MBeanInfo object.
	 */
	public MBeanInfo getMBeanInfo() {
		if (_mbeanInfo == null) {
			_mbeanInfo = createMBeanInfo();
		}
		return _mbeanInfo;
	}

	/**
	 * Privately used function to create and return the MBeanInfo object
	 * associated with this ObjectPool.
	 * 
	 * @return The ObjectPool's MBeanInfo object.
	 */
	public MBeanInfo createMBeanInfo() {
		return new MBeanInfo(getClass().getName(), "Provides high-level management of object pools.",
				createMBeanAttributeInfo(), createMBeanConstructorInfo(), null, null);
	}

	/**
	 * Exposes desired attributes via JMX.
	 * 
	 * @return An array of MBeanAttributeInfo Object's.
	 */
	private MBeanAttributeInfo[] createMBeanAttributeInfo() {
		List<MBeanAttributeInfo> attributes = new ArrayList<MBeanAttributeInfo>();
		attributes.add(new MBeanAttributeInfo(AVAILABLE, String.class.getName(),
				"Retrieve the number of available objects sitting in the pool.", true, false, false));
		attributes.add(new MBeanAttributeInfo(TOTAL_OBJECTS_CREATED, String.class.getName(),
				"Retrieve the number of objects created over the lifespan of this pool.", true, false, false));
		attributes.add(new MBeanAttributeInfo(TOTAL_OBJECTS_DESTROYED, String.class.getName(),
				"Retrieve the number of objects destroyed over the lifespan of this pool.", true, false, false));
		attributes.add(new MBeanAttributeInfo(TOTAL_TIMES_ACQUIRED, String.class.getName(),
				"Retrieve the number of times an object from this pool has been acquired for use.", true, false,
				false));
		attributes.add(new MBeanAttributeInfo(CURRENT_POOL_SIZE, String.class.getName(),
				"Retrieve the number of objects alive (either in the pool or in use).", true, false, false));
		attributes.add(new MBeanAttributeInfo(POOL_TYPE, String.class.getName(),
				"Retrieve the name of the pool type associated with this object pool.", true, false, false));
		attributes.add(new MBeanAttributeInfo(POOL_MANAGER, String.class.getName(),
				"Retrieve the name of the pool manager associated with this object pool.", true, false, false));
		attributes.add(new MBeanAttributeInfo(MAX_CONCURRENCY, String.class.getName(),
				"Retrieve the maximum number of concurrent objects from this pool leased out over the lifespan of the process.",
				true, false, false));
		return (MBeanAttributeInfo[]) attributes.toArray(new MBeanAttributeInfo[attributes.size()]);
	}

	/**
	 * Defines a custom MBeanConstructor for the ObjectPool to provide access to
	 * the descriptor for displaying meta-data about the ObjectPool.
	 * 
	 * @return An array of MBeanConstructorInfo Object's.
	 */
	private MBeanConstructorInfo[] createMBeanConstructorInfo() {
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[1];
		constructors[0] = new MBeanConstructorInfo("ObjectPool",
				"Provides a pool of Objects associated with the meta-data in the descriptor", null,
				createMBeanDescriptor());
		return constructors;
	}

	/**
	 * Defines a custom MBeanDesciptor for the ObjectPool expsoing meta-data
	 * specific to the Object's being pooled.
	 * 
	 * @return The MBean Descriptor for the ObjectPool.
	 */
	private Descriptor createMBeanDescriptor() {
		Map<String, String> properties = _poolableObjectCreator.getMetadata();
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.putAll(properties);
		Descriptor descriptor = new DescriptorSupport();
		Iterator<String> iterator = treeMap.descendingKeySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			descriptor.setField(key, treeMap.get(key));
		}
		return descriptor;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String,
	 * java.lang.Object[], java.lang.String[])
	 * 
	 * N/A
	 */
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		return null;
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
	 * @see
	 * javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 * 
	 * N/A
	 */
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
	}

}
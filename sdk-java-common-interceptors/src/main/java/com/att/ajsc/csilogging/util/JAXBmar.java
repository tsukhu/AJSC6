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

/**
 * JAXBumar.java
 *
 * Created on: Apr 10, 2009
 * Created by: jg1555
 *
 * � 2009 SBC Knowledge Ventures, L.P. All rights reserved.
 ******************************************************************* 
 * RESTRICTED - PROPRIETARY INFORMATION The Information contained 
 * herein is for use only by authorized employees of AT&T Services, 
 * Inc., and authorized Affiliates of AT&T Services, Inc., and is 
 * not for general distribution within or outside the respective 
 * companies. 
 *******************************************************************
 */
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.att.ajsc.csilogging.util.JAXBContextHelper;
import com.att.ajsc.csilogging.util.RuntimeTIG;

/**
 * The JAXBmar class provides a thread-safe implementation for using non-thread
 * safe JAXB Marshaller's.
 */
public class JAXBmar implements PoolableObjectCreator {

	/*
	 * A cache of JAXBmar objects available for reuse.
	 */
	private static HashMap<String, JAXBmar> s_jaxbMarCache = new HashMap<String, JAXBmar>();

	/*
	 * Offset to uniquely name the JAXBumar MBean registration.
	 */
	private static AtomicInteger s_counter = new AtomicInteger();

	/*
	 * The JAXBContext used to create underlying Unmarshaller object's.
	 */
	private JAXBContext _jaxbContext = null;

	/*
	 * The classes bound to the JAXBContext.
	 */
	private Class<?>[] _classes;

	/*
	 * Boolean flag indicating whether or not to include the XML fragment in the
	 * marshalled XML data.
	 */
	private boolean _fragment;

	/*
	 * The output encoding in the marshalled XML data.
	 */
	private String _encoding;

	/*
	 * The QName used to marshal the XML data.
	 */
	private QName _qname;

	/*
	 * The underlying ObjectPool (marshaller's) for this implementation.
	 */
	private ObjectPool _objectPool;

	/**
	 * Be careful....always returns null. The use of this method should be
	 * removed as pool initialization is done automatically.
	 * 
	 * Not sure if M2E-CSI is using this anymore so it hasn't been removed.
	 * @return Marshaller
	 */
	@Deprecated
	public Marshaller get() {
		return null;
	}

	/**
	 * Generates a key name based on the supplied inputs for storing this object
	 * in the static HashMap for reuse.
	 * 
	 * @param encoding
	 *            The output encoding in the marshalled XML data.
	 * @param fragment
	 *            Boolean flag indicating whether or not to include the XML
	 *            fragment in the marshalled XML data.
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param classes
	 *            An array of classes bound to the JAXBContext.
	 * @return A formatted string corresponding to the meta-data of this object.
	 */
	public static String getKey(String encoding, boolean fragment, QName qname, Class<?>... classes) {
		StringBuilder sb = new StringBuilder();
		if (encoding == null) {
			sb.append("null");
		} else {
			sb.append(encoding);
		}
		sb.append("|");
		sb.append(fragment);
		sb.append("|");
		if (qname == null) {
			sb.append("null");
		} else {
			sb.append(qname);
		}
		if (classes != null) {
			for (Class<?> c : classes) {
				sb.append("|");
				sb.append(c.getName());
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes.
	 * 
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 * @return A JAXBmar instance bound to the supplied classes.
	 */
	public static JAXBmar instance(Class<?>... classes) {
		String key = getKey("UTF-8", false, null, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the supplied classes.
	 * 
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 */
	public JAXBmar(Class<?>... classes) {
		_encoding = "UTF-8";
		_fragment = false;
		_qname = null;
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes and specified fragment handling.
	 * 
	 * @param isFragment
	 *            A boolean flag indicating whether or not to include the XML
	 *            fragment in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 * @return A JAXBmar instance bound to the supplied classes and specified
	 *         fragment handling.
	 */
	public static JAXBmar instance(boolean isFragment, Class<?>... classes) {
		String key = getKey("UTF-8", isFragment, null, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(isFragment, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the supplied classes and specified
	 * fragment handling.
	 * 
	 * @param isFragment
	 *            A boolean flag indicating whether or not to include the XML
	 *            fragment in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 */
	public JAXBmar(boolean isFragment, Class<?>... classes) {
		_encoding = "UTF-8";
		_fragment = isFragment;
		_qname = null;
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes and specified encoding.
	 * 
	 * @param encoding
	 *            The output encoding in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 * @return A JAXBmar instance bound to the supplied classes and specified
	 *         encoding.
	 */
	public static JAXBmar instance(String encoding, Class<?>... classes) {
		String key = getKey(encoding, false, null, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(encoding, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the supplied classes and specified
	 * encoding.
	 * 
	 * @param encoding
	 *            The output encoding in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 */
	public JAXBmar(String encoding, Class<?>... classes) {
		_encoding = encoding;
		_fragment = false;
		_qname = null;
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes and QName.
	 * 
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 * @return A JAXBmar instance bound to the supplied classes and QName.
	 */
	public static JAXBmar instance(QName qname, Class<?>... classes) {
		String key = getKey("UTF-8", false, qname, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(qname, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the supplied classes and QName.
	 * 
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 */
	public JAXBmar(QName qname, Class<?>... classes) {
		_encoding = "UTF-8";
		_fragment = false;
		_qname = qname;
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes, QName, and encoding.
	 * 
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param encoding
	 *            The output encoding in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 * @return A JAXBmar instance bound to the supplied classes, QName and
	 *         encoding.
	 */
	public static JAXBmar instance(QName qname, String encoding, Class<?>... classes) {
		String key = getKey(encoding, false, qname, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(qname, encoding, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the supplied classes, QName, and
	 * encoding.
	 * 
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param encoding
	 *            The output encoding in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 */
	public JAXBmar(QName qname, String encoding, Class<?>... classes) {
		_encoding = encoding;
		_fragment = false;
		_qname = qname;
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes, QName, and fragment handling.
	 * 
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param isFragment
	 *            A boolean flag indicating whether or not to include the XML
	 *            fragment in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 * @return A JAXBmar instance bound to the supplied classes, QName and
	 *         fragment handling.
	 */
	public static JAXBmar instance(QName qname, boolean isFragment, Class<?>... classes) {
		String key = getKey("UTF-8", isFragment, qname, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(qname, isFragment, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the supplied classes, QName, and fragment handling.
	 * 
	 * @param qname
	 *            The QName used to marshal the XML data.
	 * @param isFragment
	 *            A boolean flag indicating whether or not to include the XML
	 *            fragment in the marshalled XML data.
	 * @param classes
	 *            The classes bound to the JAXBContext for the desired JAXBmar
	 *            instance.
	 */
	public JAXBmar(QName qname, boolean isFragment, Class<?>... classes) {
		_encoding = "UTF-8";
		_fragment = isFragment;
		_qname = qname;
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the QName associated with the supplied classes in the
	 * supplied RuntimeTIG.
	 * 
	 * @param tig
	 *            The RuntimeTIG used to lookup the class name from the supplied
	 *            QName.
	 * @param classes
	 *            The classes used to lookup the QName in the supplied
	 *            RuntimeTIG.
	 * @return A JAXBmar instance bound to the QName associated with the classes
	 *         in the RuntimeTIG.
	 * 
	 *         Note: This method is not guaranteed to be safe and is therefore
	 *         not recommended for future use.
	 */
	@Deprecated
	public static JAXBmar instance(RuntimeTIG tig, Class<?>... classes) {
		QName qname = tig.getRealQName(classes[0]);

		String key = getKey("UTF-8", false, qname, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(tig, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the QName associated with the
	 * supplied classes in the supplied RuntimeTIG.
	 * 
	 * @param tig
	 *            The RuntimeTIG used to lookup the class name from the supplied
	 *            QName.
	 * @param classes
	 *            The classes used to lookup the QName in the supplied
	 *            RuntimeTIG.
	 * 
	 *            Note: This method is not guaranteed to be safe and is
	 *            therefore not recommended for future use.
	 */
	@Deprecated
	public JAXBmar(RuntimeTIG tig, Class<?>... classes) {
		_encoding = "UTF-8";
		_fragment = false;
		_qname = tig.getRealQName(classes[0]);
		_classes = classes;

		try {
			_jaxbContext = JAXBContextHelper.instance().get(classes[0]);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}

	/**
	 * Returns a JAXBmar (existing if available in the cache, otherwise new)
	 * instance bound to the class name associated with the supplied QName in
	 * the supplied RuntimeTIG.
	 * 
	 * @param tig
	 *            The RuntimeTIG used to lookup the class name from the supplied
	 *            QName.
	 * @param qname
	 *            The QName used to lookup the class name in the supplied
	 *            RuntimeTIG.
	 * @return A JAXBmar instance bound to the class name associated with the
	 *         QName in the RuntimeTIG.
	 */
	public static JAXBmar instance(RuntimeTIG tig, QName qname) {
		Class<?>[] classes;
		try {
			classes = new Class<?>[] {
					Thread.currentThread().getContextClassLoader().loadClass(tig.classNameForElement(qname)) };
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to load required JAXBContext class for this JAXBumar", e);
		}

		String key = getKey("UTF-8", false, qname, classes);

		synchronized (s_jaxbMarCache) {
			if (s_jaxbMarCache.containsKey(key)) {
				return s_jaxbMarCache.get(key);
			} else {
				JAXBmar jaxbMar = new JAXBmar(qname, classes);
				s_jaxbMarCache.put(key, jaxbMar);
				return jaxbMar;
			}
		}
	}

	/**
	 * Constructs a JAXBmar object bound to the class name associated with the
	 * supplied QName in the supplied RuntimeTIG.
	 * 
	 * @param tig
	 *            The RuntimeTIG used to lookup the class name from the supplied
	 *            QName.
	 * @param qname
	 *            The QName used to lookup the class name in the supplied
	 *            RuntimeTIG.
	 */
	public JAXBmar(RuntimeTIG tig, QName qname) {
		_encoding = "UTF-8";
		_fragment = false;
		_qname = qname;

		try {
			_classes = new Class<?>[] {
					Thread.currentThread().getContextClassLoader().loadClass(tig.classNameForElement(qname)) };
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to load required JAXBContext class for this JAXBmar", e);
		}

		try {
			_jaxbContext = JAXBContextHelper.instance().get(_classes);
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot instantiate the JAXBContext for this JAXBmar", e);
		}

		_objectPool = new ObjectPool("jaxb", this);

		ObjectPool.registerManagement("JAXBmar-" + s_counter.incrementAndGet(), _objectPool);
	}


	@SuppressWarnings("unchecked")
	public <O> O marshal(O o, Writer writer) throws JAXBException {
		Marshaller marshaller = null;
		try {
			marshaller = (Marshaller) _objectPool.acquirePoolableObject();
			if (_qname == null) {
				marshaller.marshal(o, writer);
			} else {
				marshaller.marshal(new JAXBElement<O>(_qname, (Class<O>) _classes[0], o), writer);
			}
			return o;
		} finally {
			if (marshaller != null) {
				_objectPool.release(marshaller);
			}
		}
	}


	@SuppressWarnings("unchecked")
	public <O> O marshal(O o, OutputStream os) throws JAXBException {
		Marshaller marshaller = null;
		try {
			marshaller = (Marshaller) _objectPool.acquirePoolableObject();
			if (_qname == null) {
				marshaller.marshal(o, os);
			} else {
				marshaller.marshal(new JAXBElement<O>(_qname, (Class<O>) _classes[0], o), os);
			}
			return o;
		} finally {
			if (marshaller != null) {
				_objectPool.release(marshaller);
			}
		}
	}


	@SuppressWarnings("unchecked")
	public <O> O marshal(O o, Document doc) throws JAXBException {
		Marshaller marshaller = null;
		try {
			marshaller = (Marshaller) _objectPool.acquirePoolableObject();
			if (_qname == null) {
				marshaller.marshal(o, doc);
			} else {
				marshaller.marshal(new JAXBElement<O>(_qname, (Class<O>) _classes[0], o), doc);
			}
			return o;
		} finally {
			if (marshaller != null) {
				_objectPool.release(marshaller);
			}
		}
	}


	public <O> O marshal(O o, Writer writer, Class<O> theClass) throws JAXBException {
		Marshaller marshaller = null;
		try {
			marshaller = (Marshaller) _objectPool.acquirePoolableObject();
			if (_qname == null) {
				marshaller.marshal(o, writer);
			} else {
				marshaller.marshal(new JAXBElement<O>(_qname, theClass, o), writer);
			}
			return o;
		} finally {
			if (marshaller != null) {
				_objectPool.release(marshaller);
			}
		}
	}


	public <O> O marshal(O o, OutputStream os, Class<O> theClass) throws JAXBException {
		Marshaller marshaller = null;
		try {
			marshaller = (Marshaller) _objectPool.acquirePoolableObject();
			if (_qname == null) {
				marshaller.marshal(o, os);
			} else {
				marshaller.marshal(new JAXBElement<O>(_qname, theClass, o), os);
			}
			return o;
		} finally {
			if (marshaller != null) {
				_objectPool.release(marshaller);
			}
		}
	}


	public <O> O marshal(O o, Document doc, Class<O> theClass) throws JAXBException {
		Marshaller marshaller = null;
		try {
			marshaller = (Marshaller) _objectPool.acquirePoolableObject();
			if (_qname == null) {
				marshaller.marshal(o, doc);
			} else {
				marshaller.marshal(new JAXBElement<O>(_qname, theClass, o), doc);
			}
			return o;
		} finally {
			if (marshaller != null) {
				_objectPool.release(marshaller);
			}
		}
	}


	public <O> String stringify(O o) throws JAXBException {
		StringWriter sw = new StringWriter();
		marshal(o, sw);
		return sw.toString();
	}

	/**
	 * Returns the class bound to the JAXBmar.
	 * 
	 * @return The class bound to the JAXBmar.
	 */
	public Class<?> getMarshalClass() {
		return _classes[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.att.m2e.obj.pool.PoolableObjectCreator#createPoolableObject()
	 */
	public Marshaller createPoolableObject() {
		try {
			Marshaller marshaller = _jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, _encoding);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, _fragment);
			return marshaller;
		} catch (JAXBException e) {
			throw new RuntimeException("Cannot create JAXBMarshaller for this thread", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.att.m2e.obj.pool.PoolableObjectCreator#getMetadata()
	 */
	public Map<String, String> getMetadata() {
		Map<String, String> metaDataMap = new HashMap<String, String>();
		metaDataMap.put("encoding", String.valueOf(_encoding));
		metaDataMap.put("fragment", String.valueOf(_fragment));
		metaDataMap.put("qname", String.valueOf(_qname));
		for (int i = 0; i < _classes.length; i++) {
			metaDataMap.put("class-" + i, _classes[i].getName());
		}
		return metaDataMap;
	}

}
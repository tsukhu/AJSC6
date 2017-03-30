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
 * RuntimeTIG.java
 *
 * Created on: Jun 4, 2009
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

import java.io.IOException;
import java.io.Reader;

import javax.xml.namespace.QName;

/**
 * There are certain elements of TypeInfoGroup we want at runtime, but we want
 * to remove all intermediary HashMaps, etc to save Memory once in Runtime Mode.
 * 
 * These methods are safe at runtime.
 */
public interface RuntimeTIG {

	/**
	 * Obtains the Java Package M2E will use based on Schema targetNamespace.
	 * 
	 * The least standardized piece of Schema in JAXB compiling is how the class
	 * names will be created.
	 * 
	 * Several "goofy" conventions include using "http://". Of course, it's not
	 * a URI, but most programmers would think it is a URI, so to prevent
	 * someone from attempting to actually use it as a URI, they code any "dots"
	 * inside "path" backwards.
	 * 
	 * i.e. "http://csi.cingular.com/CSI..."
	 * 
	 * However, it has become a convention if you end up using "http" in your
	 * namespace.
	 * 
	 * On the other hand, there is actually no reason to put "http://" in your
	 * namespace, and other nicer "conventions" have arisen, including using
	 * "java:com.cingular.csi", which actually looks like a java path, and just
	 * "com.cingular.csi", which is just as valid.
	 * 
	 * There is no telling what kind of additional "goofiness" will be found in
	 * the company and vendors of ATT,so we reserve the ability to code this
	 * within M2E in a convenient place.
	 * 
	 * @param namespace
	 *            The namespace used to derive the Java package name.
	 * @return The M2E Java package associated with the supplied namespace.
	 */
	public String getPkgFromNS(String namespace);

	/**
	 * Class names aren't actually QNames, but under certain circumstances JAXB
	 * requires the use of QName to marshall an object out to a String.
	 * 
	 * M2E analyzes the Schemas and saves off what QNames go with what Classes
	 * in an attempt to avoid these issues. However, it still is not always 100%
	 * safe.
	 * 
	 * @param cls
	 *            The class name used to return the QName from the RuntimeTIG.
	 * @return The QName associated with the supplied class name in the
	 *         RuntimeTIG.
	 */
	@Deprecated
	public QName getRealQName(Class<?> cls);

	/**
	 * Given a specified QName, M2E has the smarts to identify and return its
	 * underlying class.
	 * 
	 * @param qname
	 *            The QName for the associated class we want to derive.
	 * @return The class name corresponding to the supplied QName.
	 */
	public String classNameForElement(QName qname);

	/**
	 * Load up the pre-compiled information into maps that support these calls.
	 * 
	 * While schemas are not fully used by this class, the Schemas may be
	 * required for runtime validation. Thus, the schema Dir is where the system
	 * can find the start of the schema libraries.
	 * 
	 * @param xsdRootDir
	 *            The schema root directory.
	 * @param in
	 *            The Reader containing the pre-compiled data.
	 * @throws IOException
	 *             Thrown if the pre-compiled data cannot be loaded properly
	 *             into runtime objects.
	 */
	public void loadCompiled(String xsdRootDir, Reader in) throws IOException;

}
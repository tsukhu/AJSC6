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
package com.att.ajsc.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class Response implements HttpServletResponse {

	int sc;

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getCharacterEncoding() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getContentType() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setCharacterEncoding(String charset) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setContentLength(int len) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setContentLengthLong(long len) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setContentType(String type) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setBufferSize(int size) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public int getBufferSize() {

		return 0;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void flushBuffer() throws IOException {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void resetBuffer() {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean isCommitted() {

		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void reset() {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setLocale(Locale loc) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Locale getLocale() {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void addCookie(Cookie cookie) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public boolean containsHeader(String name) {
		return false;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String encodeURL(String url) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String encodeRedirectURL(String url) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String encodeUrl(String url) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String encodeRedirectUrl(String url) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void sendError(int sc, String msg) throws IOException {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void sendError(int sc) throws IOException {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void sendRedirect(String location) throws IOException {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setDateHeader(String name, long date) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void addDateHeader(String name, long date) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setHeader(String name, String value) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void addHeader(String name, String value) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setIntHeader(String name, int value) {

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void addIntHeader(String name, int value) {

	}

	public void setStatus(int sc) {
		this.sc = sc;

	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public void setStatus(int sc, String sm) {

	}

	public int getStatus() {
		return sc;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public String getHeader(String name) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Collection<String> getHeaders(String name) {
		return null;
	}

	/*
	 * Implementation not required, as this class is only used for testing
	 * 
	 */
	public Collection<String> getHeaderNames() {
		return null;
	}

}

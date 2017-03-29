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

package com.att.ajsc.common.messaging;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.att.ajsc.common.AjscProvider;

@AjscProvider
@PreMatching
@Priority(Integer.MIN_VALUE)
@SuppressWarnings("ClassWithMultipleLoggers")
public final class LoggingFilter implements ContainerRequestFilter, ClientRequestFilter, ContainerResponseFilter,
		ClientResponseFilter, WriterInterceptor {

	private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
	private static final String NOTIFICATION_PREFIX = "* ";
	private static final String REQUEST_PREFIX = "> ";
	private static final String RESPONSE_PREFIX = "< ";
	private static final String ENTITY_LOGGER_PROPERTY = LoggingFilter.class.getName() + ".entityLogger";
	private static final String LOGGING_ID_PROPERTY = LoggingFilter.class.getName() + ".id";
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String CHARSET_PARAMETER = "charset";

	private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR = new Comparator<Map.Entry<String, List<String>>>() {

		@Override
		public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
			return o1.getKey().compareToIgnoreCase(o2.getKey());
		}
	};

	private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;

	@SuppressWarnings("NonConstantLogger")
	private final Logger logger;
	private final AtomicLong _id = new AtomicLong(0);
	private final boolean printEntity;
	private final int maxEntitySize;

	public LoggingFilter() {
		this(LOGGER, false);
	}

	@SuppressWarnings("BooleanParameter")
	public LoggingFilter(final Logger logger, final boolean printEntity) {
		this.logger = logger;
		this.printEntity = printEntity;
		this.maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;
	}

	public LoggingFilter(final Logger logger, final int maxEntitySize) {
		this.logger = logger;
		this.printEntity = true;
		this.maxEntitySize = Math.max(0, maxEntitySize);
	}

	private void log(final StringBuilder b) {
		if (logger != null) {
			logger.info(b.toString());
		}
	}

	private StringBuilder prefixId(final StringBuilder b, final long id) {
		b.append(Long.toString(id)).append(" ");
		return b;
	}

	private void printRequestLine(final StringBuilder b, final String note, final long id, final String method,
			final URI uri) {
		prefixId(b, id).append(NOTIFICATION_PREFIX).append(note).append(" on thread ")
				.append(Thread.currentThread().getName()).append("\n");
		prefixId(b, id).append(REQUEST_PREFIX).append(method).append(" ").append(uri.toASCIIString()).append("\n");
	}

	private void printResponseLine(final StringBuilder b, final String note, final long id, final int status) {
		prefixId(b, id).append(NOTIFICATION_PREFIX).append(note).append(" on thread ")
				.append(Thread.currentThread().getName()).append("\n");
		prefixId(b, id).append(RESPONSE_PREFIX).append(Integer.toString(status)).append("\n");
	}

	private void printPrefixedHeaders(final StringBuilder b, final long id, final String prefix,
			final MultivaluedMap<String, String> headers) {
		for (final Map.Entry<String, List<String>> headerEntry : getSortedHeaders(headers.entrySet())) {
			final List<?> val = headerEntry.getValue();
			final String header = headerEntry.getKey();

			if (val.size() == 1) {
				prefixId(b, id).append(prefix).append(header).append(": ").append(val.get(0)).append("\n");
			} else {
				final StringBuilder sb = new StringBuilder();
				boolean add = false;
				for (final Object s : val) {
					if (add) {
						sb.append(',');
					}
					add = true;
					sb.append(s);
				}
				prefixId(b, id).append(prefix).append(header).append(": ").append(sb.toString()).append("\n");
			}
		}
	}

	private Set<Map.Entry<String, List<String>>> getSortedHeaders(final Set<Map.Entry<String, List<String>>> headers) {
		final TreeSet<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<Map.Entry<String, List<String>>>(
				COMPARATOR);
		sortedHeaders.addAll(headers);
		return sortedHeaders;
	}

	private InputStream logInboundEntity(final StringBuilder b, InputStream stream, final Charset charset)
			throws IOException {
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream);
		}
		stream.mark(maxEntitySize + 1);
		final byte[] entity = new byte[maxEntitySize + 1];
		final int entitySize = stream.read(entity);
		b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
		if (entitySize > maxEntitySize) {
			b.append("...more...");
		}
		b.append('\n');
		stream.reset();
		return stream;
	}

	@Override
	public void filter(final ClientRequestContext context) throws IOException {
		final long id = _id.incrementAndGet();
		context.setProperty(LOGGING_ID_PROPERTY, id);

		final StringBuilder b = new StringBuilder();

		printRequestLine(b, "Sending client request", id, context.getMethod(), context.getUri());
		printPrefixedHeaders(b, id, REQUEST_PREFIX, context.getStringHeaders());

		if (printEntity && context.hasEntity()) {
			final OutputStream stream = new LoggingStream(b, context.getEntityStream());
			context.setEntityStream(stream);
			context.setProperty(ENTITY_LOGGER_PROPERTY, stream);
			// not calling log(b) here - it will be called by the interceptor
		} else {
			log(b);
		}
	}

	@Override
	public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext)
			throws IOException {
		final Object requestId = requestContext.getProperty(LOGGING_ID_PROPERTY);
		final long id = requestId != null ? (Long) requestId : _id.incrementAndGet();

		final StringBuilder b = new StringBuilder();

		printResponseLine(b, "Client response received", id, responseContext.getStatus());
		printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getHeaders());

		if (printEntity && responseContext.hasEntity()) {
			responseContext.setEntityStream(
					logInboundEntity(b, responseContext.getEntityStream(), getCharset(responseContext.getMediaType())));
		}

		log(b);
	}

	@Override
	public void filter(final ContainerRequestContext context) throws IOException {
		final long id = _id.incrementAndGet();
		context.setProperty(LOGGING_ID_PROPERTY, id);

		final StringBuilder b = new StringBuilder();

		printRequestLine(b, "Server has received a request", id, context.getMethod(),
				context.getUriInfo().getRequestUri());
		printPrefixedHeaders(b, id, REQUEST_PREFIX, context.getHeaders());

		if (printEntity && context.hasEntity()) {
			context.setEntityStream(logInboundEntity(b, context.getEntityStream(), getCharset(context.getMediaType())));
		}

		log(b);
	}

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		final Object requestId = requestContext.getProperty(LOGGING_ID_PROPERTY);
		final long id = requestId != null ? (Long) requestId : _id.incrementAndGet();

		final StringBuilder b = new StringBuilder();

		printResponseLine(b, "Server responded with a response", id, responseContext.getStatus());
		printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getStringHeaders());

		if (printEntity && responseContext.hasEntity()) {
			final OutputStream stream = new LoggingStream(b, responseContext.getEntityStream());
			responseContext.setEntityStream(stream);
			requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
			// not calling log(b) here - it will be called by the interceptor
		} else {
			log(b);
		}
	}

	@Override
	public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext)
			throws IOException, WebApplicationException {
		final LoggingStream stream = (LoggingStream) writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
		writerInterceptorContext.proceed();
		if (stream != null) {
			log(stream.getStringBuilder(getCharset(writerInterceptorContext.getMediaType())));
		}
	}

	private class LoggingStream extends FilterOutputStream {

		private final StringBuilder b;
		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		LoggingStream(final StringBuilder b, final OutputStream inner) {
			super(inner);

			this.b = b;
		}

		StringBuilder getStringBuilder(final Charset charset) {
			// write entity to the builder
			final byte[] entity = baos.toByteArray();

			b.append(new String(entity, 0, Math.min(entity.length, maxEntitySize), charset));
			if (entity.length > maxEntitySize) {
				b.append("...more...");
			}
			b.append('\n');

			return b;
		}

		@Override
		public void write(final int i) throws IOException {
			if (baos.size() <= maxEntitySize) {
				baos.write(i);
			}
			out.write(i);
		}
	}

	public static Charset getCharset(MediaType m) {
		String name = (m == null) ? null : m.getParameters().get(CHARSET_PARAMETER);
		return (name == null) ? UTF8 : Charset.forName(name);
	}
}

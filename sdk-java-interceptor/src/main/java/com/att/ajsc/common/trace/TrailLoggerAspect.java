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
package com.att.ajsc.common.trace;

import java.lang.reflect.Method;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import com.att.ajsc.common.InterceptorMessages;
import com.att.ajsc.common.Tracable;
import com.att.ajsc.common.TransactionTrail;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TrailLoggerAspect {

	private static EELFLogger logger = AjscEelfManager.getInstance().getLogger(TrailLoggerAspect.class);
	private static final String TRANSACTION_TRAIL = "transactionTrail";
	private static final String MILLIS = " millis for ";
	private static final String LINE = "line";

	@Autowired
	private ApplicationContext context;

	@Pointcut("@annotation(com.att.ajsc.common.Tracable)")
	private void pointcut() {

	}

	@Around("pointcut()")
	public Object logTrail(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTimeInMilliseconds = System.currentTimeMillis();
		long endTimeInMilliseconds;
		long durationInMilliseconds;
		String message = null;
		String finalMessage = "";
		String executionDepth = "-";
		String identifier = "";
		String placeholder = "";
		TransactionTrail transactionTrail;
		try {
			transactionTrail = (TransactionTrail) context.getBean(TRANSACTION_TRAIL);
		} catch (Exception e) {
			logger.warn(InterceptorMessages.INTERCEPTOR_INVALID_TRAILLOGGER, e, ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
			return joinPoint.proceed();
		}

		try {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			identifier = method.getDeclaringClass().toString() + method.getName();
			transactionTrail.addInCompleteMethod(identifier);
			long line = transactionTrail.getTrail().split("\n").length + 1;
			placeholder = LINE + line + ":";
			transactionTrail.setTrail(transactionTrail.getTrail() + "\n" + placeholder);
			Tracable tracable = method.getAnnotation(Tracable.class);
			message = tracable.message();
			if (message.length() == 0) {
				message = signature.toString();
			}

			Object result = joinPoint.proceed();
			endTimeInMilliseconds = System.currentTimeMillis();
			int inCompleteMethods = incompleteMethods(transactionTrail.getInCompleteMethods());
			if (inCompleteMethods > 0) {
				for (int i = 0; i < inCompleteMethods; i++) {
					executionDepth = executionDepth + "-";
				}
			}
			durationInMilliseconds = endTimeInMilliseconds - startTimeInMilliseconds;
			finalMessage = executionDepth + durationInMilliseconds + MILLIS + message;
			transactionTrail.setTrail(transactionTrail.getTrail().replace(placeholder, finalMessage));
			transactionTrail.getInCompleteMethods().remove(identifier);
			return result;
		} catch (Throwable e) {
			logger.error(InterceptorMessages.INTERCEPTOR_TRAIL_LOGGER_MESSAGE, e, joinPoint.getSignature().toString());
			endTimeInMilliseconds = System.currentTimeMillis();
			int inCompleteMethods = incompleteMethods(transactionTrail.getInCompleteMethods());
			if (inCompleteMethods > 0) {
				for (int i = 0; i < inCompleteMethods; i++) {
					executionDepth = executionDepth + "-";
				}
			}
			durationInMilliseconds = endTimeInMilliseconds - startTimeInMilliseconds;
			finalMessage = executionDepth + durationInMilliseconds + MILLIS + message;
			transactionTrail.setTrail(transactionTrail.getTrail().replace(placeholder, finalMessage));
			transactionTrail.getInCompleteMethods().remove(identifier);
			throw e;
		}
	}

	private int incompleteMethods(Set<String> methods) {
		int inCompleteMethods = 0;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			StackTraceElement stackTraceElement = elements[i];
			if (methods.contains("class " + stackTraceElement.getClassName() + stackTraceElement.getMethodName())) {
				inCompleteMethods++;
			}

		}
		return inCompleteMethods;
	}
}

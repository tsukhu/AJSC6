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
package com.att.ajsc.common.camel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;

import com.att.ajsc.common.AjscProvider;
import com.att.ajsc.common.AjscService;

@Configuration
public class BeanCreator implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		Set<String> providerNames = new HashSet<String>();
		Set<String> servicesNames = new HashSet<String>();

		Reflections reflections = new Reflections();
		Set<Class<?>> providerClasses = reflections.getTypesAnnotatedWith(AjscProvider.class);
		for (Class<?> annotatedClass : providerClasses) {
			AjscProvider provider = annotatedClass.getAnnotation(AjscProvider.class);
			if (!providerNames.contains(provider.name())) {
				providerNames.add(provider.name());
			}

		}

		Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(AjscService.class);
		for (Class<?> annotatedClass : serviceClasses) {
			AjscService provider = annotatedClass.getAnnotation(AjscService.class);
			if (!servicesNames.contains(provider.name())) {
				servicesNames.add(provider.name());
			}

		}

		for (Iterator<String> iterator = servicesNames.iterator(); iterator.hasNext();) {
			String serviceName = (String) iterator.next();
			RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
					.rootBeanDefinition(ArrayList.class).getBeanDefinition();
			registry.registerBeanDefinition(serviceName, beanDefinition);

		}

		for (Iterator<String> iterator = providerNames.iterator(); iterator.hasNext();) {
			String providerName = (String) iterator.next();
			RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
					.rootBeanDefinition(ArrayList.class).getBeanDefinition();
			registry.registerBeanDefinition(providerName, beanDefinition);

		}

	}

}

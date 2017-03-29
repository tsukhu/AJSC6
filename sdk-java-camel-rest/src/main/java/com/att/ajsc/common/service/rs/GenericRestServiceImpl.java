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
package com.att.ajsc.common.service.rs;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.dom4j.tree.AbstractEntity;

import com.att.ajsc.common.dto.EnterpriseWrapper;
import com.att.ajsc.common.dto.OrderBy;
import com.att.ajsc.common.dto.PagingInfo;
import com.att.ajsc.common.dto.PagingParameters;
import com.att.ajsc.common.dto.SortAttribute;
import com.att.ajsc.common.dto.WhereClause;
import com.att.ajsc.common.exception.ServerErrorException;
import com.att.ajsc.common.service.GenericService;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericRestServiceImpl<T> implements GenericRestService<T> {
	private static Set<String> entityFieldNames = new HashSet<String>();

	protected final GenericService<T> service;

	public GenericRestServiceImpl(GenericService<T> service) {
		this.service = service;
	}

	@PostConstruct
	private void initialize() {
		entityFieldNames = getEntityFields();
	}

	@Override
	public T getById(String transactionId, String id) {
		T entity = null;
		try {
			entity = service.getEntityById(id);
		} catch (Exception e) {
			throw new ServerErrorException(e.getMessage()).getRestException();
		}

		return entity;
	}

	private List<WhereClause> getWhereClauseFromUri(UriInfo uri, List<WhereClause> whereClause)
			throws BadRequestException {
		if (uri == null)
			return whereClause;

		MultivaluedMap<String, String> queryParameters = uri.getQueryParameters();

		for (String queryParameter : queryParameters.keySet()) {
			List<String> queryArguments = queryParameters.get(queryParameter);
			if (queryArguments.size() > 1) {
				throw new BadRequestException("Only one argument per entity parameter allowed");
			} else if (!queryArguments.isEmpty() && entityFieldNames.contains(queryParameter)) {
				addWhere(whereClause, queryParameter, "=", queryArguments.get(0));
			}
		}

		return whereClause;
	}

	private List<WhereClause> getWhereClause(UriInfo uri, List<WhereClause> whereClause, String filter)
			throws BadRequestException {
		if (whereClause == null)
			whereClause = new ArrayList<WhereClause>();

		getWhereClauseFromUri(uri, whereClause);
		getWhereClauseFromFilter(filter);

		return whereClause;
	}

	@Override
	public Response getWithFilters(String transactionId, List<String> ids, List<String> select, List<WhereClause> where,
			String filter, UriInfo allUri, List<OrderBy> orderBy, Integer limit, Integer offset) {
		Response response = null;
		try {
			where = getWhereClause(allUri, where, filter);
			PagingParameters pagingParameters = getPagingParameters(orderBy, limit, offset);

			EnterpriseWrapper<T> wrapper = service.getEntitiesWithFilters(select, where, pagingParameters);

			List<T> entities = wrapper.getEntities();
			PagingInfo pagingInfo = wrapper.getPagingInfo();
			response = Response.status(Status.OK).entity(entities)
					.header("X-ATT-Total-Count", pagingInfo.getRecordSetTotal()).build();
		} catch (Exception e) {
			throw new ServerErrorException(e.getMessage()).getRestException();
		}

		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<String> getEntityFields() {
		Set<String> entityFields = new HashSet<String>();
		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		Class<T> entityClass = (Class) type.getActualTypeArguments()[0];

		for (Field field : entityClass.getDeclaredFields()) {
			entityFields.add(field.getName());
		}

		for (Field field : AbstractEntity.class.getDeclaredFields()) {
			entityFields.add(field.getName());
		}

		return entityFields;
	}

	@SuppressWarnings("unchecked")
	private List<WhereClause> getWhereClauseFromFilter(String filter) throws BadRequestException {
		List<WhereClause> whereClauses = new ArrayList<WhereClause>();
		if (filter == null || filter.isEmpty())
			return whereClauses;

		final ObjectMapper mapper = new ObjectMapper().configure(Feature.ALLOW_SINGLE_QUOTES, true);

		try {
			final JsonNode jsonNode = mapper.readTree(filter);
			final Map<String, Object> result = (Map<String, Object>) mapper.convertValue(jsonNode, Map.class);

			for (String filterKey : result.keySet()) {
				String fieldName = filterKey.toLowerCase();
				if (!entityFieldNames.contains(fieldName)) {
					throw new Exception(fieldName + " is not a valid field name for this entity");
				}
				Object value = result.get(filterKey);
				if (value instanceof Map) {
					Map<String, Object> operatorMap = (Map<String, Object>) value;
					for (String operatorMapKey : operatorMap.keySet()) {
						String operator = operatorMapKey.toLowerCase();
						Object operatorValue = operatorMap.get(operatorMapKey);
						if ("range".equalsIgnoreCase(operator)) {
							List<Object> valueList = (List<Object>) operatorValue;
							if (valueList != null && valueList.size() == 2) {
								Double min = (Double) valueList.get(0);
								Double max = (Double) valueList.get(1);
								addWhere(whereClauses, fieldName, ">=", min.toString());
								addWhere(whereClauses, fieldName, "<=", max.toString());
							}
						} else if (operatorValue instanceof Map) {
							throw new Exception("and/or not yet supported");
						} else if (operatorValue instanceof List) {
							List<String> valueList = (List<String>) operatorValue;
							for (String valueFromList : valueList) {
								addWhere(whereClauses, fieldName, operator, valueFromList.toString());
							}
						} else {
							addWhere(whereClauses, fieldName, operator, operatorValue.toString());
						}
					}
				} else {
					addWhere(whereClauses, fieldName, "eq", value.toString());
				}
			}
		} catch (Exception e) {
			String message = "Unable to parse filter query: " + e.getMessage();
			throw new BadRequestException(message);
		}
		return whereClauses;
	}

	private void addWhere(List<WhereClause> whereClauses, String fieldName, String operator, String value) {
		if ("eq".equalsIgnoreCase(operator)) {
			operator = "=";
		} else if ("lt".equalsIgnoreCase(operator)) {
			operator = "<";
		} else if ("gt".equalsIgnoreCase(operator)) {
			operator = ">";
		} else if ("lteq".equalsIgnoreCase(operator)) {
			operator = "<=";
		} else if ("gteq".equalsIgnoreCase(operator)) {
			operator = ">=";
		} else if ("noteq".equalsIgnoreCase(operator)) {
			operator = "!=";
		}
		WhereClause where = new WhereClause(fieldName, operator, value);
		whereClauses.add(where);
	}

	@Override
	public T submit(String transactionId, T entity) {
		try {
			entity = service.saveEntity(entity);
		} catch (Exception e) {
			throw new ServerErrorException(e.getMessage()).getRestException();
		}

		return entity;
	}

	@Override
	public T put(String transactionId, T entity) {
		try {
			entity = service.updateEntity(entity);
		} catch (Exception e) {
			throw new ServerErrorException(e.getMessage()).getRestException();
		}

		return entity;
	}

	@Override
	public T update(String transactionId, T entity) throws Exception {
		return put(transactionId, entity);
	}

	@Override
	public void delete(String transactionId, String id) {
		try {
			service.deleteEntity(id);
		} catch (Exception e) {
			throw new ServerErrorException(e.getMessage()).getRestException();
		}
	}

	private PagingParameters getPagingParameters(List<OrderBy> orderBy, Integer limit, Integer offset) {
		PagingParameters params = new PagingParameters();
		params.setFetchSize(limit);
		params.setFetchStart(offset);
		if (orderBy != null && orderBy.size() > 0) {
			List<SortAttribute> sortAttributes = new ArrayList<SortAttribute>();
			for (OrderBy orderByDTO : orderBy) {
				SortAttribute entry = new SortAttribute();
				entry.setName(orderByDTO.getFieldName());
				entry.setDescending(orderByDTO.isDesc());
				sortAttributes.add(entry);
			}
			params.getSortOrder().addAll(sortAttributes);
		}

		return params;
	}
}

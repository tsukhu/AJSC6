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
package com.att.ajsc.common.service;

import java.util.List;

import com.att.ajsc.common.dao.GenericDAO;
import com.att.ajsc.common.dto.EnterpriseWrapper;
import com.att.ajsc.common.dto.PagingInfo;
import com.att.ajsc.common.dto.PagingParameters;
import com.att.ajsc.common.dto.WhereClause;

public abstract class GenericServiceImpl<T> implements GenericService<T> {	
	protected final GenericDAO<T> dao;
	
	public GenericServiceImpl(GenericDAO<T> dao) {
		this.dao = dao;
	}
	
	@Override
	public T saveEntity(T entity) throws Exception {
		return dao.save(entity);
	}
	
	@Override
	public List<T> batchSave(List<T> entities) throws Exception {
		return dao.batchSave(entities);
	}

	@Override
	public T updateEntity(T entity) throws Exception {
		return dao.update(entity);
	}
	
	@Override
	public List<T> batchUpdate(List<T> entities) throws Exception {
		return dao.batchUpdate(entities);
	}
	
	@Override
	public void deleteEntity(String id) throws Exception {
		dao.delete(id);
	}
	
	@Override
	public void batchDelete(List<String> ids) throws Exception {
		dao.batchDelete(ids);
	}

	@Override
	public T getEntityById(String id) throws Exception {
		if (id == null) return null;
		
		return dao.findById(id);
	}
	
	@Override
	public List<T> getEntitiesByIds(List<String> ids) throws Exception {
		if (ids == null) return null;
		
		return dao.findListByIds(ids);
	}
	
	@Override
	public List<T> getEntitiesByProperty(
			String propertyName, 
			String value, 
			Integer limit,
			Integer offset) throws Exception {
		if (propertyName == null || value == null) return null;
		
		return dao.findByProperty(propertyName, value, limit, offset);
	}

	@Override
	public EnterpriseWrapper<T> getEntitiesWithFilters(
			List<String> selectFilters,
			List<WhereClause> whereClauses,
			PagingParameters pagingParameters) throws Exception {
		EnterpriseWrapper<T> wrapper = dao.findWithFilters(
				selectFilters, whereClauses, pagingParameters);
		int totalCount = getTotalCount(whereClauses);
		
		PagingInfo pagingInfo = wrapper.getPagingInfo();
		pagingInfo.setRecordSetTotal(totalCount);
		int max = pagingInfo.getRecordSetCount() + pagingInfo.getRecordSetStartNumber();
		if (totalCount <= max) {
			pagingInfo.setRecordSetCompleteIndicator(true);
		}
		
		return wrapper;		
	}
	
	@Override
	public Integer getTotalCount(
			List<WhereClause> whereClauses) throws Exception {
		return dao.findTotalCount(whereClauses);
	}
	
	@Override
	public Long generateSeqId(String sequence) {
		return dao.generateSeqId(sequence);
	}
}

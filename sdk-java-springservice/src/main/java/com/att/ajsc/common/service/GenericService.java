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

import com.att.ajsc.common.dto.EnterpriseWrapper;
import com.att.ajsc.common.dto.PagingParameters;
import com.att.ajsc.common.dto.WhereClause;

public interface GenericService<T> {
	
	/**
	 * Call the dao's save operation for a previously unsaved entity. 
	 * All subsequent persist actions of this entity should use 
	 * the {@link #updateEntity(Object)} method.
	 * 
	 * @param entity
	 * 			The entity to persist
	 * @return Object
	 * 			The entity that was just persisted
	 * @throws Exception
	 * 			Thrown when the persist operation fails
	 */
	public T saveEntity(T entity) throws Exception;

	/**
	 * Perform a batchSave of a previously unsaved list of entities.
	 * 
	 * @param entities
	 * 			The entity to persist
	 * @return List&lt;Object&gt;
	 * 			The entity that was just persisted
	 * @throws Exception
	 * 			Thrown when the persist operation fails
	 */
	public List<T> batchSave(List<T> entities) throws Exception;
	
	/**
	 * Call the dao's update operation for any previously saved entity and 
	 * returns it or a copy of it to the sender. A copy of the entity
	 * parameter is returned when the JPA persistence mechanism has not
	 * previously been tracking the updated entity.
	 * 
	 * @param entity
	 * 			The entity to update
	 * @return Object
	 * 			The updated entity instance, may not be the same
	 * @throws Exception
	 * 			Thrown when the update operation fails
	 */
	public T updateEntity(T entity) throws Exception;
	
	/**
	 * Batch persist any previously saved entities and return them or a copy 
	 * of them to the sender. Copies of the entities parameter is returned 
	 * when the JPA persistence mechanism has not previously been 
	 * tracking the updated entities.
	 * 
	 * @param entity
	 * 			The entity to update
	 * @return List&lt;Object&gt;
	 * 			The updated entity instance, may not be the same
	 * @throws Exception
	 * 			Thrown when the update operation fails
	 */
	public List<T> batchUpdate(List<T> entity) throws Exception;
	
	/**
	 * Call the dao's delete operation, which will delete 
	 * a persistent entity.
	 * 
	 * @param id
	 * 			The ID number of the entity that is to be deleted
	 * @throws Exception
	 * 			Thrown when the delete operation fails
	 */
	public void deleteEntity(String id) throws Exception;
	
	/**
	 * Batch delete a list of persistent entities.
	 * 
	 * @param ids
	 * 			The ID numbers of the entities that are to be deleted.
	 * @throws Exception
	 * 			Thrown when the delete operation fails
	 */
	public void batchDelete(List<String> ids) throws Exception;

	/**
	 * Call the dao's findById operation, which will find the 
	 * entity with the matching ID number.
	 * 
	 * @param id
	 * 			The ID number that will be used to retrieve the entity
	 * @return Object
	 * 			The entity that is found from the give id number
	 * @throws Exception
	 * 			Thrown when the findById operation fails
	 */
	public T getEntityById(String id) throws Exception;
	
	/**
	 * Call the dao's findListByIds operation, which will find the 
	 * entities with the matching ID numbers.
	 * 
	 * @param ids
	 * 			The ID numbers that will be used to retrieve the list
	 * 			of entities
	 * @return List&lt;T&gt;
	 * 			The list of entities that are found by the query
	 * @throws Exception
	 * 			Thrown when the findListByIds operation fails
	 */
	public List<T> getEntitiesByIds(List<String> ids) throws Exception;
	
	/**
	 * Call the dao's findByProperty operation, which will find all 
	 * entities with a specific property value.
	 * 
	 * @param propertyName
	 * 			The name of the entity property to query
	 * @param value
	 * 			The property value to match
	 * @param limit 
	 * 			The maximum size of the results list that will be returned. If 
	 * 			this value is left null, then all of the results will be returned.
	 * @param offset 
	 * 			The first result of the results list that will be returned. If 
	 * 			this value is left null, then the first result will be 1.
	 * @return List&lt;T&gt;
	 * 			The list of entities that are found by the query
	 * @throws Exception
	 * 			Thrown when the findByProperty operation fails
	 */
	public List<T> getEntitiesByProperty(
			String propertyName, 
			String value, 
			Integer limit,
			Integer offset) throws Exception;

	/**
	 * Call the dao's findWithFilters operation, which will find all entities 
	 * with a given set of properties to match. Also, the caller has the 
	 * option to nullify any attributes of the entities that they desire. 
	 * The searchRequest parameter contains a list of strings that specify 
	 * which attributes of the entity are desired. This defaults to all attributes 
	 * if the list is null or empty. The searchRequest parameter also contains 
	 * a list of FilterEntries, which act as a mapping to specify the where 
	 * clause of the custom query.
	 * 
	 * @param selectFilters The fields to return instead of the entire object
	 * @param whereClauses The filters to specify which records to return
	 * @param pagingParameters The pagination parameters specifying what chunk
	 * 		of objects to return
	 * @return List&lt;T&gt;
	 * 			The list of entities that are found by the query
	 * @throws Exception
	 * 			Thrown when the findWithFilters operation fails
	 */
	public EnterpriseWrapper<T> getEntitiesWithFilters(
			List<String> selectFilters,
			List<WhereClause> whereClauses,
			PagingParameters pagingParameters) throws Exception;
	
	public Integer getTotalCount(
			List<WhereClause> whereClauses) throws Exception;
	
	public Long generateSeqId(String sequence) throws Exception;
}

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

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.tree.AbstractEntity;

import com.att.ajsc.common.dto.OrderBy;
import com.att.ajsc.common.dto.WhereClause;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Produces({ MediaType.APPLICATION_JSON })
public interface GenericRestService<T> {

	/**
	 * Call the dao's save operation for a previously unsaved Example.
	 * 
	 * @param transactionId
	 *            The unique ID tracing through all service calls for this
	 *            thread
	 * @param entity
	 *            The Entity to persist
	 * @return Entity The Entity that was just persisted
	 * @throws Exception
	 *             Thrown when the persist operation fails
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Submit a new entity", notes = "Returns the new entity with ID", response = AbstractEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid field value supplied"),
			@ApiResponse(code = 503, message = "Could not connect to the datasource") })
	public T submit(@HeaderParam("X-ATT-Transaction-Id") String transactionId, T entity) throws Exception;

	/**
	 * Call the dao's update operation for any previously saved Example and
	 * returns it or a copy of it to the web service example. A copy of the
	 * Example parameter is returned when the JPA persistence mechanism has not
	 * previously been tracking the updated entity.
	 * 
	 * @param transactionId
	 *            The unique ID tracing through all service calls for this
	 *            thread
	 * @param entity
	 *            The Example to update
	 * @return Entity The updated Example instance, may not be the same
	 * @throws Exception
	 *             Thrown when the update operation fails
	 */
	@PUT
	@Path("/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update an existing entity", notes = "Returns the updated entity", response = AbstractEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid field value supplied in entity"),
			@ApiResponse(code = 503, message = "Could not connect to the datasource") })
	public T put(@HeaderParam("X-ATT-Transaction-Id") String transactionId, T entity) throws Exception;

	/**
	 * Call the dao's update operation for any previously saved Example and
	 * returns it or a copy of it to the web service example. A copy of the
	 * Example parameter is returned when the JPA persistence mechanism has not
	 * previously been tracking the updated entity.
	 * 
	 * @param transactionId
	 *            The unique ID tracing through all service calls for this
	 *            thread
	 * @param entity
	 *            The Example to update
	 * @return Entity The updated Example instance, may not be the same
	 * @throws Exception
	 *             Thrown when the update operation fails
	 */
	@POST
	@Path("/update")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update an existing entity", notes = "Returns the updated entity", response = AbstractEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid field value supplied in entity"),
			@ApiResponse(code = 503, message = "Could not connect to the datasource") })
	public T update(@HeaderParam("X-ATT-Transaction-Id") String transactionId, T entity) throws Exception;

	/**
	 * Call the dao's delete operation, which will delete a persistent Example.
	 * 
	 * @param transactionId
	 *            The unique ID tracing through all service calls for this
	 *            thread
	 * @param id
	 *            The ID number of the Example that is to be deleted
	 * @throws Exception
	 *             Thrown when the delete operation fails
	 */
	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete an entity", notes = "Delete the existing entity with the ID provided")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Entity not found"),
			@ApiResponse(code = 503, message = "Could not connect to the datasource") })
	public void delete(@HeaderParam("X-ATT-Transaction-Id") String transactionId,
			@ApiParam(value = "ID of the entity to delete", allowableValues = "range[1,99999999999]", required = true) @PathParam("id") String id)
			throws Exception;

	/**
	 * Call the dao's findById operation, which will find the Example with the
	 * matching ID number.
	 * 
	 * @param transactionId
	 *            The unique ID tracing through all service calls for this
	 *            thread
	 * @param id
	 *            The ID number that will be used to retrieve the Example
	 * @return Example The Example that is found from the give id number
	 * @throws Exception
	 *             Thrown when the findById operation fails
	 */
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get the entity by ID", notes = "Returns the entity with the given ID", response = AbstractEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Entity not found"),
			@ApiResponse(code = 503, message = "Could not connect to the datasource") })
	public T getById(@HeaderParam("X-ATT-Transaction-Id") String transactionId,
			@ApiParam(value = "ID of the entity to fetch", allowableValues = "range[1,99999999999]", required = true) @PathParam("id") String id)
			throws Exception;

	/**
	 * Get a list of Entities with filters. This function provides the ability
	 * to narrow a search by a list of select filters, where clauses, order by
	 * statements and pagination.
	 * 
	 * @param transactionId
	 *            The unique ID tracing through all service calls for this
	 *            thread
	 * @param ids
	 *            The ID numbers that will be used to retrieve the list of
	 *            entities
	 * @param select
	 *            Specifies the fields to return in the main object that is
	 *            being queried. For example, if "name,created" are specified,
	 *            then an object will be returned with only the name and created
	 *            field's values. All other fields in the main object will be
	 *            null.
	 * @param where
	 *            Specifies the where clauses that will be applied to the custom
	 *            select statement. If this field is left null, then ALL records
	 *            in the table will be retrieved. This argument is expected to
	 *            be in the following format: {fieldName}:{operator}:{value}.
	 *            For example, 'priority:&lt;=:3'.
	 * @param filter
	 *            json formatted query string for advanced filtering
	 * @param allUri
	 *            the uri context to pull named filters from such as ?field=1
	 * @param orderBy
	 *            Specifies which order by statements to implement in the
	 *            database query. This argument is expected to be in the
	 *            following format: {fieldName}:{(asc or desc)}. For example,
	 *            'created:desc'.
	 * @param limit
	 *            The last index of the result set. This is the maximum size of
	 *            the results list that will be returned. If this value is left
	 *            null, then all of the results will be returned.
	 * @param offset
	 *            The first index of the results list that will be returned. If
	 *            this value is left null, then the first result will be 0.
	 * @return The list of Entities that are found by the query.
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Get a list of entities", notes = "Returns the entities matching the given parameters", response = AbstractEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid field value supplied"),
			@ApiResponse(code = 404, message = "Entities not found"),
			@ApiResponse(code = 503, message = "Could not connect to the datasource") })
	public Response getWithFilters(@HeaderParam("X-ATT-Transaction-Id") String transactionId,
			@ApiParam(value = "IDs of the entities to fetch", allowableValues = "range[1,99999999999]", required = false) @QueryParam("id") List<String> ids,
			@ApiParam(value = "Fields of the entities to return. All other fields are left out. All fields returned when no fields are specified.", required = false) @QueryParam("select") List<String> select,
			@ApiParam(value = "Field values of the entities that must match. All entities returned when no field values are specified.", required = false) @QueryParam("where") List<WhereClause> where,
			@ApiParam(value = "Field values of the entities that must match. All entities returned when no field values are specified.", required = false) @QueryParam("filter") String filter,
			@Context UriInfo allUri,
			@ApiParam(value = "Fields of the entities to order the list by. All entities returned unordered when no fields are specified.", required = false) @QueryParam("orderBy") List<OrderBy> orderBy,
			@ApiParam(value = "Number of entities in the matching list to return. All entities returned when no limit is specified.", required = false) @QueryParam("limit") Integer limit,
			@ApiParam(value = "Entity to start from of entities in the matching list to return. All entities returned when no offset is specified.", required = false) @QueryParam("offset") Integer offset)
			throws BadRequestException;

}
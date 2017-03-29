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
package com.att.ajsc.common.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@MappedSuperclass
@Cacheable(value = false)
@ApiModel(value = "AbstractEntity", description = "The base of all entities")
public abstract class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED", nullable = false, length = 11)
	protected Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED", nullable = false, length = 11)
	protected Date lastModified;
	
	@PrePersist
    private void onInsert() {
        this.created = new Date();
        this.lastModified = this.created;
    }

    @PreUpdate
    private void onUpdate() {
        this.lastModified = new Date();
    }

    /**
	 * The date that the entity was created.
	 * 
	 * @return the created date
	 */
	@ApiModelProperty(
			value = "Timestamp of the creation of this entity")
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * The date that the entity was last modified.
	 * 
	 * @return the last modified date
	 */
	@ApiModelProperty(
			value = "Timestamp of the last modification of this entity")
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}

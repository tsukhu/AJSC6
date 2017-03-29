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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "Example_v2")
@XmlRootElement
@ApiModel(value = "ExampleEntity", description = "An example of an entity")
public class ExampleEntity extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="system-uuid2")
	@GenericGenerator(name="system-uuid2", strategy = "uuid2")
	@Column(name = "ID", unique = true, nullable = false)
	private String id;
	
	@Column(name = "EXAMPLE", nullable = false, length = 50)
	private String example;
	
    public ExampleEntity() { }

	public ExampleEntity(
			String	example) {
		this.example = example;
	}

	@ApiModelProperty(value = "The Entity's ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@ApiModelProperty(
			value = "The Entity's example field",
			allowableValues = "example1,example1b,example1c,example1d")
	public String getExample() {
		return example;
	}
	
	public void setExample(String example) {
		this.example = example;
	}
	
	@Override
	public String toString() {
		return "{"
				+ "id = " 		      + id 
				+ ", example = " 	  + example
				+ ", created = " 	  + created 
				+ ", lastModified = " + lastModified 
				+ "}";
	}
}

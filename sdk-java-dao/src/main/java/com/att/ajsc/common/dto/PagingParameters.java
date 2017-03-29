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
package com.att.ajsc.common.dto;

import java.util.ArrayList;
import java.util.List;

public class PagingParameters {

    protected Integer fetchSize;
    protected Integer fetchStart;
    protected List<SortAttribute> sortOrder;

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer value) {
        this.fetchSize = value;
    }

    public Integer getFetchStart() {
        return fetchStart;
    }

    public void setFetchStart(Integer value) {
        this.fetchStart = value;
    }

    /**
     * Gets the value of the sortOrder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a &lt;CODE&gt;set&lt;/CODE&gt; method for the sortOrder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSortOrder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SortAttribute }
     * 
     * @return List&lt;SortAttribute&gt; the sorting attributes
     */
    public List<SortAttribute> getSortOrder() {
        if (sortOrder == null) {
            sortOrder = new ArrayList<SortAttribute>();
        }
        return this.sortOrder;
    }

}

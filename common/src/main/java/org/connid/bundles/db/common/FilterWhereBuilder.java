/**
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2011-2013 Tirasa. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License"). You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at https://oss.oracle.com/licenses/CDDL
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at https://oss.oracle.com/licenses/CDDL.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.connid.bundles.db.common;


import java.util.ArrayList;
import java.util.List;

import org.identityconnectors.common.CollectionUtil;

/**
 * The Filter Where builder is component intended to be used within subclass of 
 * <code>AbstractFilterTranslator</code> to help create the database WHERE query clause.
 * <p>The main functionality of this helper class is create SQL WHERE query clause</p>
 * <p>The builder can return a List of params to be used within preparedStatement creation<p>  
 * 
 * @version $Revision 1.0$
 * @since 1.0
 */
public class FilterWhereBuilder {

    private boolean in;
    private List<SQLParam> params = new ArrayList<SQLParam>();
    private StringBuilder where = new StringBuilder();
  

    /**
     * Compound join operator
     * 
     * @param operator AND/OR
     * @param l left <CODE>FilterQueryBuiler</CODE>
     * @param r right <CODE>FilterQueryBuiler</CODE>
     */
    public void join(final String operator, 
            final FilterWhereBuilder l, final FilterWhereBuilder r) {
        this.in = true;
        if (l.isIn())
            where.append("( ");
        where.append(l.getWhere());
        if (l.isIn())
            where.append(" )");
        where.append(" ");
        where.append(operator);
        where.append(" ");
        if (r.isIn())
            where.append("( ");
        where.append(r.getWhere());
        if (r.isIn())
            where.append(" )");

        // The params
        params.addAll(l.getParams());
        params.addAll(r.getParams());
    }
    
    /**
     * @return the params
     */
    public List<SQLParam> getParams() {
        return CollectionUtil.asReadOnlyList(params);
    }


    /**
     * @return the where
     */
    public StringBuilder getWhere() {
        return where;
    }
    
    /**
     * Add name value pair bindings with operator, this is lazy bindings resolved at {@link #getWhereClause()}
     * The names are quoted using the {@link #columnQuote} value
     * 
     * @see FilterWhereBuilder#getWhereClause()
     * 
     * @param param value to builder
     * @param operator an operator to compare
     * @param index 
     */
    public void addBind(final SQLParam param, final String operator) {
        if (param == null) throw new IllegalArgumentException("null.param.not.suported");
        where.append(param.getQuotedName());
        where.append(" ").append(operator).append(" ?");
        params.add(param);
    }
    
    /**
     * Add null value
     * The names are quoted using the {@link #columnQuote} value
     * 
     * @see FilterWhereBuilder#getWhereClause()
     * 
     * @param name of the column
     * @param operator an operator to compare  
     * @param param value to builder
     * @param index 
     */
    public void addNull(final String name) {
        where.append(name);
        where.append(" IS NULL");
    }    
    
    /**
     * There is a need to put the content into brackets
     * 
     * @return boolean a in
     */
    public boolean isIn() {
        return in;
    }
    
    /**
     * @param columnQuote The required quote type
     * @return the where clause as a String
     */
    public String getWhereClause() {
        return this.getWhere().toString();
    }
}

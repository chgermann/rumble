/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Stefan Irimescu, Can Berker Cikis
 *
 */

package org.rumbledb.runtime.flwor.expression;


import org.rumbledb.exceptions.ExceptionMetadata;
import org.rumbledb.runtime.RuntimeIterator;
import org.rumbledb.runtime.primary.VariableReferenceIterator;

import java.io.Serializable;

public class GroupByClauseSparkIteratorExpression implements Serializable {


    private static final long serialVersionUID = 1L;
    private final VariableReferenceIterator variableReference;
    private final RuntimeIterator expression;
    private final ExceptionMetadata iteratorMetadata;

    public GroupByClauseSparkIteratorExpression(
            RuntimeIterator expression,
            VariableReferenceIterator variable,
            ExceptionMetadata iteratorMetadata
    ) {
        this.expression = expression;
        this.variableReference = variable;
        this.iteratorMetadata = iteratorMetadata;
    }

    public VariableReferenceIterator getVariableReference() {
        return this.variableReference;
    }

    public ExceptionMetadata getIteratorMetadata() {
        return this.iteratorMetadata;
    }

    public RuntimeIterator getExpression() {
        return this.expression;
    }
}
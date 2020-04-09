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

import org.rumbledb.api.Item;
import org.rumbledb.context.DynamicContext;
import org.rumbledb.exceptions.ExceptionMetadata;
import org.rumbledb.exceptions.IteratorFlowException;
import org.rumbledb.exceptions.OurBadException;
import org.rumbledb.runtime.LocalRuntimeIterator;
import org.rumbledb.runtime.RuntimeIterator;
import sparksoniq.jsoniq.ExecutionMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class SimpleMapExpressionIterator extends LocalRuntimeIterator {

    private static final long serialVersionUID = 1L;
    private RuntimeIterator iterator;
    private RuntimeIterator map;
    private Item nextResult;
    private DynamicContext mapDynamicContext;
    private Queue<Item> mapValues;


    public SimpleMapExpressionIterator(
            RuntimeIterator sequence,
            RuntimeIterator mapExpression,
            ExecutionMode executionMode,
            ExceptionMetadata iteratorMetadata
    ) {
        super(Arrays.asList(sequence, mapExpression), executionMode, iteratorMetadata);
        this.iterator = sequence;
        this.map = mapExpression;
        this.mapDynamicContext = null;
    }

    @Override
    public Item next() {
        if (this.hasNext) {
            Item result = this.nextResult; // save the result to be returned
            setNextResult(); // calculate and store the next result
            return result;
        }
        throw new IteratorFlowException("Invalid next() call in simple map expression", getMetadata());
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public void close() {
        this.iterator.close();
    }

    @Override
    public void open(DynamicContext context) {
        super.open(context);
        if (this.children.size() < 2) {
            throw new OurBadException("Invalid simple map! Must initialize map before calling next");
        }
        this.mapDynamicContext = new DynamicContext(this.currentDynamicContextForLocalExecution);
        this.mapValues = new LinkedList<>();
        this.iterator.open(this.currentDynamicContextForLocalExecution);
        setNextResult();
    }

    private void setNextResult() {
        this.nextResult = null;

        if (this.mapValues.size() > 0) {
            this.nextResult = this.mapValues.poll();
        } else if (this.iterator.hasNext()) {
            Item item = this.iterator.next();
            List<Item> currentItems = new ArrayList<>();
            this.mapDynamicContext.addVariableValue("$$", currentItems);
            currentItems.add(item);

            List<Item> mapValuesRaw = this.map.materialize(this.mapDynamicContext);
            if (mapValuesRaw.size() == 1) {
                this.nextResult = mapValuesRaw.get(0);
            } else {
                this.mapValues.addAll(mapValuesRaw);
                this.nextResult = this.mapValues.poll();
            }
            this.mapDynamicContext.removeVariable("$$");
        }

        if (this.nextResult == null) {
            this.hasNext = false;
            this.iterator.close();
        } else {
            this.hasNext = true;
        }
    }

    public Map<String, DynamicContext.VariableDependency> getVariableDependencies() {
        Map<String, DynamicContext.VariableDependency> result =
            new TreeMap<String, DynamicContext.VariableDependency>();
        result.putAll(this.map.getVariableDependencies());
        result.remove("$");
        result.putAll(this.iterator.getVariableDependencies());
        return result;
    }
}

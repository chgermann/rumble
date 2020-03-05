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

package org.rumbledb.runtime.postfix;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.rumbledb.api.Item;
import org.rumbledb.items.ArrayItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayLookupClosure implements FlatMapFunction<Item, Item> {

    private static final long serialVersionUID = 1L;
    private final Integer lookup;

    public ArrayLookupClosure(Integer lookup) {
        this.lookup = lookup;
    }

    public Iterator<Item> call(Item arg0) throws Exception {
        List<Item> results = new ArrayList<Item>();

        if (!(arg0 instanceof ArrayItem)) {
            return results.iterator();
        }

        if (this.lookup <= 0 || this.lookup > arg0.getSize()) {
            return results.iterator();
        }

        Item item = arg0.getItemAt(this.lookup - 1);
        if (item != null) {
            results.add(item);
        }
        return results.iterator();
    }
};

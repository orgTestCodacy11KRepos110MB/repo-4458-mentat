/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * Copyright 2018 Mozilla
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. */

package org.mozilla.mentat;

/**
 * Wraps a `Rel` result from a Mentat query.
 * A `Rel` result is a list of rows of `TypedValues`.
 * Individual rows can be fetched or the set can be iterated.
 * </p>
 * To fetch individual rows from a `RelResult` use `row(Int32)`.
 * </p>
 * <pre>
 * mentat.query(query).run(new RelResultHandler() {
 *      @Override
 *      public void handleRows(RelResult rows) {
 *          TupleResult row1 = rows.rowAtIndex(0);
 *          TupleResult row2 = rows.rowAtIndex(1);
 *          ...
 *      }
 * });
 *</pre>
 * </p>
 * To iterate over the result set use standard iteration flows.
 * <pre>
 * mentat.query(query).run(new RelResultHandler() {
 *      @Override
 *      public void handleRows(RelResult rows) {
 *          for (TupleResult row: rows) {
 *                ...
 *          }
 *      }
 * });
 *</pre>
 * </p>
 * Note that iteration is consuming and can only be done once.
 */
public class RelResult extends RustObject<JNA.RelResult> implements Iterable<TupleResult> {

    public RelResult(JNA.RelResult pointer) {
        super(pointer);
    }

    /**
     * Fetch the row at the requested index.
     * TODO: Throw an exception if the result set has already been iterated.
     * @param index the index of the row to be fetched
     * @return  The row at the requested index as a `TupleResult`, if present, or nil if there is no row at that index.
     */
    public TupleResult rowAtIndex(int index) {
        this.assertValidPointer();
        JNA.TypedValueList pointer = JNA.INSTANCE.row_at_index(this.validPointer(), index);
        if (pointer == null) {
            return null;
        }

        return new TupleResult(pointer);
    }

    @Override
    public RelResultIterator iterator() {
        JNA.RelResultIter iterPointer = JNA.INSTANCE.typed_value_result_set_into_iter(this.consumePointer());
        return new RelResultIterator(iterPointer);
    }

    @Override
    protected void destroyPointer(JNA.RelResult p) {
        JNA.INSTANCE.typed_value_result_set_destroy(p);
    }
}

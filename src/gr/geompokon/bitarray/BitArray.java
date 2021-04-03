/*
 Copyright 2021 George Bouroutzoglou

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package gr.geompokon.bitarray;

import java.util.AbstractList;
import java.util.RandomAccess;

public class BitArray extends AbstractList<Boolean> implements RandomAccess {

    private static final int DEFAULT_CAPACITY = 10;
    private final BitArrayImpl array;

    public BitArray() {
        this(DEFAULT_CAPACITY);
    }

    public BitArray(int initialSize) {
        array = new BitArrayImpl(initialSize);
    }

    @Override
    public void add(int index, Boolean bit) {
        array.add(index, bit);
    }

    @Override
    public Boolean get(int index) {
        return array.get(index);
    }

    @Override
    public Boolean set(int index, Boolean bit) {
        return array.set(index, bit);
    }

    @Override
    public Boolean remove(int index) {
        return array.remove(index);
    }

    @Override
    public int size() {
        return array.size();
    }

    @Override
    public void clear() {
        array.clear();
    }
}

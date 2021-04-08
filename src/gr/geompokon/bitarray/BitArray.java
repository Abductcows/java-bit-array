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
import java.util.Objects;
import java.util.RandomAccess;

/**
 * Class that models an array of {@code Booleans} with the {@link java.util.List} interface. The aim of this array is to
 * minimize memory required for storage of the {@code Boolean} elements.
 *
 * <p>
 * The Boolean elements of this array are stored as bits inside of {@code long} primitives. Therefore until queried,
 * each element takes no more than one bit in memory. The aim here is scalability. Memory conservation scales linearly
 * with the number of elements. This could be crucial in some specialised applications where Java was selected for some
 * reason.
 * </p>
 * <p>
 * Note that methods that explicitly return a {@code Collection} or {@code Stream} of the elements will NOT follow the
 * one bit per entry principle. In general you are mostly meant to process elements individually or sequentially in
 * operations. That said, every {@link java.util.AbstractList} operation is supported and works with
 * {@link java.util.ArrayList<Boolean>} in mind.
 * </p>
 *
 * @author George Bouroutzoglou (geompokon@csd.auth.gr)
 */
public class BitArray extends AbstractList<Boolean> implements RandomAccess {

    /**
     * Default capacity of the array for the empty constructor.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * The actual array object. Operations are delegated here.
     */
    private final BitArrayImpl array;

    /**
     * Default constructor. Allocates enough memory for at least {@link #DEFAULT_CAPACITY} elements before resizing.
     */
    public BitArray() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initialises the array to store at least {@code initialSize} elements before resizing.
     *
     * @param initialSize initial element capacity of the array
     */
    public BitArray(int initialSize) {
        array = new BitArrayImpl(initialSize);
    }

    /**
     * Inserts the {@code bit} element at the argument index.
     *
     * @param index index of the insertion
     * @param bit   the element to be inserted
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
    public void add(int index, Boolean bit) {
        Objects.requireNonNull(bit);
        modCount++;
        array.add(index, bit);
    }

    /**
     * Returns the value of the element at the specified index.
     *
     * @param index index of the element to be retrieved
     * @return array element at the index
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
    public Boolean get(int index) {
        return array.get(index);
    }

    /**
     * Sets the element at the specified index to the new value.
     *
     * @param index index of the element to be replaced
     * @param bit   new value of the element
     * @return previous value of the element
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
    public Boolean set(int index, Boolean bit) {
        Objects.requireNonNull(bit);
        modCount++;
        return array.set(index, bit);
    }

    /**
     * Removes and returns the element at the argument index.
     *
     * @param index index of the element to be removed
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
    public Boolean remove(int index) {
        modCount++;
        return array.remove(index);
    }

    /**
     * Returns current number of elements in the array.
     *
     * @return current number of elements in the array
     */
    @Override
    public int size() {
        return array.size();
    }

    /**
     * Removes all elements from the array. The array is empty after a call to this method.
     */
    @Override
    public void clear() {
        modCount++;
        array.clear();
    }
}

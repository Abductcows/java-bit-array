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

import java.util.Arrays;

public class BitArrayImpl {
    private static final int DEFAULT_CAPACITY = ImplBitUtilities.BITS_PER_LONG;

    private final ImplBitUtilities bitUtils = new ImplBitUtilities();
    private long[] data;
    private int elements; // number of elements in the array

    public BitArrayImpl() {
        this(DEFAULT_CAPACITY);
    }

    public BitArrayImpl(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Array initial size is negative: " + initialCapacity);
        }
        initMembers(initialCapacity);
    }

    private void initMembers(int initialCapacity) {
        int sizeInLongs =
                (int) Math.ceil(
                        (double) initialCapacity / ImplBitUtilities.BITS_PER_LONG);
        data = new long[sizeInLongs];
        elements = 0;
    }

    public boolean add(boolean bit) {
        add(elements, bit);
        return true;
    }

    public void add(int index, boolean bit) {
        // check for index out of bounds
        ensureIndexInRange(index, size());
        ensureCapacity();

        // check for append
        if (index == elements) {
            setBit(elements, bit);
            elements = elements + 1;
            return;
        }

        // else insert normally
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);
        addAndShiftAllRight(bit, longIndex, indexInLong);
        elements = elements + 1;
    }

    /**
     * Returns the bit value from the selected index
     *
     * @param index index of the bit in the array
     * @return 0 or 1 corresponding to the bit value
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    public boolean get(int index) {
        ensureIndexInRange(index, size() - 1);
        // get index of the long housing the bit
        int longIndex = getLongIndex(index);
        // get index of the bit inside the long
        int indexInLong = getIndexInLong(index);

        int selectedBit = bitUtils.getBitInLong(data[longIndex], indexInLong);
        return bitUtils.intBoolValue(selectedBit);
    }

    public boolean set(int index, boolean bit) {
        boolean oldBit = get(index);
        setBit(index, bit);
        return oldBit;
    }

    public boolean remove(int index) {
        ensureIndexInRange(index, size() - 1);
        boolean bit = get(index);

        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);
        removeAndShiftAllLeft(longIndex, indexInLong);

        elements = elements - 1;
        return bit;
    }

    public int size() {
        return elements;
    }

    public boolean isEmpty() {
        return elements == 0;
    }

    public void clear() {
        initMembers(DEFAULT_CAPACITY);
    }


    private int getLongIndex(int bitIndex) {
        return bitIndex / ImplBitUtilities.BITS_PER_LONG;
    }

    private int getIndexInLong(int bitIndex) {
        return bitIndex % ImplBitUtilities.BITS_PER_LONG;
    }

    private void setBit(int index, boolean bit) {
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        if (bit) {
            data[longIndex] = bitUtils.longWithSetBit(data[longIndex], indexInLong);
        } else {
            data[longIndex] = bitUtils.longWithClearedBit(data[longIndex], indexInLong);
        }
    }

    private void addAndShiftAllRight(boolean bit, int longIndex, int indexInLong) {
        // start at current long index and work all the way to the last long
        int maxLongIndex = getLongIndex(elements);
        // add the bit and save the LSB that was shifted out
        int bitIntValue = bitUtils.boolIntValue(bit);
        int rightmostBit = insertInLongShiftRight(bitIntValue, longIndex++, indexInLong);
        // keep inserting old LSB at 0 of next long and moving on with the new LSB
        while (longIndex <= maxLongIndex) {
            rightmostBit = insertInLongShiftRight(rightmostBit, longIndex++, 0);
        }
    }

    /**
     * Inserts the bit in the index of the long specified by the arguments and then shifts
     * everything to its right to the right. The LSB that is shifted out is returned.
     *
     * @param bit         the bit to be inserted
     * @param longIndex   index of the long in the data array
     * @param indexInLong index of the bit in the long
     * @return LSB of the long before insertion
     */
    private int insertInLongShiftRight(int bit, int longIndex, int indexInLong) {
        long[] splitLong = bitUtils.splitLong(data[longIndex], indexInLong);
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = splitLong[0];
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = splitLong[1];

        // save LSB
        int rightSideLSB = (int) rightSide & 1;
        // unsigned shift to the right to make space for the new bit
        rightSide >>>= 1;
        // new bit is 0 from the shift, change it to 1 if required
        if (bit == 1) {
            rightSide = bitUtils.longWithSetBit(rightSide, indexInLong);
        }
        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the LSB
        return rightSideLSB;
    }

    private void removeAndShiftAllLeft(int longIndex, int indexInLong) {
        // start at the end and work back to current long index
        int currentLongIndex = getLongIndex(elements - 1);
        int leftmostBit = 0; // dud value for first shift
        // keep adding the old MSB as LSB of the previous long index and shifting the rest to the left
        while (currentLongIndex > longIndex) {
            leftmostBit = appendLongShiftLeft(leftmostBit, currentLongIndex--, 0);
        }
        // add the final MSB as LSB of {@code longIndex} and shift only the bits to the removed's right
        appendLongShiftLeft(leftmostBit, longIndex, indexInLong);
    }

    private int appendLongShiftLeft(int bit, int longIndex, int indexInLong) {
        long[] splitLong = bitUtils.splitLong(data[longIndex], indexInLong);
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = splitLong[0];
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = splitLong[1];

        // save MSB
        int rightSideMSB = bitUtils.getBitInLong(rightSide, indexInLong);
        // clear MSB and shift to the left to make it disappear
        rightSide = bitUtils.longWithClearedBit(rightSide, indexInLong);
        rightSide <<= 1;
        // append the previous bit
        rightSide += bit;

        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the MSB
        return rightSideMSB == 0 ? rightSideMSB : 1;
    }

    private void ensureIndexInRange(int index, int endInclusive) {
        if (index < 0 || index > endInclusive) {
            throw new IndexOutOfBoundsException("Array index " + index + " out of bounds for array size " + this.size());
        }
    }

    private void ensureCapacity() {
        if (elements == data.length * ImplBitUtilities.BITS_PER_LONG) {
            doubleSize();
        }
    }

    /**
     * Doubles the size of the array by calling resize with argument 2 times
     * the current amount of elements in the array
     */
    private void doubleSize() {
        resize(2 * elements);
    }

    private void resize(int newSize) {
        // In case the initial capacity of the array is 0, simply call
        // {@link #initMembers} and declare the new capacity a one.
        if (newSize == 0) {
            this.initMembers(1);
            return;
        }
        // make sure to create enough longs for new size
        int newSizeInLongs = (int) Math.ceil(
                (double) newSize / ImplBitUtilities.BITS_PER_LONG);

        // copy data
        data = Arrays.copyOf(data, newSizeInLongs);
        // if elements were truncated, update element count
        elements = Math.min(elements, newSize);
    }
}

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

/**
 * Class that models an array of boolean values. The aim of this class is to minimise memory allocation.
 *
 * <p>
 * The "magic trick" here is the fact that an n-bit integer can store n bits. We map each bit to its boolean value,
 * namely 0 to false and 1 to true. Therefore we can represent n independent boolean values if we can access and
 * modify them separately.
 * </p>
 * <p>
 * The class uses an array of {@code long} primitives to store the bits to maximise efficiency of bulk operations such as
 * shifts and array resizes. The bit array is by all means an array; elements in the array have contiguous indices.
 * Insertions and removals shift necessary elements to preserve that property. Array accesses are random access because
 * the index of a bit in the data array can be determined by an integer division.
 * </p>
 * <p>
 * A bit more on the accessing part: Because we are grouping bits in groups of 64, we need to modify our array accessing
 * but only internally; clients should be specifying absolute array indices and we need to know which long that index
 * corresponds to. Thanks to papa Euclid we know that there are unique q and r such that {@code i = 64 * q + r}.
 * We use {@code q} to specify the long in the {@link #data} array and {@code r} to specify which bit we are looking for.
 * </p>
 * <p>
 * Normal indices are referred to as "global" or "absolute" when a distinction should be made. Also, bits and booleans
 * are sometimes used interchangeably.
 * </p>
 * <p>
 * This class is package private in order to be used by the public class, {@link BitArray} which extends
 * {@link java.util.AbstractList}.
 * </p>
 *
 * @author George Bouroutzoglou (geompokon@csd.auth.gr)
 */
class BitArrayImpl {

    /**
     * Utility bitwise operation object.
     */
    private static final ImplBitUtilities bitUtils = new ImplBitUtilities(); // utility bitwise operation object

    /**
     * Default array capacity in bit entries.
     */
    private static final int DEFAULT_CAPACITY = ImplBitUtilities.BITS_PER_LONG;

    /**
     * {@code long} array storing the bit entries.
     */
    private long[] data;

    /**
     * Current number of elements in the array.
     */
    private int elements;

    /**
     * Default constructor. Sets initial capacity to {@link #DEFAULT_CAPACITY}.
     */
    BitArrayImpl() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initialises the array to store at least {@code initialCapacity} bits before resizing.
     *
     * <p>
     * Actual memory size of the array in bits is rounded up to the next multiple of 64.
     * The array should not resize before {@code initialCapacity} elements have been inserted.
     * </p>
     *
     * @param initialCapacity initial capacity of the array in bit entries
     */
    BitArrayImpl(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Array initial size is negative: " + initialCapacity);
        }
        initMembers(initialCapacity);
    }

    /**
     * Initialises the array as a freshly created array. {@link #elements} should be 0 after a call to this.
     *
     * @param initialCapacity initial capacity of the array in bit entries
     */
    private void initMembers(int initialCapacity) {
        // allocate enough longs for the number of bits required
        int sizeInLongs =
                (int) Math.ceil(
                        (double) initialCapacity / ImplBitUtilities.BITS_PER_LONG);
        // init the array and set number of elements to 0
        data = new long[sizeInLongs];
        elements = 0;
    }

    /**
     * Inserts the boolean value as a bit at the argument index.
     *
     * @param index global array index of the insertion
     * @param bit   the bit to be inserted
     * @throws IndexOutOfBoundsException if index is out of array insertion bounds
     */
    void add(int index, boolean bit) {
        // check for index out of bounds
        ensureIndexInRange(index, elements);
        ensureCapacity();

        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // check for append
        if (index == elements) {
            setBit(longIndex, indexInLong, bit);
            elements = elements + 1;
            return;
        }

        // else insert normally
        addAndShiftAllRight(bit, longIndex, indexInLong);
        elements = elements + 1;
    }

    /**
     * Returns the boolean value of the bit at the selected array index.
     *
     * @param index global index of the bit in the array
     * @return boolean value of the bit entry
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    boolean get(int index) {
        // check for index out of bounds
        ensureIndexInRange(index, elements - 1);
        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // return the bit
        return getBit(longIndex, indexInLong);
    }

    /**
     * Sets the bit at the specified index to the desired value and returns the old value.
     *
     * @param index global index of the array element to be changed
     * @param bit   the new value of the array element
     * @return boolean value of the previous bit at that index
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    boolean set(int index, boolean bit) {
        // check for index out of bounds
        ensureIndexInRange(0, elements - 1);
        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // save previous entry
        boolean oldBit = getBit(longIndex, indexInLong);
        // set the new bit
        setBit(longIndex, indexInLong, bit);
        // return previous
        return oldBit;
    }

    /**
     * Removes the bit at the specified array index.
     *
     * @param index global index of the element
     * @return boolean value of the removed bit
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    boolean remove(int index) {
        // check for index out of bounds
        ensureIndexInRange(index, elements - 1);

        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // save the bit to be removed
        boolean removedBit = getBit(longIndex, indexInLong);
        // remove it
        removeAndShiftAllLeft(longIndex, indexInLong);

        // update elements and return the removed bit
        elements = elements - 1;
        return removedBit;
    }

    /**
     * Returns the number of elements in the array.
     *
     * @return number of elements in the array
     */
    int size() {
        return elements;
    }

    /**
     * Clears the contents of the array. Previous {@code data} memory should be available for
     * garbage collection after a call to this method.
     */
    void clear() {
        initMembers(DEFAULT_CAPACITY);
    }

    /**
     * Returns the index of the long in the {@code data} array that contains the bit at {@code bitIndex}.
     *
     * @param bitIndex global index of the bit in the array
     * @return index of the long housing the bit
     */
    private int getLongIndex(int bitIndex) {
        return bitIndex / ImplBitUtilities.BITS_PER_LONG;
    }

    /**
     * Used in conjunction with {@link #getLongIndex(int bitIndex)}. Returns the index of the bit in the long's bits.
     *
     * <p>
     * Long refers to the long at the index returned by {@link #getLongIndex(int)}. Indices start counting
     * from 0 to {@link ImplBitUtilities#BITS_PER_LONG} - 1 from left to right.
     * </p>
     *
     * @param bitIndex global index of the bit in the array
     * @return index of the bit in its long
     */
    private int getIndexInLong(int bitIndex) {
        return bitIndex % ImplBitUtilities.BITS_PER_LONG;
    }

    /**
     * Sets the argument bit at the location specified by the long indices.
     *
     * @param longIndex   index of the long in the data array
     * @param indexInLong index of the bit in the long
     * @param bit         new bit to replace the previous entry at that index
     */
    private void setBit(int longIndex, int indexInLong, boolean bit) {
        if (bit) {
            data[longIndex] = bitUtils.longWithSetBit(data[longIndex], indexInLong);
            return;
        }
        data[longIndex] = bitUtils.longWithClearedBit(data[longIndex], indexInLong);
    }

    /**
     * Returns the bit at the location specified by the long indices.
     *
     * @param longIndex   index of the long in the data array
     * @param indexInLong index of the bit in the long
     * @return the bit at the specified location
     */
    private boolean getBit(int longIndex, int indexInLong) {
        // get the bit
        int bit = bitUtils.getBitInLong(data[longIndex], indexInLong);
        // return its bool value
        return bitUtils.intBoolValue(bit);
    }

    /**
     * Adds the bit at the array index and shifts every entry to its right to the right.
     *
     * @param bit         the new bit to be added
     * @param longIndex   index of the long of the insertion index
     * @param indexInLong index of the bit in the long of the insertion
     */
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
     * Inserts the bit in the index of the long specified by the arguments and shifts the previous LSB out.
     *
     * <p>
     * Inserting at any index is done by splitting the long word in two parts and rejoining them after shifting and
     * setting the new bit. The LSB that is shifted out is returned.
     * </p>
     *
     * @param bit         the bit to be inserted
     * @param longIndex   index of the long in the {@code data} array
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

    /**
     * Removes the bit at the array index and shifts everything to its right to the left.
     *
     * @param longIndex   index of the long of the remove index
     * @param indexInLong index of the bit in the long of the removal
     */
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

    /**
     * Appends the bit at the end of the long specified by the arguments and removes the bit at {@code indexInLong}.
     *
     * <p>
     * Since {@code indexInLong} can be at the middle of the long word, removing the bit is done by splitting the
     * long in two parts, clearing the desired bit and shifting once to restore the order of the previous bits.
     * </p>
     *
     * @param bit         the bit to be appended to the long
     * @param longIndex   index of the long in the {@code data} array
     * @param indexInLong index of the bit in the long
     * @return bit at {@code longIndex} that was popped out
     */
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
        return rightSideMSB;
    }

    /**
     * Checks for index out of bounds.
     *
     * @param index        index to be checked
     * @param endInclusive last allowed value of the index
     */
    private void ensureIndexInRange(int index, int endInclusive) {
        if (index < 0 || index > endInclusive) {
            throw new IndexOutOfBoundsException("Array index " + index + " out of bounds for array size " + this.size());
        }
    }

    /**
     * Extends the array size to store at least one more element.
     *
     * <p>
     * Doubling of size preferred.
     * </p>
     */
    private void ensureCapacity() {
        if (elements == data.length * ImplBitUtilities.BITS_PER_LONG) {
            doubleSize();
        }
    }

    /**
     * Doubles the size of the array.
     */
    private void doubleSize() {
        resize(2 * elements);
    }

    /**
     * Resizes the array. Number of elements is updated in case of truncation.
     *
     * @param newSize new size in bit entries
     */
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

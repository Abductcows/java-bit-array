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

import java.util.*;

/**
 * Class that models an array of {@code Booleans} with the {@link java.util.List} interface.
 *
 * <p>
 * The aim of this class is to enhance the performance of common operations such as {@code add}, {@code remove} and
 * {@code set} while also minimizing its memory footprint. This class was made explicitly to replace {@link ArrayList}
 * when working with {@code Boolean} elements.
 * </p>
 *
 * <p>
 * Memory conservation and higher performance stem from the particular case of dealing with {@code Boolean} elements.
 * The array stores each boolean as its bit equivalent (0 for false and 1 for true) inside of an array of long primitives.
 * Therefore shifts of multiple elements and array copying can be done en masse, all while elements occupy less memory
 * when in storage.
 * </p>
 *
 * <p>
 * A glimpse of the future:<br><br>
 * <code>
 * List&lt;Boolean&gt; elements = new ArrayList&lt;&gt;(); // rookie mistake
 * </code><br>
 * changes to:<br>
 * <code>
 * List&lt;Boolean&gt; elements = new BitArray(); // no convoluted diamond operator, superior performance
 * </code>
 * </p>
 *
 * <p>
 * Note that methods that explicitly return a new {@code Collection} of the elements (other than {@code subList}) will
 * not follow the one bit per entry principle.
 * </p>
 *
 * @author George Bouroutzoglou (geompokon@csd.auth.gr)
 * @version 1.0.0
 * @see java.util.List
 * @see java.util.AbstractList
 * @see java.util.ArrayList
 */
public final class BitArray extends AbstractList<Boolean> implements RandomAccess, Cloneable {

    /**
     * Number of bits in a long integer
     */
    private static final int BITS_PER_LONG = 64;

    /**
     * Default array capacity in bit entries. Used in empty constructor
     */
    private static final int DEFAULT_CAPACITY = BITS_PER_LONG;

    /**
     * {@code long} array storing the bit entries.
     */
    private long[] data;

    /**
     * Current number of bit elements in the array.
     */
    private int elements;

    /**
     * Default constructor. Sets initial capacity to {@link #DEFAULT_CAPACITY}.
     */
    public BitArray() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initialises the array to store at least {@code initialCapacity} elements before resizing.
     *
     * <p>
     * Actual memory size of the array in bits is rounded up to the next multiple of 64.
     * </p>
     *
     * @param initialCapacity initial capacity of the array in bit entries
     */
    public BitArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Array initial capacity is negative: " + initialCapacity);
        }
        initMembers(initialCapacity);
    }

    /**
     * Builds the array from the specified collection in the order specified by its iterator
     *
     * <p>
     * Copy of collection without {@code addAll()} for BitArray types
     * </p>
     *
     * @param other the collection supplying the elements
     * @throws NullPointerException if the collection is null
     */
    public BitArray(Collection<? extends Boolean> other) {
        Objects.requireNonNull(other);

        // fast copy for BitArray
        if (other instanceof BitArray) {
            BitArray otherBitArray = (BitArray) other;
            int longsToCopy = longsRequiredForNBits(otherBitArray.elements);

            this.data = Arrays.copyOf(otherBitArray.data, longsToCopy);
            this.elements = otherBitArray.elements;
            return;
        }

        // standard copy
        initMembers(other.size());
        this.addAll(other);
    }

    /**
     * Initialises the array as a freshly created array. {@link #elements} should be 0 after a call to this.
     *
     * @param initialCapacity initial capacity of the array in bit entries
     */
    private void initMembers(int initialCapacity) {
        // allocate enough longs for the number of bits required
        int sizeInLongs = longsRequiredForNBits(initialCapacity);
        // init the array and set number of elements to 0
        data = new long[sizeInLongs];
        elements = 0;
    }

    /**
     * Inserts the boolean value as a bit at the argument index.
     *
     * @param index array index to insert the element in
     * @param bit   the boolean value to be inserted
     * @throws IndexOutOfBoundsException if index is out of array insertion bounds
     */
    public void add(int index, Boolean bit) {
        Objects.requireNonNull(bit);
        ensureIndexInRange(index, elements);
        modCount++;
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
     * @param index index of the element in the array
     * @return boolean value of the bit entry
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    public Boolean get(int index) {
        ensureIndexInRange(index, elements - 1);
        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // return the bit
        return getBit(longIndex, indexInLong);
    }

    /**
     * Sets the boolean value of the element at the specified index to the desired value and returns the old value.
     *
     * @param index index of the array element to be changed
     * @param bit   the new value of the array element
     * @return boolean value of the previous bit at that index
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    public Boolean set(int index, Boolean bit) {
        Objects.requireNonNull(bit);
        ensureIndexInRange(0, elements - 1);
        modCount++;
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
     * @param index index of the element
     * @return boolean value of the removed bit
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    public Boolean remove(int index) {
        ensureIndexInRange(index, elements - 1);
        modCount++;

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
    public int size() {
        return elements;
    }

    /**
     * Clears the contents of the array and releases memory used previously.
     */
    public void clear() {
        modCount++;
        initMembers(DEFAULT_CAPACITY);
    }

    /**
     * Returns the index of the long in the {@code data} array that contains the bit at {@code bitIndex}.
     *
     * @param bitIndex global index of the bit in the array
     * @return index of the long housing the bit
     */
    private int getLongIndex(int bitIndex) {
        return bitIndex / BITS_PER_LONG;
    }

    /**
     * Used in conjunction with {@link #getLongIndex(int bitIndex)}. Returns the index of the bit in the long's bits.
     *
     * <p>
     * Long refers to the long at the index returned by {@link #getLongIndex(int)}. Indices start counting
     * from 0 to {@link #BITS_PER_LONG} - 1 from left to right.
     * </p>
     *
     * @param bitIndex global index of the bit in the array
     * @return index of the bit in its long
     */
    private int getIndexInLong(int bitIndex) {
        return bitIndex % BITS_PER_LONG;
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
            data[longIndex] |= singleBitMask(indexInLong);
            return;
        }
        data[longIndex] &= ~singleBitMask(indexInLong);
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
        int bit = getBitInLong(data[longIndex], indexInLong);
        // return its bool value
        return bit != 0;
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
        int bitIntValue = Boolean.compare(bit, Boolean.FALSE);
        int rightmostBit = insertInLong(bitIntValue, longIndex++, indexInLong);
        // keep inserting old LSB at 0 of next long and moving on with the new LSB
        while (longIndex <= maxLongIndex) {
            rightmostBit = insertInLong(rightmostBit, longIndex++, 0);
        }
    }

    /**
     * Inserts the bit in the index of the long specified by the arguments and returns the previous LSB.
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
    private int insertInLong(int bit, int longIndex, int indexInLong) {
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = (data[longIndex] << indexInLong) >>> indexInLong;
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = data[longIndex] - rightSide;

        // save LSB
        long rightSideLSB = rightSide & 1L;
        // unsigned shift to the right to make space for the new bit
        rightSide >>>= 1;
        // set the new bit
        rightSide |= (long) bit << (BITS_PER_LONG - 1 - indexInLong);
        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the LSB
        return (int) rightSideLSB;
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
            leftmostBit = appendBitAndRemoveAtIndex(leftmostBit, currentLongIndex--, 0);
        }
        // add the final MSB as LSB of {@code longIndex} and shift only the bits to the removed's right
        appendBitAndRemoveAtIndex(leftmostBit, longIndex, indexInLong);
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
    private int appendBitAndRemoveAtIndex(int bit, int longIndex, int indexInLong) {
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = (data[longIndex] << indexInLong) >>> indexInLong;
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = data[longIndex] - rightSide;

        // save MSB
        int rightSideMSB = getBitInLong(rightSide, indexInLong);
        // clear MSB and shift to the left to make it "disappear"
        rightSide &= ~singleBitMask(indexInLong);
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
        if (elements == data.length * BITS_PER_LONG) {
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
        // In case the new size is 0 (for example from calling double on a 0 capacity array)
        // set the new capacity to some default value
        if (newSize == 0) {
            this.initMembers(DEFAULT_CAPACITY);
            return;
        }
        // make sure to create enough longs for new size
        int newSizeInLongs = longsRequiredForNBits(newSize);

        // copy data
        data = Arrays.copyOf(data, newSizeInLongs);
        // if elements were truncated, update element count
        elements = Math.min(elements, newSize);
    }

    /**
     * Returns a long bit mask with only one bit set to 1
     *
     * @param bitIndex index of the bit in the long to be set
     * @return long bit mask with the specific bit set
     */
    private long singleBitMask(int bitIndex) {
        return Long.MIN_VALUE >>> bitIndex;
    }

    /**
     * Returns 0 or 1 based on the value of the specified bit in the long
     *
     * @param theLong  the long storing the bit
     * @param bitIndex index of the bit in the long
     * @return integer value of the bit in the long
     */
    private int getBitInLong(long theLong, int bitIndex) {
        // position the bit at the rightmost part and extract it
        return (int) (theLong >> (BITS_PER_LONG - 1 - bitIndex)) & 1;
    }

    /**
     * Returns the smallest number of longs needed to contain {@code nBits} bits.
     *
     * @param nBits the number of bits
     * @return ceil division of {@code nBits} with {@link #BITS_PER_LONG}
     */
    private int longsRequiredForNBits(int nBits) {
        return (int) Math.ceil(
                (double) nBits / BITS_PER_LONG);
    }


    /*
        BitArray specific methods
    */

    /**
     * Returns a deep copy of this object
     *
     * @return deep copy of {@code this}
     */
    public BitArray clone() {
        return new BitArray(this);
    }

    /**
     * Serializes the array into a string.
     *
     * <p>
     * The string consists of the number of elements in the array and a list of the elements in a human readable
     * format. Exact representation is "Size = SIZE, [((0 | 1) + ' ')*]" where SIZE is a non negative integer and
     * the list of elements consists of opening square brackets ([), zero or more bits (single digit ones or zeros)
     * separated by spaces and closing square brackets.
     * </p>
     * <p>
     * Examples:<br>
     * Size = 7, [1 1 1 0 1 1 1]<br>
     * Size = 4, [0 0 0 0]<br>
     * Size = 0, []
     * </p>
     *
     * @return String representation of the array and its elements
     */
    public String toString() {
        StringBuilder s = new StringBuilder(this.size() * 2);

        // write size of the array
        s.append("Size = ").append(this.size()).append(", ");

        // write the list of bits as 1s and 0s
        s.append('[');
        for (int i = 0; i < this.size() - 1; i++) {
            s.append(Boolean.compare(this.get(i), Boolean.FALSE));
            s.append(' ');
        }
        if (size() > 0) {
            s.append(Boolean.compare(this.get(this.size() - 1), Boolean.FALSE));
        }
        s.append(']');

        return s.toString();
    }

    /**
     * Constructs the BitArray from the serialized String representation given
     *
     * @param stringArray array in String format as returned by {@link #toString()}
     * @return new BitArray instance with the String array's contents
     * @throws UnknownFormatConversionException if string parsing fails
     */
    public static BitArray fromString(String stringArray) {

        String start = "Size = ";

        if (!stringArray.startsWith(start)) {
            throw new UnknownFormatConversionException("Not a valid BitArray string");
        }

        // count number of digits of the array size
        int currentIndex = start.length();
        while (stringArray.charAt(currentIndex) != ',') {
            currentIndex++;
        }

        int arraySize;
        try {
            String arraySizeStr = stringArray.substring(start.length(), currentIndex);
            arraySize = Integer.parseInt(arraySizeStr);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new UnknownFormatConversionException("Not a valid BitArray string");
        }

        // move the cursor to the first element
        currentIndex += ", [".length();

        // read elements
        BitArray result = new BitArray(arraySize);
        for (int i = 0; i < arraySize; i++) {
            if (currentIndex >= stringArray.length() - 1) {
                throw new UnknownFormatConversionException("Not a valid BitArray string");
            }
            result.add(stringArray.charAt(currentIndex) == '1');
            currentIndex += 2;
        }

        return result;
    }
}

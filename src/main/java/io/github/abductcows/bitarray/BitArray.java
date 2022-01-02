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

package io.github.abductcows.bitarray;

import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * <h2>Random access List&lt;Boolean&gt; that uses a {@code long} primitive array to store its elements. Each element
 * occupies a single bit in its corresponding {@code long}</h2>
 *
 * <p>This class is superior to ArrayList in terms of CRUD performance and memory usage. Its only limitation is its
 * inability to store {@code null} values.</p>
 *
 * <p>Read only operations such as {@link #get(int)} and iterator traversals
 * run at about the same time. {@link #add(Boolean) Add} and {@link #remove(int) remove} at the tail also have similar
 * performance. The most significant gains come from element copy and move operations. This includes
 * random index {@link #add(Boolean) add} and {@link #remove(int) remove}, array resizes etc.
 * You can find my benchmarks and their results from my machine
 * <a href=https://github.com/Abductcows/bit-array-benchmarks>here</a></p>
 *
 * <p>This class is NOT synchronized. For a synchronized version use {@link java.util.Collections.SynchronizedList}</p>
 *
 * <p>Note that methods which return a copy of the elements will probably not follow the one bit per entry
 * principle.
 * {@link java.util.AbstractList.SubList SubList} and
 * {@link java.util.Collections.SynchronizedList SynchronizedList}
 * are safe to use of course.</p>
 *
 * @version 1.1.1
 */
@CustomNonNullApi
public final class BitArray extends AbstractList<Boolean> implements RandomAccess, Cloneable {

    /**
     * Number of bits in a long integer
     */
    private static final int BITS_PER_LONG = 64;

    /**
     * Default array capacity in bit entries
     */
    private static final int DEFAULT_CAPACITY = BITS_PER_LONG;

    /**
     * Element storage
     */
    private long[] data = {};

    /**
     * Current number of elements
     */
    private int elements;

    /**
     * Default constructor. Sets initial capacity to {@link #DEFAULT_CAPACITY}
     */
    public BitArray() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initialises the array to store at least {@code initialCapacity} elements before resizing.
     *
     * <p>
     * Actual memory size of the array in bits is rounded up to the next multiple of {@link #BITS_PER_LONG}.
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
     * @param other the collection supplying the elements
     * @throws NullPointerException if the collection is null
     */
    public BitArray(Collection<? extends Boolean> other) {
        // fast copy for BitArray
        if (other instanceof BitArray) {
            BitArray otherBitArray = (BitArray) other;
            int longsToCopy = longsRequiredForNBits(otherBitArray.elements);

            data = Arrays.copyOf(otherBitArray.data, longsToCopy);
            elements = otherBitArray.elements;
            return;
        }

        // standard copy
        initMembers(other.size());
        addAll(other);
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
    @Override
    public void add(int index, Boolean bit) {
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
     * Inserts the boolean value as a bit at the tail of the array
     *
     * @param bit the boolean value to be inserted
     * @return success / failure of the add operation
     */
    @Override
    public boolean add(Boolean bit) {
        add(elements, bit);
        return true;
    }

    /**
     * Returns the boolean value of the bit at the selected array index.
     *
     * @param index index of the element in the array
     * @return boolean value of the bit entry
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
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
    @Override
    public Boolean set(int index, Boolean bit) {
        ensureIndexInRange(index, elements - 1);
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
    @Override
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
    @Override
    public int size() {
        return elements;
    }

    /**
     * Clears the contents of the array and releases memory used previously.
     */
    @Override
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
        long rightmostBit = insertInLong(bitIntValue, 1, longIndex++, indexInLong);
        // keep inserting old LSB at 0 of next long and moving on with the new LSB
        while (longIndex <= maxLongIndex) {
            rightmostBit = insertInLong(rightmostBit, 1, longIndex++, 0);
        }
    }

    /**
     * Inserts the {@code lastLength} rightmost bits of lastValue in the position specified by {@code longIndex} and
     * {@code indexInLong}, and then shifts every element with index >= {@code indexInLong} to the right. The bits that
     * are shifted out are returned in the leftmost position
     *
     * @param lastValue   bits to be inserted into the long
     * @param lastLength  length in bits of the last value
     * @param longIndex   index of the long in the {@code data} array
     * @param indexInLong index of the insertion bit in the long
     * @return bits that were shifted out due to the insertion
     */
    private long insertInLong(long lastValue, int lastLength, int longIndex, int indexInLong) {
        // select the bits [indexInLong, (word end)] for the insertion
        long rightSide = (data[longIndex] << indexInLong) >>> indexInLong;
        // separate the left part, this will remain intact
        long leftSide = data[longIndex] & ~rightSide;

        // save the bits that will be shifted out
        long rightSideShiftOut = selectBits(rightSide, BITS_PER_LONG - lastLength, lastLength);
        // unsigned shift to the right to make space for the new bits
        rightSide >>>= lastLength;
        // set the new bits
        rightSide |= lastValue << (BITS_PER_LONG - lastLength - indexInLong);
        // re-join the two parts
        data[longIndex] = leftSide ^ rightSide;

        // return the discarded bits
        return rightSideShiftOut;
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
        long leftmostBit = 0; // dud value for first shift
        // keep adding the old MSB as LSB of the previous long index and shifting the rest to the left
        while (currentLongIndex > longIndex) {
            leftmostBit = removeAtIndexAndAppend(leftmostBit, 1, currentLongIndex--, 0);
        }
        // add the final MSB as LSB of longIndex and shift only the bits to the popped bit's right
        removeAtIndexAndAppend(leftmostBit, 1, longIndex, indexInLong);
    }

    /**
     * Removes the {@code lastLength} bits from the long specified by {@code longIndex} starting from {@code indexInLong}
     * and then appends the same length of bits from {@code lastValue} at the end of the long. The
     *
     * @param lastValue   bits to be appended to the long
     * @param lastLength  length in bits of the last value
     * @param longIndex   index of the long in the {@code data} array
     * @param indexInLong index of the first removed bit in the long
     * @return bits that were popped from the long
     */
    private long removeAtIndexAndAppend(long lastValue, int lastLength, int longIndex, int indexInLong) {
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = (data[longIndex] << indexInLong) >>> indexInLong;
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = data[longIndex] & ~rightSide;

        // save removed values
        long poppedValues = selectBits(rightSide, indexInLong, lastLength) >>> (BITS_PER_LONG - indexInLong - lastLength);

        // clear copied bits and shift to the left
        rightSide = (rightSide << indexInLong + lastLength) >>> indexInLong;
        // append the previous bits
        rightSide |= lastValue;

        // re-join the two parts
        data[longIndex] = leftSide ^ rightSide;

        // return the popped bits
        return poppedValues;
    }

    /**
     * Selects the bits [start, start + length) from the argument long, leaving everything else at 0
     *
     * @param start  start index of the selection
     * @param length number of bits to be kept intact
     * @return the argument long with only the selected bits preserved
     * @implSpec <p>
     * {@code start} should be in the range [0, 63]<br>
     * {@code length} should be in the range [1, 64]<br>
     * {@code start} and {@code length} should satisfy: start + length <= {@link #BITS_PER_LONG}
     * </p>
     */
    private long selectBits(long aLong, int start, int length) {
        long mask = Long.MIN_VALUE >>> start; // need at least the first bit
        mask |= (Long.MIN_VALUE >>> start) - 1; // make everything to the right ones
        mask &= -(Long.MIN_VALUE >>> (start + length - 1)); // make everything from end of length and forward 0
        return aLong & mask;
    }

    /**
     * Checks for index out of bounds.
     *
     * @param index        index to be checked
     * @param endInclusive last allowed value of the index
     */
    private void ensureIndexInRange(int index, int endInclusive) {
        if (index < 0 || index > endInclusive) {
            throw new IndexOutOfBoundsException("Array index " + index + " out of bounds for array size " + size());
        }
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
     * Extends the array size to store at least one more element.
     *
     * <p>
     * Doubling of size preferred.
     * </p>
     */
    private void ensureCapacity() {
        // check for completely full array
        if (elements == Integer.MAX_VALUE) {
            throw new IllegalStateException("Cannot insert; array is completely full. Size = " + size());
        }
        // extend if currently full
        if (elements == data.length * BITS_PER_LONG) {
            doubleSize();
        }
    }

    /**
     * Doubles the size of the array.
     */
    private void doubleSize() {
        // make sure new element count does not overflow
        // we can't index more than Integer.MAX_VALUE elements through the List interface anyway
        int newSize = (int) Math.min(2L * elements, Integer.MAX_VALUE);
        resize(newSize);
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
            initMembers(DEFAULT_CAPACITY);
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
    long singleBitMask(int bitIndex) {
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
     * @return smallest number of longs needed to contain {@code nBits} bits.
     */
    private int longsRequiredForNBits(int nBits) {
        return (int) Math.ceil(
                (double) nBits / BITS_PER_LONG);
    }

    /*
        BitArray specific methods
    */

    /**
     * Calculates the parity of {@link Boolean#TRUE ones} in the array
     *
     * @return 0 or 1 if there is an even or odd number of {@link Boolean#TRUE ones} respectively
     */
    public int sumMod2() {
        if (isEmpty()) return 0;
        int sumMod2 = 0;
        int limit = longsRequiredForNBits(size()) - 1;

        sumMod2 += Long.bitCount(Arrays.stream(data)
                .limit(limit)
                .reduce(0L, (i, j) -> i ^ j));

        int remainingBitsIndex = limit * BITS_PER_LONG;
        for (int i = remainingBitsIndex; i < elements; i++) {
            if (get(i)) {
                sumMod2++;
            }
        }

        return sumMod2 % 2;
    }

    /**
     * Counts the number of {@code true} elements in the array
     *
     * @return number of true elements in the array
     */
    public int countOnes() {
        if (isEmpty()) return 0;
        int oneCount = 0;
        int limit = longsRequiredForNBits(size()) - 1;

        oneCount += Arrays.stream(data)
                .limit(limit)
                .map(Long::bitCount)
                .sum();

        int remainingBitsIndex = limit * BITS_PER_LONG;
        for (int i = remainingBitsIndex; i < elements; i++) {
            if (get(i)) {
                oneCount++;
            }
        }

        return oneCount;
    }

    /**
     * Counts the number of {@code false} elements in the array
     *
     * @return number of false elements in the array
     */
    public int countZeros() {
        return size() - countOnes();
    }

    /**
     * Finds the index of the first occurrence of {@code needle} by skipping multiple occurrences of {@code !needle}
     *
     * <p>This method should be used when {@code needle} is indeed a needle in a haystack of {@code !needle} elements.
     * In other cases, it most likely run slower than {@link #indexOf(Object) indexOf}. It skips ahead of multiples of
     * {@link #BITS_PER_LONG 64} starting at element 0, so 0-63, 64-127 etc, using a single {@code long} comparison for each.</p>
     *
     * @param needle the boolean element
     * @return index of the first occurrence of {@code needle} or -1 if not found
     */
    public int indexOfNeedle(boolean needle) {
        final long indifferentLongFormat = needle ? 0L : -1L; // if looking for True 0L longs can be skipped etc
        int longIndex = 0;
        int longIndexLimit = longsRequiredForNBits(size()) - 1;

        while (longIndex < longIndexLimit && data[longIndex] == indifferentLongFormat) {
            longIndex++;
        }

        for (int i = longIndex * BITS_PER_LONG; i < elements; i++) {
            if (get(i).equals(needle)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a deep copy of this object
     *
     * @return deep copy of {@code this}
     */
    @Contract(" -> new")
    @Override
    public BitArray clone() {
        return new BitArray(this);
    }

    /**
     * Serializes the array into a string.
     *
     * <p>
     * The string consists of the number of elements in the array and a list of the elements in a human readable
     * format. Exact representation is "Size = SIZE, [(((0 | 1) + ' ')* (0 | 1))?]" where SIZE is a non negative integer
     * and '+' is the concatenation operator. The list of elements consists of opening square brackets ([), zero or more
     * bits (single digit ones or zeros) separated by spaces and closing square brackets.
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
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(size() * 2 + 10);

        // write size of the array
        s.append("Size = ").append(size()).append(", ");

        // write the list of bits as 1s and 0s
        s.append('[');
        for (int i = 0; i < size() - 1; i++) {
            s.append(Boolean.compare(get(i), Boolean.FALSE));
            s.append(' ');
        }
        if (size() > 0) {
            s.append(Boolean.compare(get(size() - 1), Boolean.FALSE));
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

        final String start = "Size = ";

        if (!stringArray.startsWith(start)) {
            throw new UnknownFormatConversionException("Not a valid BitArray string");
        }

        // count number of digits of the array size
        int currentIndex = stringArray.indexOf(",", start.length());

        int arraySize;
        try {
            String arraySizeStr = stringArray.substring(start.length(), currentIndex);
            arraySize = Integer.parseInt(arraySizeStr);


            // move the cursor to the first element
            currentIndex += ", [".length();

            // read elements
            List<Character> allowedElements = List.of('0', '1');

            BitArray result = new BitArray(arraySize);
            for (int i = 0; i < arraySize; i++) {
                char current = stringArray.charAt(currentIndex);
                if (currentIndex >= stringArray.length() - 1 || !allowedElements.contains(current)) {
                    throw new UnknownFormatConversionException("Not a valid BitArray string");
                }

                result.add(current == '1');
                currentIndex += 2;
            }

            return result;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new UnknownFormatConversionException("Not a valid BitArray string");
        }
    }

    /*
    Non-null overrides
     */

    @Override
    public int indexOf(Object o) {
        return super.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return super.lastIndexOf(o);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Boolean> c) {
        return super.addAll(index, c);
    }

    @Override
    public Iterator<Boolean> iterator() {
        return super.iterator();
    }

    @Override
    public ListIterator<Boolean> listIterator() {
        return super.listIterator();
    }

    @Override
    public ListIterator<Boolean> listIterator(int index) {
        return super.listIterator(index);
    }

    @Override
    public List<Boolean> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }

    @Override
    public Boolean[] toArray() {
        return toArray(new Boolean[size()]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return super.toArray(a);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return super.toArray(generator);
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Boolean> c) {
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Boolean> operator) {
        super.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Boolean> c) {
        super.sort(c);
    }

    @Override
    public Spliterator<Boolean> spliterator() {
        return super.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super Boolean> filter) {
        return super.removeIf(filter);
    }

    @Override
    public Stream<Boolean> stream() {
        return super.stream();
    }

    @Override
    public Stream<Boolean> parallelStream() {
        return super.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super Boolean> action) {
        for (Boolean b : this) {
            action.accept(b);
        }
    }

}

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
 * <h2>Random access List&lt;Boolean&gt; that uses a long primitive array to store its elements. Each element
 * occupies a single bit in its corresponding long</h2>
 *
 * <p>This class is superior to ArrayList in terms of CRUD performance and memory usage. Its limitation is its
 * inability to store {@code null} values.</p>
 *
 * <p>Read only operations such as {@link #get(int)} and iterator traversals
 * run at about the same time. {@link #add(Boolean) Add} and {@link #remove(int) remove} at the tail also have similar
 * performance. The most significant gains come from element copy and move operations. This includes
 * random index {@link #add(Boolean) add} and {@link #remove(int) remove}, array resizes etc.
 * You can find my benchmarks and their results from my machine
 * <a href=https://github.com/Abductcows/bit-array-benchmarks>here</a></p>
 *
 * <p>This class is NOT synchronized. For a synchronized version, use {@link java.util.Collections.SynchronizedList}</p><br>
 *
 * <h2>Caveats</h2>
 * <p><b>No nulls.</b> As mentioned above, the array will not accept null values, and throw a {@link NullPointerException} instead.</p>
 * <p><b>Whole list copies.</b> Note that methods which return a copy of the elements will probably not follow the one bit per entry
 * principle.
 * {@link java.util.AbstractList.SubList SubList} and
 * {@link java.util.Collections.SynchronizedList SynchronizedList}
 * are safe to use of course.</p>
 *
 * @version 2.0.0
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
    public static final int DEFAULT_CAPACITY = BITS_PER_LONG;

    /**
     * Element storage
     */
    private long[] data = new long[0];

    /**
     * Current number of elements
     */
    private int elements;

    /**
     * Initialises the array to some default capacity
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
     * Initialises the array as a freshly created array. The array should not contain any elements after a call to this
     *
     * @param initialCapacity initial capacity of the array in bit entries
     */
    private void initMembers(int initialCapacity) {
        int sizeInLongs = longsRequiredForNBits(initialCapacity);
        data = new long[sizeInLongs];
        elements = 0;
    }

    /**
     * Inserts the boolean value at the argument index.
     *
     * @param index array index to insert the element in
     * @param bit   the boolean value to be inserted
     * @throws IndexOutOfBoundsException if index is out of array insertion bounds
     * @throws IllegalStateException     if array size is Integer.MAX_VALUE at the time of insertion
     */
    @Override
    public void add(int index, Boolean bit) {
        ensureIndexInRange(index, size());
        ++modCount;
        ensureCapacity();

        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // check for append
        if (index == size()) {
            setBit(longIndex, indexInLong, bit);
            ++elements;
            return;
        }

        // else insert normally
        addAndShiftAllRight(bit, longIndex, indexInLong);
        ++elements;
    }

    /**
     * Inserts the boolean value at the tail of the array.
     *
     * @param bit the boolean value to be inserted
     * @return success / failure of the add operation
     * @throws IllegalStateException if array size is Integer.MAX_VALUE at the time of insertion
     */
    @Override
    public boolean add(Boolean bit) {
        add(size(), bit);
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
        ensureIndexInRange(index, size() - 1);
        // get bit indices
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        // return the bit
        return getBit(longIndex, indexInLong);
    }

    /**
     * Sets the value of the element at the specified index to the desired value and returns the old value.
     *
     * @param index index of the array element to be changed
     * @param bit   the new value of the array element
     * @return boolean value of the previous bit at that index
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
    public Boolean set(int index, Boolean bit) {
        ensureIndexInRange(index, size() - 1);
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
     * Removes and returns the element at the specified index.
     *
     * @param index index of the element
     * @return boolean value of the removed bit
     * @throws IndexOutOfBoundsException if index is out of array bounds
     */
    @Override
    public Boolean remove(int index) {
        ensureIndexInRange(index, size() - 1);
        ++modCount;

        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);
        boolean removedBit = getBit(longIndex, indexInLong);
        // move the rest of the elements to the left
        removeAndShiftAllLeft(longIndex, indexInLong);

        --elements;
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
        ++modCount;
        initMembers(DEFAULT_CAPACITY);
    }

    private int getLongIndex(int globalIndex) {
        return globalIndex / BITS_PER_LONG;
    }

    private int getIndexInLong(int globalIndex) {
        return globalIndex % BITS_PER_LONG;
    }

    private boolean getBit(int longIndex, int indexInLong) {
        return getBitInLong(data[longIndex], indexInLong) != 0;
    }

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
     * @param longIndex   index of the insertion long in the data array
     * @param indexInLong index of the bit in the long of the insertion
     */
    private void addAndShiftAllRight(boolean bit, int longIndex, int indexInLong) {
        int bitIntValue = boolToInt(bit);
        int maxLongIndex = getLongIndex(size());
        // insert the bit and save the LSB that was shifted out
        long rightmostBit = insertBitsInLong(longIndex, indexInLong, bitIntValue, 1);
        // keep inserting old LSB at 0 of next long and move on with the new shift-out LSB
        for (int currentLongIndex = longIndex + 1; currentLongIndex <= maxLongIndex; currentLongIndex++) {
            rightmostBit = insertBitsInLong(currentLongIndex, 0, rightmostBit, 1);
        }
        // (last rightmost bit is always garbage)
    }

    /**
     * Inserts the {@code lastLength} rightmost bits of {@code lastValue} at index {@code longIndex} of the {@code indexInLong}
     * word. The rightmost bits of the previous word that overflowed are returned.
     */
    @SuppressWarnings("SameParameterValue")
    private long insertBitsInLong(int insertLongIndex, int insertOffset, long newBits, int newBitsLength) {
        // split the word on indexInLong, left side will remain the same
        long rightSide = selectAllBitsStarting(data[insertLongIndex], insertOffset);
        final long leftSide = data[insertLongIndex] & ~rightSide;

        // pop the shifted out bits
        long rightSideShiftOut = selectLastNBits(rightSide, newBitsLength);
        rightSide >>>= newBitsLength;

        // set the new bits and return the shift-out
        int shiftToAlignWithSplitIndex = BITS_PER_LONG - insertOffset - newBitsLength;
        rightSide |= newBits << shiftToAlignWithSplitIndex;
        data[insertLongIndex] = leftSide ^ rightSide;
        return rightSideShiftOut;
    }

    /**
     * Removes the bit at the array index and shifts everything to its right to the left.
     *
     * @param longIndex   index of the long of the remove index
     * @param indexInLong index of the bit in the long of the removal
     */
    private void removeAndShiftAllLeft(int longIndex, int indexInLong) {
        int currentLongIndex = getLongIndex(size() - 1);
        long leftmostBit = 0; // dud value for first shift
        // keep adding the old MSB as LSB of the previous long index and shifting the rest to the left
        while (currentLongIndex > longIndex) {
            leftmostBit = removeBitsFromLongAndAppend(currentLongIndex--, 0, leftmostBit, 1);
        }
        // add the final MSB as LSB of the argument long and shift ONLY the bits to the popped bit's right
        removeBitsFromLongAndAppend(longIndex, indexInLong, leftmostBit, 1);
    }

    /**
     * Removes the specified number of bits from the specified long starting at the given index. Then appends
     * the same number of bits from the new bits argument at the end of the long. Popped bits are returned
     */
    @SuppressWarnings("SameParameterValue")
    private long removeBitsFromLongAndAppend(int removeLongIndex, int removeOffset, long newBits, int newBitsLength) {
        // split the word on indexInLong, left side will remain the same
        long rightSide = selectAllBitsStarting(data[removeLongIndex], removeOffset);
        final long leftSide = data[removeLongIndex] & ~rightSide;

        // pop the removed values
        long poppedValues = selectBits(rightSide, removeOffset, newBitsLength);
        rightSide = rightSide << removeOffset + newBitsLength >>> removeOffset;

        // set the new bits and return the popped bits
        rightSide |= newBits;
        data[removeLongIndex] = leftSide ^ rightSide;
        int shiftToRightAlign = BITS_PER_LONG - removeOffset - newBitsLength;
        return poppedValues >>> shiftToRightAlign;
    }

    /**
     * Copies the bits [start, start + length) from the argument long, leaving everything else at 0
     */
    private long selectBits(long original, int start, int length) {
        long leftCleared = original << start >>> start;
        return leftCleared >>> BITS_PER_LONG - start - length << BITS_PER_LONG - start - length;
    }

    private long selectAllBitsStarting(long original, int start) {
        return selectBits(original, start, BITS_PER_LONG - start);
    }

    private long selectLastNBits(long original, int length) {
        return selectBits(original, BITS_PER_LONG - length, length);
    }

    /**
     * Checks for index out of bounds (right inclusive).
     */
    private void ensureIndexInRange(int index, int endInclusive) {
        if (index < 0 || index > endInclusive) {
            throw new IndexOutOfBoundsException("Array index " + index + " out of bounds for array size " + size());
        }
    }

    /**
     * Ensures that the array can store a new element. Resizes if not possible.
     *
     * @throws IllegalStateException if the array is at max size ({@link Integer#MAX_VALUE})
     */
    private void ensureCapacity() {
        // check for completely full array
        if (size() == Integer.MAX_VALUE) {
            throw new IllegalStateException("Cannot insert; array is completely full. Size = " + size());
        }
        // extend if currently full
        if (size() == data.length * BITS_PER_LONG) {
            doubleSize();
        }
    }

    private void doubleSize() {
        // make sure new element count does not overflow
        // we can't index more than Integer.MAX_VALUE elements through the List interface anyway
        int newSize = (int) Math.min(2L * size(), Integer.MAX_VALUE);
        resize(newSize);
    }

    private void resize(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException("Array size requested is negative: " + newSize);
        }
        if (newSize == 0) {
            initMembers(DEFAULT_CAPACITY);
            return;
        }
        int newSizeInLongs = longsRequiredForNBits(newSize);
        data = Arrays.copyOf(data, newSizeInLongs);
        elements = Math.min(elements, newSize);
    }

    /**
     * Returns a long bit mask with only the argument bit set to 1
     */
    long singleBitMask(int bitIndex) {
        return Long.MIN_VALUE >>> bitIndex;
    }

    /**
     * Returns 0 or 1 based on the value of the specified bit in the long
     */
    private int getBitInLong(long theLong, int bitIndex) {
        return (int) (theLong >> (BITS_PER_LONG - 1 - bitIndex)) & 1;
    }

    /**
     * Returns the smallest number of long words needed to contain {@code nBits} bits.
     */
    private int longsRequiredForNBits(int nBits) {
        return (int) Math.ceil(
                (double) nBits / BITS_PER_LONG);
    }

    private int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    /*
        BitArray specific methods
    */

    /**
     * Counts the number of {@code true} elements in the array
     *
     * @return number of true elements in the array
     */
    public int countOnes() {
        if (isEmpty()) return 0;
        int oneCount = 0;
        int limit = longsRequiredForNBits(size()) - 1; // all full longs

        for (int i = 0; i < limit; i++) {
            oneCount += Long.bitCount(data[i]);
        }

        // last occupied long, not filled
        int remainingBits = size() - limit * BITS_PER_LONG;

        for (int i = 0; i < remainingBits; i++) {
            if (getBit(limit, i)) {
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
     * In other cases, it will most likely run slower than {@link #indexOf(Object) indexOf}. It skips ahead of multiples
     * of 64, starting at element 0. Meaning it will skip 0-63, 64-127 etc, using a single {@code long} comparison for each.</p>
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
     * Creates a deep copy of this object
     *
     * @return deep copy of {@code this}
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
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
            s.append(boolToInt(get(i)));
            s.append(' ');
        }
        if (size() > 0) {
            s.append(boolToInt(get(size() - 1)));
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

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] a) {
        return super.toArray(a);
    }

    @SuppressWarnings("SuspiciousToArrayCall")
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

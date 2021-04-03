package gr.geompokon.bitarray;

import java.util.Arrays;

public class BitArrayImpl {

    private static final int BITS_PER_LONG = 64;
    private static final int DEFAULT_CAPACITY = BITS_PER_LONG;
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
                (int) Math.round(
                        Math.ceil(
                                (double) initialCapacity / BITS_PER_LONG));
        data = new long[sizeInLongs];
        elements = 0;
    }

    public boolean add(boolean bit) {
        add(elements, bit);
        return true;
    }

    public void add(int index, boolean bit) {
        // check for index out of bounds
        if (index < 0 || index > elements) {
            throw new IndexOutOfBoundsException("Array index out of bounds");
        }
        ensureCapacity();

        // check for append
        if (index == elements) {
            setBit(elements, bit);
        } else {
            int longIndex = getLongIndex(index);
            int indexInLong = getIndexInLong(index);
            addAndShiftAllRight(bit, longIndex, indexInLong);
        }

        elements = elements + 1;
    }

    /**
     * Returns the bit value from the selected index
     *
     * @param index index of the bit in the array
     * @return 0 or 1 corresponding to the bit value
     * @throws IndexOutOfBoundsException if index is negative or ge to number of elements
     */
    public boolean get(int index) {
        if (index < 0 || index >= elements) {
            throw new IndexOutOfBoundsException("Array index out of bounds");
        }
        // get index of the long housing the bit
        int longIndex = getLongIndex(index);
        // get index of the bit inside the long
        int indexInLong = getIndexInLong(index);

        long onlySelectedBit = data[longIndex] & getBitMask(indexInLong);

        // result of & is zero if-f the bit is zero
        return onlySelectedBit != 0;
    }

    public boolean set(int index, boolean bit) {
        boolean oldBit = get(index);
        setBit(index, bit);
        return oldBit;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= elements) {
            throw new IndexOutOfBoundsException("Array index out of bounds");
        }
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
        return bitIndex / BITS_PER_LONG;
    }

    private int getIndexInLong(int bitIndex) {
        return bitIndex % BITS_PER_LONG;
    }

    private void setBit(int index, boolean bit) {
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        if (bit) {
            data[longIndex] |= getBitMask(indexInLong);
        } else {
            data[longIndex] &= ~getBitMask(indexInLong);
        }
    }

    private void addAndShiftAllRight(boolean bit, int longIndex, int indexInLong) {
        // start at current long index and work all the way to the last long
        int maxLongIndex = getLongIndex(elements);
        // add the bit and save the LSB that was shifted out
        int bitIntValue = bit ? 1 : 0;
        int LSB = insertInLongShiftRight(bitIntValue, longIndex++, indexInLong);
        // keep inserting old LSB at 0 of next long and moving on with the new LSB
        while (longIndex <= maxLongIndex) {
            LSB = insertInLongShiftRight(LSB, longIndex++, 0);
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
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = getBitsStartToIndexExclusive(indexInLong, data[longIndex]);
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = getBitsIndexToEnd(indexInLong, data[longIndex]);

        // save LSB
        int rightSideLSB = (int) rightSide & 1;
        // unsigned shift to the right to make space for the new bit
        rightSide >>>= 1;
        // new bit is 0 from the shift, change it to 1 if required
        if (bit == 1) {
            rightSide |= getBitMask(indexInLong);
        }
        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the LSB
        return rightSideLSB;
    }

    private void removeAndShiftAllLeft(int longIndex, int indexInLong) {
        // start at the end and work back to current long index
        int currentLongIndex = getLongIndex(elements - 1);
        int MSB = 0; // dud value for first shift
        // keep adding the old MSB as LSB of the previous long index and shifting the rest to the left
        while (currentLongIndex > longIndex) {
            MSB = appendLongShiftLeft(MSB, currentLongIndex--, 0);
        }
        // add the final MSB as LSB of {@code longIndex} and shift only the bits to the removed's right
        appendLongShiftLeft(MSB, longIndex, indexInLong);
    }

    private int appendLongShiftLeft(int bit, int longIndex, int indexInLong) {
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = getBitsStartToIndexExclusive(indexInLong, data[longIndex]);
        // get right side [indexInLong : ], can not be empty, will be shifted
        long rightSide = getBitsIndexToEnd(indexInLong, data[longIndex]);

        // save MSB
        int rightSideMSB = (int) (rightSide & getBitMask(indexInLong));
        // clear MSB and shift to the left to make it disappear
        rightSide &= ~getBitMask(indexInLong);
        rightSide <<= 1;
        // append the previous bit
        rightSide += bit;

        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the MSB
        return rightSideMSB == 0 ? rightSideMSB : 1;
    }

    private long getBitMask(int bitIndex) {
        return 1L << (63 - bitIndex);
    }

    private long getSelectionMask(int index) {
        return index == 0 ?
                -1 :
                getBitMask(index - 1) - 1;
    }

    private long getBitsStartToIndexExclusive(int indexExclusive, long theLong) {
        return theLong & (~getSelectionMask(indexExclusive));
    }

    private long getBitsIndexToEnd(int index, long theLong) {
        return theLong & getSelectionMask(index);
    }

    private void ensureCapacity() {
        if (elements == data.length * BITS_PER_LONG) {
            resize(2 * elements);
        }
    }

    public void resize(int newSize) {
        // make sure to create enough longs for new size
        newSize = Math.max(newSize, 1);
        int newSizeInLongs = (int)
                Math.round(
                        Math.ceil(
                                (double) newSize / BITS_PER_LONG));

        // copy data
        data = Arrays.copyOf(data, newSizeInLongs);
        // if elements were truncated, update element count
        if (newSize < elements) {
            elements = newSize;
        }
    }
}

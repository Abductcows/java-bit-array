package gr.geompokon.bitarray;

import java.util.ArrayList;
import java.util.Arrays;

public class BitArrayImpl {

    // p2[i] is a long with 1 only in index i (left to right)
    // p2[i-1] - 1 is a bit mask with ones at index i and to the right (i >= 1)
    private static final long[] bits;

    static {
        bits = new long[]{
                Long.MIN_VALUE, // 0b100...0
                4611686018427387904L, // 2^62, 0b0100...0
                2305843009213693952L, // 2^61, 0b00100..0
                1152921504606846976L, // ...
                576460752303423488L,
                288230376151711744L,
                144115188075855872L,
                72057594037927936L,
                36028797018963968L,
                18014398509481984L,
                9007199254740992L,
                4503599627370496L,
                2251799813685248L,
                1125899906842624L,
                562949953421312L,
                281474976710656L,
                140737488355328L,
                70368744177664L,
                35184372088832L,
                17592186044416L,
                8796093022208L,
                4398046511104L,
                2199023255552L,
                1099511627776L,
                549755813888L,
                274877906944L,
                137438953472L,
                68719476736L,
                34359738368L,
                17179869184L,
                8589934592L,
                4294967296L,
                2147483648L,
                1073741824L,
                536870912L,
                268435456L,
                134217728L,
                67108864L,
                33554432L,
                16777216L,
                8388608L,
                4194304L,
                2097152L,
                1048576L,
                524288L,
                262144L,
                131072L,
                65536L,
                32768L,
                16384L,
                8192L,
                4096L,
                2048L,
                1024L, // 2^10
                512L,
                256L,
                128L,
                64L,
                32L,
                16L,
                8L,
                4L,
                2L,
                1L, // 2^0
        };
    }

    private static final int DEFAULT_SIZE = 32;
    private static final int BITS_PER_LONG = 64;
    private long[] data;
    private int elements; // number of elements in the array

    public BitArrayImpl() {
        this(DEFAULT_SIZE);
    }
    public BitArrayImpl(int initialLength) {
        if (initialLength < 0) {
            throw new IllegalArgumentException("Array initial size is negative: " + initialLength);
        }
        int sizeInLongs = (int) Math.round(Math.ceil((double)initialLength / BITS_PER_LONG));
        data = new long[sizeInLongs];
        elements = 0;
    }

    public boolean add(boolean bit) {
        return add(elements, bit);
    }
    public boolean add(int index, boolean bit) {
        // check for index out of bounds
        if (index < 0 || index > elements) {
            throw new IndexOutOfBoundsException("Array index out of bounds");
        }
        ensureCapacity();

        // check for append or non-appending insert
        if (index == elements) {
            setBit(elements, bit);
        } else {
            addAndShiftAllRight(bit, getLongIndex(index), getIndexInLong(index));
        }

        elements = elements + 1;
        return true;
    }

    public boolean set(int index, boolean bit) {
        boolean oldBit = get(index);
        setBit(index, bit);
        return oldBit;
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

        long onlySelectedBit = data[longIndex] & bits[indexInLong];

        // result of & is zero if-f the bit is zero
        return onlySelectedBit != 0;
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
            data[longIndex] |= bits[indexInLong];
        } else {
            data[longIndex] &= ~bits[indexInLong];
        }
    }

    private void addAndShiftAllRight(boolean bit, int longIndex, int indexInLong) {
        // start at long index and work all the way to the end of the array
        int maxLongIndex = getLongIndex(elements);
        // add the bit and save the LSB that was shifted out
        int bitIntValue = bit? 1 : 0;
        int LSB = insertInLongShiftRight(bitIntValue, longIndex++, indexInLong);
        // keep inserting old LSB at 0 of next long and moving on with the new LSB
        while (longIndex <= maxLongIndex) {
            LSB = insertInLongShiftRight(LSB, longIndex++, 0);
        }
    }
    /**
     * Inserts the bit in the index of the long specified by the arguments by shifting
     * everything to its right to the right. If the bit that was shifted out of the long
     * was a valid element of the array, it is returned.
     *
     * @param bit         the bit to be inserted
     * @param longIndex   index of the long in the data array
     * @param indexInLong index of the bit in the long
     * @return LSB of the long before insertion or -1 if it was not an element of the array
     */
    private int insertInLongShiftRight(int bit, int longIndex, int indexInLong) {
        // get left side [0 : indexInLong), can be empty, will remain intact
        long leftSide = getBitsStartToIndexExclusive(indexInLong, data[longIndex]);
        // get right side [indexInLong : ], can not be empty, needs to be shifted
        long rightSide = getBitsIndexToEnd(indexInLong, data[longIndex]);

        // save LSB
        long rightSideLSB = rightSide & 1L;
        // unsigned shift to the right to make space for the new bit
        rightSide >>>= 1;
        // new bit is 0 from the shift, change it to 1 if required
        if (bit == 1) {
            rightSide |= bits[indexInLong];
        }
        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the LSB
        return (int) rightSideLSB;
    }

    private void removeAndShiftAllLeft(int longIndex, int indexInLong) {
        // start at the end and work back to current long index
        int currentLongIndex = getLongIndex(elements-1);
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
        // get right side [indexInLong : ], can not be empty, needs to be shifted
        long rightSide = getBitsIndexToEnd(indexInLong, data[longIndex]);

        // save MSB
        long rightSideMSB = rightSide & bits[indexInLong];
        // clear MSB and shift to the left to make it disappear
        rightSide &= ~bits[indexInLong];
        rightSide <<= 1;
        // append the previous bit
        rightSide += bit;

        // re-join the two parts
        data[longIndex] = leftSide + rightSide;

        // return the MSB
        return rightSideMSB == 0 ? 0 : 1;
    }

    private long getSelectionMask(int index) {
        return index == 0 ?
                -1 :
                bits[index - 1] - 1;
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

    int getDataLength() {
        return data.length;
    }
}

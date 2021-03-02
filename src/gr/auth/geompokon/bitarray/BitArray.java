package gr.auth.geompokon.bitarray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.RandomAccess;

public class BitArray implements RandomAccess {


    public static void main(String[] args) {

    }

    // p2[i] is a long with 1 only in index 63-i (left to right)
    private static final long[] bits;

    static {
        bits = new long[]{
                Long.MIN_VALUE,
                4611686018427387904L,
                2305843009213693952L,
                1152921504606846976L,
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
                1024L,
                512L,
                256L,
                128L,
                64L,
                32L,
                16L,
                8L,
                4L,
                2L,
                1L,
        };
    }

    private static final int DEFAULT_SIZE = 512;
    private static final int BITS_PER_LONG = 63;
    long[] data;
    int elements; // number of entries in the array

    private boolean autoShrink;

    ArrayList<Long> reference; // cannot call add at index > size


    public BitArray() {
        this(DEFAULT_SIZE);
    }

    public BitArray(int initialLength) {
        this(initialLength, false);
    }

    public BitArray(int initialLength, boolean autoShrink) {
        if (initialLength < 0) {
            throw new IllegalArgumentException("Bit array initial size is negative");
        }
        data = new long[initialLength];
        this.autoShrink = autoShrink;
        // new array, 0 entries so far
        elements = 0;
    }

    // todo: inline this?
    private int getLongIndex(int bitIndex) {
        return bitIndex / BITS_PER_LONG;
    }

    public int getIndexInLong(int bitIndex) {
        return bitIndex % BITS_PER_LONG + 1;
    }

    public void add(boolean bit, int index) {
        if (bit) {
            add(1, index);
        } else {
            add(0, index);
        }
    }

    public void add(boolean bit) {
        if (bit) {
            add(1);
        } else {
            add(0);
        }
        // add(Boolean.compare(bit, Boolean.FALSE)); true->1, false->0  // y tho
    }

    public void add(int bit) {
        add(bit, elements);
    }

    public void add(int bit, int index) {
        // check if array is full
        if (elements == data.length * BITS_PER_LONG) {
            extendArray();
        }

        // check for append or non-appending insert
        if (index == elements) {
            append(bit);
        } else {
            insertAtIndex(bit, index);
        }

        elements = elements + 1;
    }

    private void append(int bit) {
        int longIndex = getLongIndex(elements);
        int indexInLong = getIndexInLong(elements);

        if (bit == 0) {
            data[longIndex] &= ~bits[indexInLong];
        } else {
            data[longIndex] |= bits[indexInLong];
        }
    }

    private void insertAtIndex(int bit, int index) {
        int longIndex = getLongIndex(index);
        int indexInLong = getIndexInLong(index);

        int LSB = moveAndInsertInLong(bit, longIndex, indexInLong);
        while (LSB != -1) {
            LSB = moveAndInsertInLong(LSB, ++longIndex, 1);
        }
    }

    private int moveAndInsertInLong(int previousBit, int longIndex, int indexInLong) {
        // get bits from index to LSB
        long rightSide = data[longIndex] & (bits[indexInLong] - 1);
        // save lsb
        long LSB = rightSide & 1L;
        // shift right to the right
        rightSide >>>= 1;
        // clear the right side bits from data and add the new ones
        data[longIndex] &= -bits[indexInLong]; // clear
        data[longIndex] += rightSide;
        // set the new bit to 1 or leave it at 0
        if (previousBit == 1) {
            data[longIndex] |= bits[indexInLong];
        }

        if (elements + 1 < (longIndex+1) * 63) {
            return -1;
        }
        return (int) LSB;
    }

    public void remove(int index) {
        // TODO


        // update number of elements
        elements = elements - 1;

        // shrink array if autoshrink is enabled
        if (autoShrink && 2 * elements < data.length * BITS_PER_LONG) {
            shrinkArray();
        }
    }

    public void set(int index, boolean bit) {
        if (bit) {
            set(index, 1);
        } else {
            set(index, 0);
        }
    }

    public void set(int index, int bit) {
        if (index == elements) {
            add(bit);
        } else {
            throw new UnsupportedOperationException("Set not yet supported");
            // TODO
        }
    }

    /**
     * Returns the bit value from the selected index
     *
     * @param index index of the bit in the array
     * @return 0 or 1 corresponding to the bit value
     * @throws IndexOutOfBoundsException if index is negative or ge to number of elements
     */
    public int get(int index) {
        // check for index out of bounds
        if (index < 0 || index >= elements) {
            throw new IndexOutOfBoundsException("Bit array index out of bounds");
        }
        // get index of the long housing the bit
        int longIndex = getLongIndex(index);
        // get index of the bit inside the long
        int indexInLong = getIndexInLong(index); // todo: change this from modulo to subtraction?

        // return the value of the bit in the long
        return getBitInLong(indexInLong, data[longIndex]);
    }

    public int size() {
        return elements;
    }

    public boolean isEmpty() {
        return elements == 0;
    }

    /**
     * Returns the bit in index i (counting left to right) from argument long
     *
     * @param i index of the bit
     * @param l long housing the bit
     * @return integer value of the bit
     */
    private int getBitInLong(int i, long l) {

        // bitwise & with 1 bit mask to get the desired bit
        long onlySelectedBit = l & bits[i];

        // result of & is zero if-f the bit is zero
        if (onlySelectedBit == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    private void extendArray() {
        data = Arrays.copyOf(data, 2 * data.length);
    }

    private void shrinkArray() {
        data = Arrays.copyOf(data, data.length / 2);
    }

}

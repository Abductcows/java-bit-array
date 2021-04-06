package gr.geompokon.bitarray;

class ImplBitUtilities {

    static final int BITS_PER_LONG = 64;

    long getBitMask(int bitIndex) {
        return 1L << (BITS_PER_LONG - 1 - bitIndex);
    }

    long getSelectionMask(int index) {
        return index == 0 ?
                -1 :
                getBitMask(index - 1) - 1;
    }

    long getBitsStartToIndexExclusive(int indexExclusive, long theLong) {
        return theLong & (~getSelectionMask(indexExclusive));
    }

    long getBitsIndexToEnd(int index, long theLong) {
        return theLong & getSelectionMask(index);
    }

    /**
     * Returns the integer value of the bit in the long (0 or 1)
     *
     * @param theLong  the long
     * @param bitIndex index of the bit in the long
     * @return int value of the bit
     */
    int getBitInLong(long theLong, int bitIndex) {
        if ((theLong & getBitMask(bitIndex)) == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * Returns the argument long with its specified bit set to 1
     *
     * @param theLong  the long
     * @param bitIndex index of the bit to be set
     * @return argument long with its target bit set to 1
     */
    long longWithSetBit(long theLong, int bitIndex) {
        return theLong | getBitMask(bitIndex);
    }

    /**
     * Returns the argument long with its specified bit set to 0
     *
     * @param theLong  the long
     * @param bitIndex index of the bit to be cleared
     * @return argument long with its target bit set to 0
     */
    long longWithClearedBit(long theLong, int bitIndex) {
        return theLong & (~getBitMask(bitIndex));
    }

    /**
     * Returns the two longs as the result of splitting the argument long in two parts using the {@code rightSideStart}
     * argument as an index.
     * <p>
     * Result is {left, right} where
     * The left part of the result is all bits before {@code rightSideStart} (it is possible to be 0 bits)
     * The right part of the result is all bits from {@code rightSideStart} to the end
     * <p>
     * Note that the bits are returned as-is and are not shifted so {@code left + right == theLong} is always true
     *
     * @param theLong        the long to be split
     * @param rightSideStart index of the start of the right side
     * @return array containing the left and right part of the long
     */
    long[] splitLong(long theLong, int rightSideStart) {
        long leftSide = getBitsStartToIndexExclusive(rightSideStart, theLong);
        long rightSide = getBitsIndexToEnd(rightSideStart, theLong);

        return new long[]{leftSide, rightSide};
    }
}

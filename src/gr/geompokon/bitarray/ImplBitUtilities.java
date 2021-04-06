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

/**
 * Package private bit operation utility class for BitArrayImpl
 */
class ImplBitUtilities {

    // number of bits in a long integer
    static final int BITS_PER_LONG = 64;


    /**
     * Returns the int value of the argument boolean; 0 for false and 1 for true
     *
     * @param bit boolean to be evaluated
     * @return int value of the boolean
     */
    int bitIntValue(boolean bit) {
        return Boolean.compare(bit, Boolean.FALSE);
    }

    /**
     * Returns the boolean value of the argument int; false for 0 and 1 for everything else
     *
     * @param theInt the int to be evaluated
     * @return boolean value of the int
     */
    boolean intBitValue(int theInt) {
        return !(theInt == 0);
    }

    /**
     * Returns a long bit mask with a single 1 at the {@code bitIndex} index
     *
     * @param bitIndex index of the 1 bit
     * @return bit mask with only one bit set
     */
    long getBitMask(int bitIndex) {
        return 1L << (BITS_PER_LONG - 1 - bitIndex);
    }

    /**
     * Returns the integer value of the bit in the long (0 or 1)
     *
     * @param theLong  the long
     * @param bitIndex index of the bit in the long
     * @return int value of the bit
     */
    int getBitInLong(long theLong, int bitIndex) {
        // position the bit at the rightmost part and extract it
        return (int) (theLong >> (BITS_PER_LONG - 1 - bitIndex)) & 1;
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
        long rightSide = (theLong << rightSideStart) >>> rightSideStart;
        long leftSide = theLong - rightSide;
        return new long[]{leftSide, rightSide};
    }
}

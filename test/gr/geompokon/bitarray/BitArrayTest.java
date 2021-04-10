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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitArrayTest {

    final static int MAX_TEST_SIZE = 500;

    static BitArray bitArray;
    static ArrayList<Boolean> boolArray;
    static Random random;

    @BeforeEach
    void setUp() {
        bitArray = new BitArray();
        boolArray = new ArrayList<>();
        random = new Random();
    }

    /**
     * Clears both arrays and adds the same {@code noOfElements} random booleans to both. That is, a call to
     * boolArray.equals(bitArray) is always successful afterwards
     *
     * @param noOfElements number of random booleans to be added
     */
    void initArrays(int noOfElements) {
        bitArray.clear();
        boolArray.clear();
        for (int i = 0; i < noOfElements; i++) {
            boolean element = random.nextBoolean();
            bitArray.add(element);
            boolArray.add(element);
        }
    }

    /**
     * Asserts that the two arrays have the exact same contents
     */
    void myAssertSameArrays() {
        assertEquals(boolArray, bitArray);
    }

    @Test
    void testClone() {
        initArrays(MAX_TEST_SIZE);
        BitArray clone = (BitArray) bitArray.clone();

        assertEquals(clone, bitArray);
        assertEquals(bitArray, clone);
    }
}
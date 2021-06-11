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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Random;
import java.util.UnknownFormatConversionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class BitArrayTest {

    static BitArray bitArray;
    static ArrayList<Boolean> boolArray;
    static Random random;

    /**
     * Start each test with a fresh array
     */
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
     * Asserts that the two lists have the same exact contents
     */
    void myAssertSameArrays() {
        for (int i = 0; i < boolArray.size(); i++) {
            assertEquals(boolArray.get(i), bitArray.get(i));
        }
    }

    /**
     * Make the bit array from the boolean array
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Copy constructor result should have same size and elements")
    void testCopyConstructor(int elementsToAdd) {
        initArrays(elementsToAdd);
        bitArray = new BitArray(boolArray);

        myAssertSameArrays();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Result of clone() should have the same elements")
    void testClone(int elementsToAdd) {
        initArrays(elementsToAdd);
        BitArray clone = bitArray.clone();

        assertEquals(bitArray, clone);
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Serialized and immediately deserialized array should be the same as original")
    void testToFromString(int elementsToAdd) {
        // test array with elements
        initArrays(elementsToAdd);
        BitArray copy = BitArray.fromString(bitArray.toString());

        assertEquals(bitArray, copy);
    }

    @ParameterizedTest
    @ValueSource(strings = {"[0 1]", "Size = 2, [true true]", "Size =z, [0 1]", "Size = 3, [0 1]"})
    @DisplayName("Bad strings should throw specific exceptions")
    void testBadFromString(String faultyString) {
        try {
            BitArray.fromString(faultyString);
        } catch (Exception e) {
            if (!(e instanceof UnknownFormatConversionException
                    || e instanceof IndexOutOfBoundsException
                    || e instanceof NumberFormatException)) {
                fail(e);
            }
        }
    }
}
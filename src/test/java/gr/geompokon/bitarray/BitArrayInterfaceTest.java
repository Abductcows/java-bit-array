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
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayInterfaceTest {

    static List<Boolean> bitArray;
    static List<Boolean> boolArray;
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
     * Clears both lists and adds the same {@code noOfElements} random booleans to both. That is, a call to
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
     * Random insertions at random indices
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Lists should be the same when doing the same insertions")
    void addAtIndex(int elementsToAdd) {
        for (int i = 0; i < elementsToAdd; i++) {
            int index = random.nextInt(bitArray.size() + 1); // bound is exclusive
            boolean element = random.nextBoolean();
            bitArray.add(index, element);
            boolArray.add(index, element);
        }
        myAssertSameArrays();
    }

    /**
     * Modification of elements at random indices
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Lists should be the same when doing the same set operations")
    void set(int elementsToAdd) {
        // test empty array behaviour
        assertThrows(IndexOutOfBoundsException.class, () -> bitArray.set(0, true));

        // test with elements
        initArrays(elementsToAdd);

        for (int i = 0; i < elementsToAdd; i++) {
            int index = random.nextInt(bitArray.size());
            Boolean negatedElement = !bitArray.get(index); // to ensure contents change
            bitArray.set(index, negatedElement);
            boolArray.set(index, negatedElement);
        }

        myAssertSameArrays();
    }

    /**
     * Remove of elements at random indices
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Lists should be the same at any point while doing the same removes")
    void remove(int elementsToAdd) {
        // test empty array behaviour
        assertThrows(IndexOutOfBoundsException.class, () -> bitArray.remove(0));

        // test with elements
        initArrays(elementsToAdd);
        for (int i = 0; i < elementsToAdd; i++) {
            int index = random.nextInt(boolArray.size());
            assertEquals(boolArray.remove(index), bitArray.remove(index));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Cleared array should be empty")
    void clear(int elementsToAdd) {
        initArrays(elementsToAdd);
        bitArray.clear();
        assertTrue(bitArray.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 50, 100})
    @DisplayName("Number of elements should be the same as the number of insertions on new arrays")
    void size(int elementsToAdd) {
        initArrays(elementsToAdd);
        assertEquals(elementsToAdd, bitArray.size());
    }
}

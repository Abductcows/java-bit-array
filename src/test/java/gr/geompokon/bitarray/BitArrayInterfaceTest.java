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
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayInterfaceTest {

    final static int MAX_TEST_SIZE = 200;

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
    @Test
    void addAtIndex() {
        for (int i = 0; i < MAX_TEST_SIZE; i++) {
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
    @Test
    void set() {
        // test empty array behaviour
        assertThrows(Exception.class, () -> bitArray.set(0, true));

        // test with elements
        initArrays(MAX_TEST_SIZE);

        for (int i = 0; i < MAX_TEST_SIZE; i++) {
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
    @Test
    void remove() {
        // test empty array behaviour
        assertThrows(Exception.class, () -> bitArray.remove(0));

        // test with elements
        initArrays(MAX_TEST_SIZE);

        for (int i = 0; i < MAX_TEST_SIZE && !bitArray.isEmpty(); i++) {
            int index = random.nextInt(bitArray.size());
            bitArray.remove(index);
            boolArray.remove(index);
        }
        myAssertSameArrays();

        while (!bitArray.isEmpty()) {
            bitArray.remove(bitArray.size() - 1);
            boolArray.remove(bitArray.size() - 1);
        }
        myAssertSameArrays();
    }

    @Test
    void clear() {
        initArrays(10);
        bitArray.clear();
        assertTrue(bitArray.isEmpty());
    }

    @Test
    void size() {
        // test newly created array size
        assertEquals(0, bitArray.size());

        // add some elements
        initArrays(MAX_TEST_SIZE);
        int expectedSize = MAX_TEST_SIZE;
        assertEquals(expectedSize, bitArray.size());

        // remove some
        int noToRemove = MAX_TEST_SIZE / 2;
        bitArray.subList(0, noToRemove).clear();

        expectedSize -= noToRemove;
        assertEquals(expectedSize, bitArray.size());

        // add back some
        int noToAdd = MAX_TEST_SIZE / 2;
        for (int i = 0; i < noToAdd; i++) {
            bitArray.add(random.nextBoolean());
        }
        expectedSize += noToAdd;
        assertEquals(expectedSize, bitArray.size());
    }
}

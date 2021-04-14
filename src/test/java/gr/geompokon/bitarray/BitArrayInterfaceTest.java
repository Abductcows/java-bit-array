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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitArrayInterfaceTest {

    final static int MAX_TEST_SIZE = 500;

    static List<Boolean> bitArray;
    static List<Boolean> boolArray;
    static Random random;

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
        assertEquals(boolArray, bitArray);
    }

    @Test
    void addAtIndex() {
        for (int i = 0; i < MAX_TEST_SIZE; i++) {
            int index = random.nextInt(bitArray.size() + 1);
            boolean element = random.nextBoolean();
            bitArray.add(index, element);
            boolArray.add(index, element);
        }
        myAssertSameArrays();
    }

    @Test
    void set() {
        initArrays(MAX_TEST_SIZE);

        for (int i = 0; i < MAX_TEST_SIZE; i++) {
            int index = random.nextInt(bitArray.size());
            Boolean negatedElement = !bitArray.get(index);
            bitArray.set(index, negatedElement);
            boolArray.set(index, negatedElement);
        }

        myAssertSameArrays();
    }

    @Test
    void remove() {
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
        initArrays(MAX_TEST_SIZE);
        int expectedSize = MAX_TEST_SIZE;
        assertEquals(expectedSize, bitArray.size());

        int noToRemove = MAX_TEST_SIZE / 2;
        bitArray.subList(0, noToRemove).clear();

        expectedSize -= noToRemove;
        assertEquals(expectedSize, bitArray.size());

        int noToAdd = 100;
        for (int i = 0; i < noToAdd; i++) {
            bitArray.add(random.nextBoolean());
        }
        expectedSize += noToAdd;
        assertEquals(expectedSize, bitArray.size());
    }
}

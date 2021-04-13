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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Hope you have about 8GB of memory lying around
 */
public class BitArrayVsArrayListBenchmarkTest {
    static long testSeed;
    static long startTime, duration;
    static List<Boolean> bitArray = new BitArray();
    static List<Boolean> boolArray = new ArrayList<>();

    static Random rand = new Random();
    static final int RANDOM_ADD_SIZE = 1_000_000;
    static final int APPEND_SIZE = 250_000_000;
    static final int RANDOM_REMOVE_SIZE = 1_000_000; // dont try anything more than 10m with arraylist


    @BeforeEach
    void setUp() {
        // Set the seed to be the same for both arrays
        testSeed = System.nanoTime();
    }

    @AfterEach
    void tearDown() {
        // clean up for the next test
        bitArray.clear();
        boolArray.clear();
    }

    /**
     * Adds the same {@code numberDesired} number of elements to both arrays
     */
    void populateArrays(int numberDesired) {
        rand = new Random(System.nanoTime());
        for (int i = 0; i < numberDesired; i++) {
            boolean nextElement = rand.nextBoolean();
            bitArray.add(nextElement);
            boolArray.add(nextElement);
        }
    }

    void setUpBitArray() {
        rand = new Random(testSeed);
        System.out.print("BitArray: ");
        startTime = System.nanoTime();
    }

    void setUpBoolArray() {
        rand = new Random(testSeed);
        System.out.print("BoolArray: ");
        startTime = System.nanoTime();
    }

    void stopTimerAndPrint() {
        duration = System.nanoTime() - startTime;
        System.out.printf("%d ns | %.2f seconds\n",
                duration, duration / 1_000_000_000.0);
    }

    @Test
    void doRandomAdd() {
        System.out.println("Random insertion of " + RANDOM_ADD_SIZE + " random elements");

        // BitArray
        setUpBitArray();
        for (int i = 0; i < RANDOM_ADD_SIZE; i++) {
            int nextIndex = rand.nextInt(bitArray.size() + 1);
            bitArray.add(nextIndex, rand.nextBoolean());
        }
        stopTimerAndPrint();

        // ArrayList<Boolean>
        setUpBoolArray();
        for (int i = 0; i < RANDOM_ADD_SIZE; i++) {
            int nextIndex = rand.nextInt(boolArray.size() + 1); // different size() methods called in the two loops, expect some error
            boolArray.add(nextIndex, rand.nextBoolean());
        }
        stopTimerAndPrint();
        assertEquals(RANDOM_ADD_SIZE, bitArray.size());
        assertEquals(RANDOM_ADD_SIZE, boolArray.size());
    }

    @Test
    void doAppend() {
        System.out.println("Append of " + APPEND_SIZE + " random elements");

        // BitArray
        setUpBitArray();
        for (int i = 0; i < APPEND_SIZE; i++) {
            bitArray.add(i, rand.nextBoolean());
        }
        stopTimerAndPrint();

        // ArrayList<Boolean>
        setUpBoolArray();
        for (int i = 0; i < APPEND_SIZE; i++) {
            boolArray.add(i, rand.nextBoolean());
        }
        stopTimerAndPrint();
        assertEquals(APPEND_SIZE, bitArray.size());
        assertEquals(APPEND_SIZE, boolArray.size());
    }

    @Test
    void doRandomGet() {
        // add some elements for the gets
        System.out.println("Populating both arrays with " + APPEND_SIZE + " elements..");
        populateArrays(APPEND_SIZE);
        assertEquals(bitArray.size(), boolArray.size());
        System.out.println("Get of " + APPEND_SIZE + " random elements");

        int arraySize = bitArray.size(); // save size since we are not updating number of elements

        // BitArray
        setUpBitArray();
        for (int i = 0; i < APPEND_SIZE; i++) {
            int getIndex = rand.nextInt(arraySize);
            bitArray.get(getIndex);
        }
        stopTimerAndPrint();
        bitArray.clear(); // clear to free memory

        // ArrayList<Boolean>
        setUpBoolArray();
        for (int i = 0; i < APPEND_SIZE; i++) {
            int getIndex = rand.nextInt(arraySize);
            boolArray.get(getIndex);
        }
        stopTimerAndPrint();
    }

    @Test
    void doRandomSet() {
        System.out.println("Populating both arrays with " + APPEND_SIZE + " elements..");
        populateArrays(APPEND_SIZE);
        assertEquals(bitArray.size(), boolArray.size());
        System.out.println("Set of " + APPEND_SIZE + " random elements with random values");

        int arraySize = bitArray.size();

        // BitArray
        setUpBitArray();
        for (int i = 0; i < APPEND_SIZE; i++) {
            int nextIndex = rand.nextInt(arraySize);
            boolean nextElement = rand.nextBoolean();
            bitArray.set(nextIndex, nextElement);
        }
        stopTimerAndPrint();
        bitArray.clear(); // clear for some much needed memory

        // ArrayList<Boolean>
        setUpBoolArray();
        for (int i = 0; i < APPEND_SIZE; i++) {
            int nextIndex = rand.nextInt(arraySize);
            boolean nextElement = rand.nextBoolean();
            boolArray.set(nextIndex, nextElement);
        }
        stopTimerAndPrint();
    }

    @Test
    void doRandomRemove() {
        System.out.println("Populating both arrays with " + RANDOM_REMOVE_SIZE + " elements..");
        populateArrays(RANDOM_REMOVE_SIZE);
        assertEquals(bitArray.size(), boolArray.size());
        System.out.println("Remove of " + RANDOM_REMOVE_SIZE + " random elements with random values");

        // BitArray
        setUpBitArray();
        for (int i = 0; i < RANDOM_REMOVE_SIZE; i++) {
            int removeIndex = rand.nextInt(bitArray.size());
            bitArray.remove(removeIndex);
        }
        stopTimerAndPrint();

        // ArrayList<Boolean>
        setUpBoolArray();
        for (int i = 0; i < RANDOM_REMOVE_SIZE; i++) {
            int removeIndex = rand.nextInt(boolArray.size());
            boolArray.remove(removeIndex);
        }
        stopTimerAndPrint();
        assertEquals(0, bitArray.size());
        assertEquals(0, boolArray.size());
    }

}

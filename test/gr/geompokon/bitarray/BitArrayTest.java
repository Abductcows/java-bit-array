package gr.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitArrayTest {

    final static int MAX_TEST_SIZE = 500;

    static AbstractList<Boolean> bitArray;
    static AbstractList<Boolean> boolArray;
    static Random random;

    @BeforeEach
    void setUp() {
        bitArray = new BitArray(MAX_TEST_SIZE);
        boolArray = new ArrayList<>(MAX_TEST_SIZE);
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

        for (int i = 0; i < MAX_TEST_SIZE; i++) {
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
    }
}
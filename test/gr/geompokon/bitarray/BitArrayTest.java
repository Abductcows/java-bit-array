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
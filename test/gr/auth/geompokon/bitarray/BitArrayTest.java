package gr.auth.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayTest {

    static BitArray array;

    @BeforeEach
    void setUp() {
        array = new BitArray();
    }



    @Test
    void testAppendAndGet() {

        final int TEST_SIZE = 9000;
        Random random = new Random();
        int[] cachedInsertions = new int[TEST_SIZE];

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2); // exclusive
            cachedInsertions[i] = nextBit;
            array.add(nextBit);
        }

        // check data integrity
        for (int i=0; i<TEST_SIZE; i++) {
            assertEquals(cachedInsertions[i], array.get(i));
        }
        assertEquals(TEST_SIZE, array.size());
    }

    @Test
    void testSet() {
        final int TEST_SIZE = 9000;
        Random random = new Random();

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2); // exclusive
            array.add(0, nextBit);
        }

        for (int i=0; i<TEST_SIZE; i++) {
            // get the current bit
            int currentBit = array.get(i);
            // set the current bit to its opposite and check if it has been set
            array.set(i, (Math.abs(currentBit-1)));

            assertEquals(1, currentBit + array.get(i));
        }

        assertEquals(TEST_SIZE, array.size());
    }

    @Test
    void testInsert0AndGet() {

        final int TEST_SIZE = 9000;
        Random random = new Random();
        Stack<Integer> cachedInsertions = new Stack<>();

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2); // exclusive
            cachedInsertions.push(nextBit);
            array.add(0, nextBit);
        }

        // check data integrity
        for (int i=0; i<TEST_SIZE; i++) {
            assertEquals(cachedInsertions.pop(), array.get(i));
        }
        assertEquals(TEST_SIZE, array.size());
    }

}
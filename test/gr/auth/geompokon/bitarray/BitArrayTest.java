package gr.auth.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayTest {

    static BitArray array;
    static Random random;

    @BeforeEach
    void setUp() {
        array = new BitArray();
        random = new Random();
    }


    @Test
    void testInsert0AndGet() {

        final int TEST_SIZE = 9000;
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

    @Test
    void testRandomInsertAndGet() {

        final int TEST_SIZE = 9000;
        ArrayList<Integer> cachedInsertions = new ArrayList<>(TEST_SIZE + 1);

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2); // exclusive
            int nextIndex = random.nextInt(array.size()+1);
            cachedInsertions.add(nextIndex, nextBit);
            array.add(nextIndex, nextBit);
        }

        // check data integrity
        for (int i=0; i<TEST_SIZE; i++) {
            assertEquals(cachedInsertions.get(i), array.get(i));
        }
        assertEquals(TEST_SIZE, array.size());
    }

    /*@Test
    void testSet() {
        final int TEST_SIZE = 9000;

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
    }*/

    @Test
    void testSizeResize() {

        int TEST_SIZE = 9000;

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2);
            array.add(nextBit);
        }

        assertEquals(TEST_SIZE, array.size());
        array.resize(array.size());
        assertEquals(TEST_SIZE, array.size()); // size should be equal to # of insertions

        int greaterSize = 2 * TEST_SIZE;
        array.resize(greaterSize);
        assertEquals(TEST_SIZE, array.size()); // size should remain unchanged

        int lesserSize = TEST_SIZE / 2;
        array.resize(lesserSize);
        assertEquals(lesserSize, array.size()); // size should be truncated
    }

    @Test
    void testPopRemove() {

        int TEST_SIZE = 9000;

        ArrayList<Boolean> cache = new ArrayList<>(TEST_SIZE);

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2);
            array.add(nextBit);
            cache.add(nextBit == 1);
        }
        assertEquals(cache.size(), array.size());

        while (!array.isEmpty()) {
            int popValue = array.remove(array.size()-1);
            boolean cacheValue = cache.remove(cache.size()-1);
            assertEquals(cacheValue, popValue == 1);
        }
    }

    @Test
    void testRandomRemove() {

        int TEST_SIZE = 9000;

        ArrayList<Boolean> cache = new ArrayList<>(TEST_SIZE);

        for (int i=0; i<TEST_SIZE; i++) {
            int nextBit = random.nextInt(2);
            array.add(nextBit);
            cache.add(nextBit == 1);
        }
        assertEquals(cache.size(), array.size());

        while (!array.isEmpty()) {
            int nextRemoveIndex = random.nextInt(array.size());
            int popValue = array.remove(nextRemoveIndex);
            boolean cacheValue = cache.remove(nextRemoveIndex);
            assertEquals(cacheValue, popValue == 1);
        }
    }

}
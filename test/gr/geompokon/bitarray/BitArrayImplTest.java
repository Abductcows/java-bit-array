package gr.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayImplTest {

    static BitArrayImpl array;
    static Random random;
    static int TEST_SIZE;

    @BeforeEach
    void setUp() {
        TEST_SIZE = 9000;
        array = new BitArrayImpl();
        random = new Random();
    }


    @Test
    void testInsert0AndGet() {
        Stack<Boolean> cachedInsertions = new Stack<>();

        for (int i=0; i<TEST_SIZE; i++) {
            boolean nextBit = random.nextBoolean();
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
        ArrayList<Boolean> cachedInsertions = new ArrayList<>(TEST_SIZE + 1);

        for (int i=0; i<TEST_SIZE; i++) {
            boolean nextBit = random.nextBoolean();
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

    @Test
    void testSizeResize() {

        int TEST_SIZE = 9000;

        for (int i=0; i<TEST_SIZE; i++) {
            boolean nextBit = random.nextBoolean();
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
        Stack<Boolean> cache = new Stack<>();

        for (int i=0; i<TEST_SIZE; i++) {
            boolean nextBit = random.nextBoolean();
            array.add(nextBit);
            cache.push(nextBit);
        }
        assertEquals(cache.size(), array.size());

        while (!array.isEmpty()) {
            boolean arrayPop = array.remove(array.size()-1);
            boolean cachePop = cache.pop();
            assertEquals(cachePop, arrayPop);
        }
    }

    @Test
    void testRandomRemove() {
        ArrayList<Boolean> cache = new ArrayList<>(TEST_SIZE);

        for (int i=0; i<TEST_SIZE; i++) {
            boolean nextBit = random.nextBoolean();
            array.add(nextBit);
            cache.add(nextBit);
        }
        assertEquals(cache.size(), array.size());

        while (!array.isEmpty()) {
            int nextRemoveIndex = random.nextInt(array.size());
            boolean arrayRemove = array.remove(nextRemoveIndex);
            boolean cacheRemove = cache.remove(nextRemoveIndex);
            assertEquals(cacheRemove, arrayRemove);
        }
    }
}
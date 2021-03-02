package gr.auth.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayTest {

    static BitArray array;

    @BeforeEach
    void setUp() {
        array = new BitArray();
    }



    @Test
    void testAddAndGet() {

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
    }

}
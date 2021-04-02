package gr.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayTest {

    final static int TEST_SIZE = 9000;

    static BitArray bitArray;
    static ArrayList<Boolean> boolArray;
    Random rand;

    @BeforeEach
    void setUp() {
        bitArray = new BitArray(TEST_SIZE);
        boolArray = new ArrayList<>(TEST_SIZE);
        rand = new Random();
    }

    void populateArrays(int noOfElements) {
        for (int i = 0; i < TEST_SIZE; i++) {
            boolean element = rand.nextBoolean();
            bitArray.add(element);
            boolArray.add(element);
        }
    }


    @Test
    void addGet() {
        populateArrays(TEST_SIZE);
        for (int i = 0; i < TEST_SIZE; i++) {
            assertEquals(boolArray.get(i), bitArray.get(i));
        }
    }

    @Test
    void set() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void remove() {
    }

    @Test
    void indexOf() {
    }

    @Test
    void lastIndexOf() {
    }

    @Test
    void clear() {
    }

    @Test
    void addAll() {
    }

    @Test
    void iterator() {
    }

    @Test
    void listIterator() {
    }

    @Test
    void testListIterator() {
    }

    @Test
    void subList() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void removeRange() {
    }

    @Test
    void isEmpty() {
    }

    @Test
    void contains() {
    }

    @Test
    void toArray() {
    }

    @Test
    void testToArray() {
    }

    @Test
    void testRemove() {
    }

    @Test
    void containsAll() {
    }

    @Test
    void testAddAll() {
    }

    @Test
    void removeAll() {
    }

    @Test
    void retainAll() {
    }

    @Test
    void replaceAll() {
    }

    @Test
    void sort() {
    }

    @Test
    void spliterator() {
    }

    @Test
    void testToArray1() {
    }

    @Test
    void removeIf() {
    }

    @Test
    void stream() {
    }

    @Test
    void parallelStream() {
    }

    @Test
    void size() {
        populateArrays(TEST_SIZE);
        int expectedSize = TEST_SIZE;
        assertEquals(expectedSize, bitArray.size());

        int noToRemove = 10;
        for (int i=0; i<noToRemove; i++) {
            bitArray.remove(0);
        }
        expectedSize -= noToRemove;
        assertEquals(expectedSize, bitArray.size());

        int noToAdd = 100;
        for (int i=0; i<noToAdd; i++) {
            bitArray.add(rand.nextBoolean());
        }
    }

    @Test
    void forEach() {
        populateArrays(TEST_SIZE);

        int i = 0;
        for (boolean bitElement : bitArray) {
            boolean boolElement = boolArray.get(i);
            assertEquals(boolElement, bitElement);
            ++i;
        }

        AtomicReference<Integer> j = new AtomicReference<>(0);
        bitArray.forEach(bitElement -> {
            boolean boolElement = boolArray.get(j.get());
            assertEquals(bitElement, boolElement);
            j.set(j.get() + 1);
        });
    }
}
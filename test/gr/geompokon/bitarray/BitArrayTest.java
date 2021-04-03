package gr.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    void initArrays(int noOfElements) {
        bitArray = new BitArray(noOfElements);
        boolArray = new ArrayList<>(noOfElements);
        for (int i = 0; i < noOfElements; i++) {
            boolean element = rand.nextBoolean();
            bitArray.add(element);
            boolArray.add(element);
        }
    }

    void myAssertSameArrays() {
        assertEquals(bitArray.size(), boolArray.size());
        for (int i = 0; i < bitArray.size(); i++) {
            assertEquals(bitArray.get(i), boolArray.get(i));
        }
    }


    @Test
    void addGet() {
        initArrays(TEST_SIZE);
        myAssertSameArrays();
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
    void listIteratorNextPrev() {
        bitArray.add(0, true);
        bitArray.add(1, true);
        bitArray.add(2, false);
        ListIterator<Boolean> it = bitArray.listIterator();

        // previous fail
        assertFalse(it.hasPrevious());
        assertThrows(NoSuchElementException.class, it::previous);

        // next success
        assertTrue(it.hasNext());
        assertDoesNotThrow(it::next);

        // move to the end of the list
        assertTrue(it.hasNext());
        assertDoesNotThrow(it::next);
        assertTrue(it.hasNext());
        assertDoesNotThrow(it::next);

        // next fail
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);

        // previous success
        assertTrue(it.hasPrevious());
        assertDoesNotThrow(it::previous);
    }

    @Test
    void listIteratorNextPrevIndices() {
        Runnable test = () -> {
            ListIterator<Boolean> it = bitArray.listIterator();
            int i = 0;
            while (it.hasNext()) {
                assertEquals(i, it.nextIndex());
                assertEquals(i - 1, it.previousIndex());
                i = i + 1;
                it.next();
            }
        };

        // test with empty array
        test.run();

        // test with elements
        initArrays(30);
        test.run();
    }

    @Test
    void listIteratorRemove() {
        initArrays(10);
        ListIterator<Boolean> bitIt = bitArray.listIterator();

        // plain remove should fail
        assertThrows(IllegalStateException.class, bitIt::remove);
        // remove next
        bitIt.next();
        assertDoesNotThrow(bitIt::remove);
        assertThrows(IllegalStateException.class, bitIt::remove);
        // move forward and remove previous
        bitIt.next();
        bitIt.previous();
        assertDoesNotThrow(bitIt::remove);
        assertThrows(IllegalStateException.class, bitIt::remove);

        // remove all elements with next
        initArrays(100);
        bitIt = bitArray.listIterator();
        while (bitIt.hasNext()) {
            bitIt.next();
            bitIt.remove();
        }
        assertEquals(0, bitArray.size());

        // remove all elements with previous
        initArrays(100);
        bitIt = bitArray.listIterator();
        while (bitIt.hasNext()) {
            bitIt.next();
        }
        while (bitIt.hasPrevious()) {
            bitIt.previous();
            bitIt.remove();
        }
        assertEquals(0, bitArray.size());
    }

    @Test
    void listIteratorSet() {
        initArrays(30);
        ListIterator<Boolean> bitIt = bitArray.listIterator();
        ListIterator<Boolean> boolIt = boolArray.listIterator();

        // set with next
        boolean value = !bitIt.next();
        boolIt.next();
        bitIt.set(value);
        boolIt.set(value);
        myAssertSameArrays();

        // set with previous
        bitIt.next();
        boolIt.next();
        value = !bitIt.previous();
        boolIt.previous();
        bitIt.set(value);
        boolIt.set(value);
        myAssertSameArrays();

        // negate entire array with set
        initArrays(100);

        bitIt = bitArray.listIterator();
        while (bitIt.hasNext()) {
            boolean negatedValue = !bitIt.next();
            bitIt.set(negatedValue);
        }

        boolArray = boolArray.stream().map(b -> !b).collect(Collectors.toCollection(ArrayList::new));
        myAssertSameArrays();
    }

    void listIteratorAdd() {
        initArrays(100);
        // clear bitArray
        bitArray = new BitArray();
        // TODO
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
        initArrays(TEST_SIZE);
        int expectedSize = TEST_SIZE;
        assertEquals(expectedSize, bitArray.size());

        int noToRemove = 10;
        for (int i = 0; i < noToRemove; i++) {
            bitArray.remove(0);
        }
        expectedSize -= noToRemove;
        assertEquals(expectedSize, bitArray.size());

        int noToAdd = 100;
        for (int i = 0; i < noToAdd; i++) {
            bitArray.add(rand.nextBoolean());
        }
    }

    @Test
    void forEach() {
        initArrays(TEST_SIZE);

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
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

    final static int TEST_SIZE = 2000;

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
    void add() {
        initArrays(TEST_SIZE);
        myAssertSameArrays();
    }

    @Test
    void addIndex() {
        initArrays(10);

        for (int i = 0; i < TEST_SIZE; i++) {
            int index = rand.nextInt(bitArray.size());
            Boolean negatedElement = !bitArray.get(index);
            bitArray.add(index, negatedElement);
            boolArray.add(index, negatedElement);
        }

        myAssertSameArrays();
    }

    @Test
    void set() {
        initArrays(TEST_SIZE);

        for (int i = 0; i < TEST_SIZE; i++) {
            int index = rand.nextInt(bitArray.size());
            Boolean negatedElement = !bitArray.get(index);
            bitArray.set(index, negatedElement);
            boolArray.set(index, negatedElement);
        }

        myAssertSameArrays();
    }

    @Test
    void remove() {
        initArrays(TEST_SIZE);

        for (int i = 0; i < TEST_SIZE; i++) {
            int index = rand.nextInt(bitArray.size());
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
    void indexOf() {
        for (int i = 0; i < 10; i++) {
            initArrays(10);
            assertEquals(boolArray.indexOf(Boolean.FALSE), bitArray.indexOf(Boolean.FALSE));
            assertEquals(boolArray.indexOf(Boolean.TRUE), bitArray.indexOf(Boolean.TRUE));
        }
        assertEquals(-1, bitArray.indexOf("not here"));
    }

    @Test
    void lastIndexOf() {
        for (int i = 0; i < 10; i++) {
            initArrays(10);
            assertEquals(boolArray.lastIndexOf(Boolean.FALSE), bitArray.lastIndexOf(Boolean.FALSE));
            assertEquals(boolArray.lastIndexOf(Boolean.TRUE), bitArray.lastIndexOf(Boolean.TRUE));
        }
        assertEquals(-1, bitArray.indexOf("not here"));
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
        // also check if it is the same as the ArrayList for every remove
        ListIterator<Boolean> boolIt = boolArray.listIterator();
        while (bitIt.hasNext()) {
            bitIt.next();
            boolIt.next();
            bitIt.remove();
            boolIt.remove();
            myAssertSameArrays();
        }
        assertEquals(0, bitArray.size());

        // remove all elements with previous
        initArrays(100);
        bitIt = bitArray.listIterator();
        boolIt = boolArray.listIterator();
        while (bitIt.hasNext()) {
            bitIt.next();
            boolIt.next();
        }
        while (bitIt.hasPrevious()) {
            bitIt.previous();
            boolIt.previous();
            bitIt.remove();
            boolIt.remove();
            myAssertSameArrays();
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

        // negate bool array
        boolArray = boolArray.stream().map(b -> !b).collect(Collectors.toCollection(ArrayList::new));
        myAssertSameArrays();
    }

    @Test
    void listIteratorAdd() {
        initArrays(100);
        // clear bitArray and add every element to the bitArray using iterator
        bitArray = new BitArray();
        ListIterator<Boolean> it = bitArray.listIterator();

        for (Boolean aBoolean : boolArray) {
            it.add(aBoolean);
        }
        myAssertSameArrays();

        // test if previous returns the newly added element
        initArrays(0);
        assertEquals(0, bitArray.size());
        it = bitArray.listIterator();

        for (int i = 0; i < 100; i++) {
            Boolean element = rand.nextBoolean();
            it.add(element);
            assertEquals(element, it.previous());
        }

        // test that add does not move the cursor
        initArrays(10);
        it = bitArray.listIterator();

        for (int i = 0; i < 100; i++) {
            Boolean nextElement = it.next();
            it.previous();
            Boolean toAdd = !nextElement; // to make sure it is different
            it.add(toAdd);
            assertEquals(nextElement, it.next());
            it.previous();
        }
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
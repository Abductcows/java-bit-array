package gr.geompokon.bitarray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitArrayImplTest {

    static final int MAX_TEST_SIZE = 500;

    static BitArrayImpl myArray;
    static ArrayList<Boolean> arrayList;
    static Random random;

    @BeforeEach
    void setUp() {
        myArray = new BitArrayImpl();
        arrayList = new ArrayList<>();
        random = new Random();
    }

    void initArrays(int noOfElements) {
        myArray = new BitArrayImpl();
        arrayList.clear();
        for (int i = 0; i < noOfElements; i++) {
            boolean element = random.nextBoolean();
            myArray.add(element);
            arrayList.add(element);
        }
    }

    void myAssertSameArrays() {
        assertEquals(arrayList.size(), myArray.size());
        for (int i = 0; i < myArray.size(); i++) {
            assertEquals(arrayList.get(i), myArray.get(i));
        }
    }


    @Test
    void addGet() {
        initArrays(MAX_TEST_SIZE);
        myAssertSameArrays();
    }

    @Test
    void set() {
        initArrays(MAX_TEST_SIZE);
        for (int i = 0; i < myArray.size(); i++) {
            boolean negatedElement = !myArray.get(i);
            myArray.set(i, negatedElement);
        }
        arrayList = arrayList.stream().map(b -> !b).collect(Collectors.toCollection(ArrayList::new));
        myAssertSameArrays();
    }

    @Test
    void remove() {
        initArrays(MAX_TEST_SIZE);
        for (int i = 0; i < MAX_TEST_SIZE && !myArray.isEmpty(); i++) {
            int removeIndex = random.nextInt(myArray.size());
            myArray.remove(removeIndex);
            arrayList.remove(removeIndex);
        }
        myAssertSameArrays();
    }

    @Test
    void size() {
        assertEquals(0, myArray.size());
        initArrays(MAX_TEST_SIZE);
        assertEquals(MAX_TEST_SIZE, myArray.size());
        myArray.remove(0);
        myArray.remove(0);
        assertEquals(MAX_TEST_SIZE - 2, myArray.size());
    }

    @Test
    void clear() {
        initArrays(10);
        myArray.clear();
        assertEquals(0, myArray.size());
    }
}
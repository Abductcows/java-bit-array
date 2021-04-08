/*
 Copyright 2021 George Bouroutzoglou

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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
            myArray.add(myArray.size(), element);
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
        for (int i = 0; i < MAX_TEST_SIZE && !(myArray.size() == 0); i++) {
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

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

package io.github.abductcows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BitArray Unit Tests")
class BitArrayTest {

    BitArray bitArray;

    @BeforeEach
    void setUp() {
        bitArray = new BitArray();
    }


    @Nested
    @DisplayName("CRUD Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CRUDTest {

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Add at index should behave like ArrayList")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void add_at_index_test(List<Boolean> elementsToAdd) {

            // given
            List<Boolean> authority = new ArrayList<>(elementsToAdd.size());
            List<Integer> addIndices = TestUtils.getAddIndices(elementsToAdd.size());

            // when
            for (int i = 0; i < elementsToAdd.size(); i++) {
                bitArray.add(addIndices.get(i), elementsToAdd.get(i));
                authority.add(addIndices.get(i), elementsToAdd.get(i));
            }

            // then
            assertThat(bitArray).isEqualTo(authority);
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Add at the end should behave like ArrayList")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void add_test(List<Boolean> elementsToAdd) {

            // given
            List<Boolean> authority = new ArrayList<>(elementsToAdd.size());

            // when
            for (Boolean aBoolean : elementsToAdd) {
                bitArray.add(aBoolean);
                authority.add(aBoolean);
            }

            // then
            assertThat(bitArray).isEqualTo(authority);
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Set at index should behave like ArrayList")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void set_test(List<Boolean> elementsToAdd) {

            // given
            List<Boolean> authority = new ArrayList<>(elementsToAdd.size());
            List<Integer> setIndices = TestUtils.getSetIndices(elementsToAdd.size());
            bitArray.addAll(elementsToAdd);
            authority.addAll(elementsToAdd);

            // when/then
            for (int i : setIndices) {
                boolean nextSetValue = !authority.get(i);

                authority.set(i, nextSetValue);
                bitArray.set(i, nextSetValue);

                assertThat(bitArray.get(i)).isEqualTo(authority.get(i));
            }
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Remove at index should behave like ArrayList")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void remove_test(List<Boolean> elementsToRemove) {

            // given
            List<Boolean> authority = new ArrayList<>(elementsToRemove.size());
            List<Integer> removeIndices = TestUtils.getRemoveIndices(elementsToRemove.size());
            bitArray.addAll(elementsToRemove);
            authority.addAll(elementsToRemove);

            // when/then
            for (int i = 0; i < elementsToRemove.size(); i++) {
                int nextRemoveIndex = removeIndices.get(i);
                assertThat(bitArray.remove(nextRemoveIndex))
                        .isEqualTo(authority.remove(nextRemoveIndex));
            }

        }
    }


    @Nested
    @DisplayName("Copy Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CopyTests {

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of copy constructor should have the same elements")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void copy_constructor_returns_identical_list(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            BitArray copy = new BitArray(bitArray);

            // then
            assertThat(copy).isEqualTo(bitArray);
        }


        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of clone() should have the same elements")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void clone_returns_identical_list(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            BitArray copy = bitArray.clone();

            // then
            assertThat(copy).isEqualTo(bitArray);
        }


        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of clone should be a separate object")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void clone_returns_new_Object(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            BitArray copy = bitArray.clone();

            // then
            assertThat(copy).isNotSameAs(bitArray);
        }
    }


    @Nested
    @DisplayName("Serialization Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SerializationTests {

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Serialized and immediately deserialized array should be the same as original")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        void toString_and_fromString_do_not_alter_content(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            String serialized = bitArray.toString();
            BitArray deserialized = BitArray.fromString(serialized);

            // then
            assertThat(deserialized).isEqualTo(bitArray);
        }

        @ParameterizedTest
        @ValueSource(strings = {"[0 1]", "Size = 2, [true true]", "Size =z, [0 1]", "Size = 3, [0 1]"})
        @DisplayName("Bad strings should throw specific exceptions")
        void fromString_throws_on_bad_string(String faultyString) {
            // when/then
            assertThatThrownBy(() -> {
                BitArray impossibleList = BitArray.fromString(faultyString);
                impossibleList.add(Boolean.FALSE);
            }).isInstanceOf(UnknownFormatConversionException.class);
        }
    }


    @Nested
    @DisplayName("New Method Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NewMethodTests {

        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        @DisplayName("sumMod2 is equivalent to parity of 1s in the array")
        void sumMod2_works(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            int sumMod2 = bitArray.sumMod2();
            int expected = (int) bitArray.stream().filter(Boolean::booleanValue).count() % 2;

            // then
            printDetails(elementsToAdd.size(), expected, sumMod2);
            assertThat(sumMod2).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        @DisplayName("countOnes is equivalent to the number of true elements in the array")
        void countOnes_counts_ones(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            int ones = bitArray.countOnes();
            int expected = (int) bitArray.stream().filter(Boolean::booleanValue).count();

            // then
            printDetails(elementsToAdd.size(), expected, ones);
            assertThat(ones).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        @DisplayName("countZeros is equivalent to the number of false elements in the array")
        void countZeros_counts_zeros(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            int zeros = bitArray.countZeros();
            int expected = (int) bitArray.stream().filter(Boolean.FALSE::equals).count();

            // then
            printDetails(elementsToAdd.size(), expected, zeros);
            assertThat(zeros).isEqualTo(expected);
        }
    }


    @Nested
    @DisplayName("Misc Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MiscTests {

        @ParameterizedTest(name = "{0} elements before clear")
        @MethodSource("io.github.abductcows.TestUtils#testCaseBooleans")
        @DisplayName("Cleared array should be empty")
        void clear_leaves_empty_list(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            bitArray.clear();

            // then
            assertThat(bitArray).isEmpty();
        }
    }

    @Nested
    @DisplayName("AbstractList implementation overrides")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AbstractListOverridesTests {

        @SuppressWarnings("SuspiciousMethodCalls")
        @ParameterizedTest(name = "{0} elements")
        @MethodSource({"io.github.abductcows.TestUtils#testCaseBooleans", "io.github.abductcows.TestUtils#allSameBooleans"})
        @DisplayName("IndexOf should work like ArrayList's")
        void test_indexOf(List<Boolean> elementsToAdd) {
            // given
            List<Boolean> authority = new ArrayList<>(elementsToAdd);
            bitArray.addAll(elementsToAdd);

            // when
            int firstTrue = bitArray.indexOf(Boolean.TRUE), firstFalse = bitArray.indexOf(Boolean.FALSE);
            int expectedTrue = authority.indexOf(Boolean.TRUE), expectedFalse = authority.indexOf(Boolean.FALSE);
            int firstInvalid = bitArray.indexOf(BitArray.class);

            // then
            printDetails(elementsToAdd.size(), expectedTrue, firstTrue, "first true");
            printDetails(elementsToAdd.size(), expectedFalse, firstFalse, "first false");
            printDetails(elementsToAdd.size(), -1, firstInvalid, "first invalid");
            assertThat(firstTrue).isEqualTo(expectedTrue);
            assertThat(firstFalse).isEqualTo(expectedFalse);
            assertThat(firstInvalid).isEqualTo(-1);
        }
    }


    void printDetails(int testSize, Object expected, Object actual) {
        printDetails(testSize, expected, actual, "");
    }

    void printDetails(int testSize, Object expected, Object actual, String additionalNote) {
        if (!additionalNote.isEmpty()) {
            System.out.printf("%15s ", String.format("(%s)", additionalNote));
        }

        System.out.printf("Test size: %3d elements | Expected = %s, Actual = %s", testSize, expected, actual);

        if (bitArray.size() <= 10) {
            System.out.println(" | Array: " + bitArray);
        } else {
            System.out.println();
        }
    }
}
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

    static BitArray bitArray;

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
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
        void remove_test(List<Boolean> elementsToRemove) {

            // given
            List<Boolean> authority = new ArrayList<>(elementsToRemove.size());
            List<Integer> removeIndices = TestUtils.getRemoveIndices(elementsToRemove.size());
            System.out.println(removeIndices);
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
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
        void copy_constructor_returns_identical_list(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            BitArray copy = new BitArray(bitArray);

            // then
            assertThat(copy).isEqualTo(bitArray);
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of copy constructor should be a separate object")
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
        void copy_constructor_returns_new_Object(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            BitArray copy = new BitArray(bitArray);

            // then
            assertThat(copy == bitArray).isFalse();
        }


        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of clone() should have the same elements")
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
        void clone_returns_new_Object(List<Boolean> elementsToAdd) {
            // given
            bitArray.addAll(elementsToAdd);

            // when
            BitArray copy = bitArray.clone();

            // then
            assertThat(copy == bitArray).isFalse();
        }
    }


    @Nested
    @DisplayName("Serialization Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SerializationTests {

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Serialized and immediately deserialized array should be the same as original")
        @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
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
                System.out.println(impossibleList);
                impossibleList.add(Boolean.FALSE);
            }).isInstanceOf(UnknownFormatConversionException.class);
        }
    }


    @ParameterizedTest(name = "{0} elements before clear")
    @MethodSource("gr.geompokon.bitarray.TestUtils#testCaseBooleans")
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
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

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Random;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BitArray Unit Tests")
class BitArrayTest {

    static BitArray bitArray;

    static final int SEED = 101;
    static final Random consistentRandom = new Random(SEED);

    @BeforeEach
    void setUp() {
        bitArray = new BitArray();
    }


    @Nested
    @DisplayName("Copy tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CopyTests {

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of copy constructor should have the same elements")
        @MethodSource("gr.geompokon.bitarray.BitArrayTest#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.BitArrayTest#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.BitArrayTest#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.BitArrayTest#testCaseBooleans")
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
        @MethodSource("gr.geompokon.bitarray.BitArrayTest#testCaseBooleans")
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


    static Stream<Named<List<Boolean>>> testCaseBooleans() {
        synchronized (consistentRandom) {
            consistentRandom.setSeed(SEED);
            return Stream.of(
                    Named.of("0", List.of()),
                    Named.of("1", List.of(Boolean.FALSE)),
                    Named.of("2", List.of(Boolean.TRUE, Boolean.FALSE)),
                    Named.of("5", List.of(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE)),
                    Named.of("100", Stream.generate(consistentRandom::nextBoolean).limit(100).collect(Collectors.toList()))
            );
        }
    }
}
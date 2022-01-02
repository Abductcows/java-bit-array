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
package io.github.abductcows.bitarray

import io.github.abductcows.bitarray.TestUtils.getAddIndices
import io.github.abductcows.bitarray.TestUtils.getRemoveIndices
import io.github.abductcows.bitarray.TestUtils.getSetIndices
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@DisplayName("BitArray Unit Tests")
internal class BitArrayTest {

    lateinit var bitArray: BitArray

    @BeforeEach
    fun setUp() {
        bitArray = BitArray()
    }

    @Nested
    @DisplayName("CRUD Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class CRUDTest {

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Add at index should behave like ArrayList")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `add at index should work like ArrayList`(elementsToAdd: List<Boolean>) {

            // given
            val authority: MutableList<Boolean> = ArrayList(elementsToAdd.size)
            val addIndices = getAddIndices(elementsToAdd.size)

            // when
            for (i in elementsToAdd.indices) {
                bitArray.add(addIndices[i], elementsToAdd[i])
                authority.add(addIndices[i], elementsToAdd[i])
            }

            // then
            assertThat(bitArray).isEqualTo(authority)
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Add at the end should behave like ArrayList")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `add at tail should work like ArrayList`(elementsToAdd: List<Boolean>) {

            // given
            val authority: MutableList<Boolean> = ArrayList(elementsToAdd.size)

            // when
            for (boolean in elementsToAdd) {
                bitArray.add(boolean)
                authority.add(boolean)
            }

            // then
            assertThat(bitArray).isEqualTo(authority)
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Set at index should behave like ArrayList")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `set at index should work like ArrayList`(elementsToAdd: List<Boolean>) {

            // given
            val authority: MutableList<Boolean> = ArrayList(elementsToAdd.size)
            val setIndices = getSetIndices(elementsToAdd.size)
            bitArray.addAll(elementsToAdd)
            authority.addAll(elementsToAdd)

            // when/then
            for (index in setIndices) {
                val nextSetValue = !authority[index]
                authority[index] = nextSetValue
                bitArray[index] = nextSetValue
                assertThat(bitArray[index]).isEqualTo(authority[index])
            }
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Remove at index should behave like ArrayList")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `remove at index should work like ArrayList`(elementsToRemove: List<Boolean>) {

            // given
            val authority: MutableList<Boolean> = ArrayList(elementsToRemove.size)
            val removeIndices = getRemoveIndices(elementsToRemove.size)
            bitArray.addAll(elementsToRemove)
            authority.addAll(elementsToRemove)

            // when/then
            for (removeIndex in removeIndices) {

                // assert same element removed
                assertThat(bitArray.removeAt(removeIndex))
                    .isEqualTo(authority.removeAt(removeIndex))

                // and rest are unchanged
                assertThat(bitArray).isEqualTo(authority)
            }
        }
    }

    @Nested
    @DisplayName("Copy Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class CopyTests {
        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of copy constructor should have the same elements")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `Copy constructor should return identical list`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            val copy = BitArray(bitArray)

            // then
            assertThat(copy).isEqualTo(bitArray)
        }

        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Result of clone should be a separate object with the same contents")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `clone should return an independent object with the same elements`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            val copy = bitArray.clone()

            // then
            assertThat(copy)
                .isEqualTo(bitArray)
                .isNotSameAs(bitArray)
        }
    }

    @Nested
    @DisplayName("Serialization Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class SerializationTests {
        @ParameterizedTest(name = "{0} elements")
        @DisplayName("Serialized and immediately deserialized array should be the same as original")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        fun `toString and fromString should not alter contents`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            val serialized = bitArray.toString()
            val deserialized = BitArray.fromString(serialized)

            // then
            assertThat(deserialized).isEqualTo(bitArray)
        }

        @ParameterizedTest
        @ValueSource(strings = ["[0 1]", "Size = 2, [true true]", "Size =z, [0 1]", "Size = 3, [0 1]"])
        @DisplayName("Bad strings should throw specific exceptions")
        fun `fromString should throw exception on bad strings`(faultyString: String) {
            assertThatThrownBy {
                val impossibleList = BitArray.fromString(faultyString)
                impossibleList.add(java.lang.Boolean.FALSE)
            }.isInstanceOf(IllegalFormatException::class.java)
        }
    }

    @Nested
    @DisplayName("New Method Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class NewMethodTests {
        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        @DisplayName("sumMod2 is equivalent to parity of 1s in the array")
        fun `sumMod2 should be equivalent to the parity of 1s in the array`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            val sumMod2 = bitArray.sumMod2()
            val expectedSumMod2 = bitArray.count { it == true } % 2

            // then
            printDetails(elementsToAdd.size, expectedSumMod2, sumMod2)
            assertThat(sumMod2).isEqualTo(expectedSumMod2)
        }

        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        @DisplayName("countOnes is equivalent to the number of true elements in the array")
        fun `countOnes should return number of true elements in the array`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            val ones = bitArray.countOnes()
            val expectedOnes = bitArray.count { it == true }

            // then
            printDetails(elementsToAdd.size, expectedOnes, ones)
            assertThat(ones).isEqualTo(expectedOnes)
        }

        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        @DisplayName("countZeros is equivalent to the number of false elements in the array")
        fun `countZeros should return number of false elements in the array`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            val zeros = bitArray.countZeros()
            val expectedZeros = bitArray.count { it == false }

            // then
            printDetails(elementsToAdd.size, expectedZeros, zeros)
            assertThat(zeros).isEqualTo(expectedZeros)
        }
    }

    @Nested
    @DisplayName("AbstractList implementation overrides")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class AbstractListOverridesTests {
        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans",
            "io.github.abductcows.bitarray.TestUtils#allSameBooleans")
        @DisplayName("indexOf should work like ArrayList")
        fun `indexOf should work like ArrayList`(elementsToAdd: List<Boolean>) {
            // given
            val authority: List<Boolean> = ArrayList(elementsToAdd)
            bitArray.addAll(elementsToAdd)

            // when
            val firstTrue = bitArray.indexOf(java.lang.Boolean.TRUE)
            val firstFalse = bitArray.indexOf(java.lang.Boolean.FALSE)
            val expectedTrue = authority.indexOf(java.lang.Boolean.TRUE)
            val expectedFalse = authority.indexOf(java.lang.Boolean.FALSE)
            val firstInvalid = bitArray.indexOf(BitArray::class.java as Any)

            // then
            printDetails(elementsToAdd.size, expectedTrue, firstTrue, "first true")
            printDetails(elementsToAdd.size, expectedFalse, firstFalse, "first false")
            printDetails(elementsToAdd.size, -1, firstInvalid, "first invalid")
            assertThat(firstTrue).isEqualTo(expectedTrue)
            assertThat(firstFalse).isEqualTo(expectedFalse)
            assertThat(firstInvalid).isEqualTo(-1)
        }

        @ParameterizedTest(name = "{0} elements before clear")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        @DisplayName("Cleared array should be empty")
        fun `clear leaves empty list`(elementsToAdd: List<Boolean>) {
            // given
            bitArray.addAll(elementsToAdd)

            // when
            bitArray.clear()

            // then
            assertThat(bitArray).isEmpty()
        }
    }

    @Nested
    @DisplayName("Internal methods tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class InternalMethodsTest {
        @Test
        @DisplayName("singleBitMask should work for all values 0-63")
        fun `singleBitMask should work for all values 0-63`() {
            // when
            val values = (0..63).map { bitArray.singleBitMask(it) }

            // then
            assertThat(values)
                .doesNotHaveDuplicates()
                .hasSize(64)
        }
    }

    @JvmOverloads
    internal fun printDetails(testSize: Int, expected: Any?, actual: Any?, additionalNote: String = "") {
        if (additionalNote.isNotEmpty()) {
            System.out.printf("%15s ", String.format("(%s)", additionalNote))
        }
        System.out.printf("Test size: %3d elements | Expected = %s, Actual = %s", testSize, expected, actual)
        if (bitArray.size <= 10) {
            println(" | Array: $bitArray")
        } else {
            println()
        }
    }
}
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
import io.github.abductcows.bitarray.TestUtils.getRemoveRangeIndices
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
                impossibleList.add(false)
            }.isInstanceOf(IllegalFormatException::class.java)
        }
    }

    @Nested
    @DisplayName("New Method Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class NewMethodTests {

        @ParameterizedTest(name = "{0} elements")
        @MethodSource("io.github.abductcows.bitarray.TestUtils#testCaseBooleans")
        @DisplayName("removeRange should remove all elements from start to end exclusive")
        fun `removeRange should remove all elements from start to end exclusive`(elementsToAdd: List<Boolean>) {

            // given
            var authority: MutableList<Boolean> = ArrayList(elementsToAdd)
            bitArray.addAll(elementsToAdd)

            val removeRangeIndices = getRemoveRangeIndices(bitArray.size)
            println(removeRangeIndices)
            // when/then
            for ((start, end) in removeRangeIndices) {
                authority = authority.filterIndexed { index, _ ->
                    index !in (start until end)
                }.toMutableList()
                bitArray.removeRange(start, end)

                assertThat(bitArray).isEqualTo(authority)
                println(bitArray)
                println(authority)
                println()
            }
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

        @ParameterizedTest(name = "{0} elements")
        @MethodSource(
            "io.github.abductcows.bitarray.TestUtils#testCaseBooleans",
            "io.github.abductcows.bitarray.TestUtils#allSameBooleans"
        )
        @DisplayName("indexOfNeedle should work like ArrayList::indexOf")
        fun `indexOfNeedle should work like ArrayList indexOf`(elementsToAdd: List<Boolean>) {
            // given
            val authority: List<Boolean> = ArrayList(elementsToAdd)
            bitArray.addAll(elementsToAdd)

            // when
            val firstTrue = bitArray.indexOfNeedle(true)
            val expectedTrue = authority.indexOf(true)

            val firstFalse = bitArray.indexOfNeedle(false)
            val expectedFalse = authority.indexOf(false)

            // then
            printDetails(elementsToAdd.size, expectedTrue, firstTrue, "first true")
            printDetails(elementsToAdd.size, expectedFalse, firstFalse, "first false")
            assertThat(firstTrue).isEqualTo(expectedTrue)
            assertThat(firstFalse).isEqualTo(expectedFalse)
        }

    }

    @Nested
    @DisplayName("AbstractList implementation overrides")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    internal inner class AbstractListOverridesTests {

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
                .allMatch { it.countOneBits() == 1 }
                .isSortedAccordingTo(Comparator.comparingInt { it.countLeadingZeroBits() })
        }

        @ParameterizedTest
        @MethodSource("io.github.abductcows.bitarray.TestUtils#bitSelectionTestWords")
        @DisplayName("selectBits should keep the selected bits the same and leave everything else at 0")
        fun `selectBits should keep the selected bits the same and leave everything else at 0`(testLong: Long) {

            val n = Long.SIZE_BITS

            for (start in 0 until n - 1) {
                for (length in 1 until n - start) {

                    var original = testLong
                    var selection = bitArray.selectBits(testLong, start, length)

                    for (bitIndex in n - 1 downTo 0) {
                        if (bitIndex < start || bitIndex > start + length - 1) {
                            assertThat(selection and 1L)
                                .isEqualTo(0)
                        } else {
                            assertThat(selection and 1L)
                                .isEqualTo(original and 1L)
                        }
                        original = original ushr 1
                        selection = selection ushr 1
                    }
                }
            }
        }

        @ParameterizedTest
        @MethodSource(
            "io.github.abductcows.bitarray.TestUtils#bitSelectionTestWords",
            "io.github.abductcows.bitarray.TestUtils#selectBitExtraWords"
        )
        @DisplayName("getBitInLong should return the value of the specified bit (0 or 1)")
        fun `getBitInLong should return the value of the specified bit (0 or 1)`(testLong: Long) {

            for (index in 0 until Long.SIZE_BITS) {

                val expected = if ((Long.MIN_VALUE ushr index and testLong) == 0L) 0L else 1L
                val actual = bitArray.getBitInLong(testLong, index)

                assertThat(actual)
                    .isEqualTo(expected)
            }
        }
    }

    internal fun printDetails(testSize: Int, expected: Any, actual: Any, additionalNote: String = "") {
        if (additionalNote.isNotEmpty()) {
            System.out.printf("%15s ", String.format("(%s)", additionalNote))
        }
        System.out.printf("Test size: %3d elements | Expected = %4s, Actual = %4s", testSize, expected, actual)
        if (bitArray.size <= 25) {
            println(" | Array: $bitArray")
        } else {
            println()
        }
    }
}
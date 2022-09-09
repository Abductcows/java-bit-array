package io.github.abductcows.bitarray

import org.junit.jupiter.api.Named
import java.lang.Long.toBinaryString
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object TestUtils {
    private const val SEED = 1111L
    private const val BITS_PER_LONG = 64
    private val consistentRandom = Random(SEED)
    private val booleanListsElementCounts = listOf(
        // standard small values
        (0..10).toList(),
        // random values less than 64
        listOf(23, 45, 59),
        // values around a few multiples of 64
        (1 * BITS_PER_LONG - 1..1 * BITS_PER_LONG + 1).toList(), // 63 - 65
        (2 * BITS_PER_LONG - 1..2 * BITS_PER_LONG + 1).toList(), // 127 - 129
        (3 * BITS_PER_LONG - 1..3 * BITS_PER_LONG + 1).toList(), // 191 - 193
        // greater values not too close to multiples of 64
        listOf(203, 230)
    ).flatten()

    /**
     * Lists of boolean elements returned by [consistentRandom]
     */
    @JvmStatic
    fun testCaseBooleans(): Iterable<Named<List<Boolean>>> = synchronizedWithFreshRandom {
        sequence {
            for (elementCount in booleanListsElementCounts) {
                yield(Named.of(
                    elementCount.toString(),
                    List(elementCount) { consistentRandom.nextBoolean() }
                ))
            }
        }.asIterable()
    }

    /**
     * Lists with all true and all false
     */
    @JvmStatic
    fun allSameBooleans(): Iterable<Named<List<Boolean>>> = sequence {
        for (elementCount in booleanListsElementCounts.filter { it > 0 }) {
            yield(
                Named.of(
                    "%d false".format(elementCount),
                    Collections.nCopies(elementCount, false)
                )
            )
            yield(
                Named.of(
                    "%d true".format(elementCount),
                    Collections.nCopies(elementCount, true)
                )
            )
        }
    }.asIterable()

    @JvmStatic
    fun getAddIndices(noOfElements: Int): List<Int> = synchronizedWithFreshRandom {
        consistentRandom.setSeed(SEED)
        val nextMax = AtomicInteger(1)
        List(noOfElements) { consistentRandom.nextInt(nextMax.getAndIncrement()) }
    }

    @JvmStatic
    fun getSetIndices(noOfElements: Int): List<Int> = synchronizedWithFreshRandom {
        List(noOfElements) { consistentRandom.nextInt(noOfElements) }
    }

    @JvmStatic
    fun getRemoveIndices(noOfElements: Int): List<Int> = synchronizedWithFreshRandom {
        val nextMax = AtomicInteger(noOfElements)
        List(noOfElements) { consistentRandom.nextInt(nextMax.getAndDecrement()) }
    }

    fun getRemoveRangeIndices(noOfElements: Int): List<Pair<Int, Int>> = synchronizedWithFreshRandom {
        var elementsLeft = noOfElements

         sequence {
            while (elementsLeft > 1) {
                val end = 1 + consistentRandom.nextInt(elementsLeft - 1)
                val start = consistentRandom.nextInt(end)
                val length = end - start
                elementsLeft -= length

                yield(start to end)
            }
        }.toList()
    }

    @JvmStatic
    fun bitSelectionTestWords(): Iterable<Named<Long>> {
        return listOf(
            listOf(-1L),
            // powers of two
            sequence {
                var next = Long.MIN_VALUE
                while (next != 0L) {
                    yield(next)
                    next = next ushr 1
                }
            }.toList(),
            listOf(0L),
            synchronizedWithFreshRandom { List(30) { consistentRandom.nextLong() } }
        )
            .flatten()
            .map {
                Named.named(
                    longFormattedToBinary(it),
                    it
                )
            }
    }

    @JvmStatic
    fun selectBitExtraWords() = listOf(
        0b0101010101010101010101010101010101010101010101010101010101010101L,
        0b10101010101010101010101010101010101010101010101010101010101010L
    ).map {
        Named.named(
            longFormattedToBinary(it),
            it
        )
    }

    private fun longFormattedToBinary(long: Long) = "($long) - 0b${(toBinaryString(long))}"

    @JvmStatic
    private fun <T> synchronizedWithFreshRandom(block: () -> T): T = synchronized(consistentRandom) {
        consistentRandom.setSeed(SEED)
        block()
    }
}
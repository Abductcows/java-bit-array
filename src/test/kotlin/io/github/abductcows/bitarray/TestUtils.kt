package io.github.abductcows.bitarray

import org.junit.jupiter.api.Named
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object TestUtils {
    private const val SEED = 69L
    private const val BITS_PER_LONG = 64
    private val consistentRandom = Random(SEED)
    private val booleanListsElementCounts = listOf(
        0,
        1,
        2,
        3,
        5,
        BITS_PER_LONG - 1,
        BITS_PER_LONG,
        BITS_PER_LONG + 1,
        2 * BITS_PER_LONG - 1,
        2 * BITS_PER_LONG,
        2 * BITS_PER_LONG + 1
    )

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
            yield(Named.of(
                "%d false".format(elementCount),
                Collections.nCopies(elementCount, false)))
            yield(Named.of(
                "%d true".format(elementCount),
                Collections.nCopies(elementCount, true)
            ))
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

    @JvmStatic
    fun <T> synchronizedWithFreshRandom(block: () -> T): T = synchronized(consistentRandom) {
        consistentRandom.setSeed(SEED)
        block()
    }
}
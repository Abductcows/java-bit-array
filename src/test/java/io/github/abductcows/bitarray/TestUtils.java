package io.github.abductcows.bitarray;

import org.junit.jupiter.api.Named;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {

    static final int SEED = 69;
    static final int BITS_PER_LONG = 64;
    static final Random consistentRandom = new Random(SEED);

    static Stream<Named<List<Boolean>>> testCaseBooleans() {
        synchronized (consistentRandom) {
            consistentRandom.setSeed(SEED);
            List<Integer> elementCounts = List.of(
                    0,
                    1,
                    2,
                    BITS_PER_LONG - 1,
                    BITS_PER_LONG,
                    BITS_PER_LONG + 1,
                    2 * BITS_PER_LONG + 1);
            Stream.Builder<Named<List<Boolean>>> builder = Stream.builder();

            for (int elementCount : elementCounts) {
                builder.add(Named.of(
                        Integer.toString(elementCount),
                        Stream.generate(consistentRandom::nextBoolean).limit(elementCount).collect(Collectors.toList()))
                );
            }

            return builder.build();
        }
    }

    static Stream<Named<List<Boolean>>> allSameBooleans() {
        List<Integer> elementCounts = List.of(
                1,
                2,
                BITS_PER_LONG - 1,
                BITS_PER_LONG,
                BITS_PER_LONG + 1,
                2 * BITS_PER_LONG + 1);
        Stream.Builder<Named<List<Boolean>>> builder = Stream.builder();
        for (int elementCount : elementCounts) {
            builder.add(Named.of(
                    String.format("%d false", elementCount),
                    Collections.nCopies(elementCount, Boolean.FALSE))
            );
            builder.add(Named.of(
                    String.format("%d true", elementCount),
                    Collections.nCopies(elementCount, Boolean.TRUE)
            ));
        }

        return builder.build();
    }

    static List<Integer> getAddIndices(int noOfElements) {
        synchronized (consistentRandom) {
            consistentRandom.setSeed(SEED);
            AtomicInteger nextMax = new AtomicInteger(1);

            return Stream
                    .generate(() -> consistentRandom.nextInt(nextMax.getAndIncrement()))
                    .limit(noOfElements)
                    .collect(Collectors.toList());
        }
    }

    static List<Integer> getSetIndices(int noOfElements) {
        synchronized (consistentRandom) {
            consistentRandom.setSeed(SEED);

            return Stream.generate(() -> consistentRandom.nextInt(noOfElements))
                    .limit(noOfElements)
                    .collect(Collectors.toList());
        }
    }

    static List<Integer> getRemoveIndices(int noOfElements) {
        synchronized (consistentRandom) {
            consistentRandom.setSeed(SEED);
            AtomicInteger nextMax = new AtomicInteger(noOfElements);

            return Stream
                    .generate(() -> consistentRandom.nextInt(nextMax.getAndDecrement()))
                    .limit(noOfElements)
                    .collect(Collectors.toList());
        }
    }
}

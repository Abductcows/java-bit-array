package gr.geompokon.bitarray;

import org.junit.jupiter.api.Named;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {

    static final int SEED = 69;
    static final Random consistentRandom = new Random(SEED);

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

package io.github.abductcows.bitarray.benchmarks;

import io.github.abductcows.bitarray.BitArray;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 4, warmups = 0)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
public class RemoveRangeBnechmark {

    @State(Scope.Benchmark)
    public static class Data {
        @Param({"512", "20000", "350000", "1500000"})
        public int elements;

        BitArray array;

        public final int removes = 100;
        public int[] removeRangeIndices;

        @Setup
        public void setUp() {
            Random rand = new Random(elements);
            array = new BitArray(elements);

            for (int i = 0; i < elements; ++i) {
                array.add(rand.nextBoolean());
            }

            int maxRemoveLength = elements / 100;
            removeRangeIndices = new int[2 * removes];

            for (int i = 0, elementsLeft = elements; i < removes; i += 2) {
                removeRangeIndices[i] = rand.nextInt(elementsLeft - maxRemoveLength);
                removeRangeIndices[i + 1] = removeRangeIndices[i] + rand.nextInt(maxRemoveLength);
                elementsLeft -= removeRangeIndices[i + 1] - removeRangeIndices[i];
            }
        }
    }

    @Benchmark
    public BitArray removeRangeNaive(Data d) {
        BitArray array = new BitArray(d.array);
        for (int i = 0, stop = 2 * d.removes; i < stop; i += 2) {
            for (int j = d.removeRangeIndices[i + 1] - d.removeRangeIndices[i], removeIndex = d.removeRangeIndices[i]; j > 0; --j) {
                array.remove(removeIndex);
            }
        }
        return array;
    }

    @Benchmark
    public BitArray removeRangeLeet(Data d) {
        BitArray array = new BitArray(d.array);
        for (int i = 0, n = 2 * d.removes; i < n; i += 2) {
            array.removeRange(d.removeRangeIndices[i], d.removeRangeIndices[i + 1]);
        }
        return array;
    }


    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }
}

package deltix.dfp;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 5, timeUnit = TimeUnit.SECONDS, iterations = 2)
@Measurement(time = 5, timeUnit = TimeUnit.SECONDS, iterations = 5)
@Fork(3)
@State(Scope.Thread)
public class ConversionBenchmark {
    private double doubleValue;
    private long decimalValue;
    private long longIntegerValue;
    private int integerValue;
    private long hugeLongValue;

    @Setup
    public void setUp() {
        doubleValue = new Random().nextDouble();
        decimalValue = Decimal64Utils.fromDouble(doubleValue);
        longIntegerValue = (long)(10000.0 * doubleValue);
        integerValue = (int)(10000.0 * doubleValue);
        hugeLongValue = 100000000000000000L +  (long)(10000.0 * doubleValue) * 10000;
    }

    @Benchmark
    public long fromDouble() {
        return Decimal64Utils.fromDouble(doubleValue);
    }

    @Benchmark
    public double toDouble() {
        return Decimal64Utils.toDouble(decimalValue);
    }

    @Benchmark
    public void fromFixedPointLong(Blackhole blackhole) {
        blackhole.consume(Decimal64Utils.fromFixedPoint(longIntegerValue, 4));
    }

    @Benchmark
    public void fromFixedPointHugeLong(Blackhole blackhole) {
        blackhole.consume(Decimal64Utils.fromFixedPoint(hugeLongValue, 4));
    }

    @Benchmark
    public void fromFixedPointInt(Blackhole blackhole) {
        blackhole.consume(Decimal64Utils.fromFixedPoint(integerValue, 4));
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void toFixedPoint(Blackhole blackhole) {
        blackhole.consume(Decimal64Utils.toFixedPoint(decimalValue, 4));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ConversionBenchmark.class.getSimpleName() + ".*")
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}


package deltix.dfp;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 3, iterations = 1)
@Measurement(time = 3, iterations = 3)
@State(Scope.Thread)
public class ParsingBenchmark {
    @Param({"0.072876024093886854"})
    private String stringValue;

    @Benchmark
    @Decimal
    public long javaImpl() throws IOException {
        return Decimal64Utils.parse(stringValue);
    }

    @Benchmark
    @Decimal
    public long viaDouble() throws IOException {
        return Decimal64Utils.fromDouble(Double.parseDouble(stringValue));
    }

    @Benchmark
    public double justDouble() throws IOException {
        return Double.parseDouble(stringValue);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(".*" + UnaryOperationBenchmark.class.getSimpleName() + ".*")
            .forks(1)
            .build();
        new Runner(opt).run();
    }
}


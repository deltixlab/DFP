package com.epam.deltix.dfp;

import org.junit.Test;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;

import java.security.SecureRandom;
import java.util.Random;

public class MathTest {
    private static final int N = 10_000;

    static final double log2 = Math.log(2.0);

    static double doubleDiff(final double refValue, final double testValue) {
        return Math.log(Math.abs(refValue - testValue) /
            Math.max(Math.ulp(refValue), Math.ulp(testValue))) / log2;
    }

    static class TestCaseData {
        public Random random;
        public double rnd01;
        public double doubleOut;
        public long decimalOut;
    }

    static void testMathPrecision(final long N, final TestUtils.Consumer<TestCaseData> testFn, final double maxAcceptableError) {
        if (N < 0)
            throw new IllegalArgumentException("The N(=" + N + ") must be positive.");

        final long randomSeed = 42; // new SecureRandom().nextLong();
        final Random random = new Random(randomSeed);

        double maxError = Double.NEGATIVE_INFINITY;
        long maxErrorIteration = -1;
        double maxErrorRnd01 = Double.NaN;
        double maxErrorDoubleOut = Double.NaN;
        double maxErrorDecimalOut = Double.NaN;
        long maxErrorDecimalLong = 0;

        final TestCaseData testData = new TestCaseData();
        testData.random = random;

        for (long i = 0; i < N; ++i) {
            final double rnd01 = random.nextDouble();

            testData.rnd01 = rnd01;
            testFn.accept(testData);
            final double decimalOut = Decimal64Utils.toDouble(testData.decimalOut);
            double error = doubleDiff(testData.doubleOut, decimalOut);

            if (maxError < error) {
                maxError = error;
                maxErrorIteration = i;
                maxErrorRnd01 = rnd01;
                maxErrorDoubleOut = testData.doubleOut;
                maxErrorDecimalOut = decimalOut;
                maxErrorDecimalLong = testData.decimalOut;
            }
        }

        if (maxError > maxAcceptableError)
            throw new RuntimeException(String.format(
                "[RandomSeed=%d][Iteration=%d][Rnd01=%.17e] The error(=%g) between doubleValue(=%.17e) and decimalValue(=%.17e == %dL)" +
                    " is greater than maxAcceptableError(=%g).",
                randomSeed, maxErrorIteration, maxErrorRnd01, maxError, maxErrorDoubleOut, maxErrorDecimalOut, maxErrorDecimalLong,
                maxAcceptableError));

        if (Math.ceil(maxError) < maxAcceptableError) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String callerName = stackTrace.length > 2 ? stackTrace[2].getMethodName() + ": " : "";
            System.out.println(callerName + "The maxAcceptableError(=" + maxAcceptableError + ") looks to high for maxError(=" +
                maxError + ") estimated on " + N + " iterations.");
        }
    }

    @Test
    public void testExp() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 200;
                testCaseData.doubleOut = Math.exp(x);
                testCaseData.decimalOut = Decimal64Utils.exp(Decimal64Utils.fromDouble(x));
            }
        }, 6);
    }

    @Test
    public void testExp2() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 200;
                testCaseData.doubleOut = Math.pow(2.0, x);
                testCaseData.decimalOut = Decimal64Utils.exp2(Decimal64Utils.fromDouble(x));
            }
        }, 6);
    }

    @Test
    public void testExp10() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 200;
                testCaseData.doubleOut = Math.pow(10.0, x);
                testCaseData.decimalOut = Decimal64Utils.exp10(Decimal64Utils.fromDouble(x));
            }
        }, 7);
    }

    @Test
    public void testExpm1() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 200;
                testCaseData.doubleOut = Math.exp(x) - 1;
                testCaseData.decimalOut = Decimal64Utils.expm1(Decimal64Utils.fromDouble(x));
            }
        }, 18);
    }

    @Test
    public void testLog() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+100;
                testCaseData.doubleOut = Math.log(x);
                testCaseData.decimalOut = Decimal64Utils.log(Decimal64Utils.fromDouble(x));
            }
        }, 1);
    }

    @Test
    public void testLog2() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+100;
                testCaseData.doubleOut = Math.log(x) / log2;
                testCaseData.decimalOut = Decimal64Utils.log2(Decimal64Utils.fromDouble(x));
            }
        }, 1);
    }

    @Test
    public void testLog10() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+100;
                testCaseData.doubleOut = Math.log10(x);
                testCaseData.decimalOut = Decimal64Utils.log10(Decimal64Utils.fromDouble(x));
            }
        }, 0);
    }

    @Test
    public void testLog1p() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+100;
                testCaseData.doubleOut = Math.log1p(x);
                testCaseData.decimalOut = Decimal64Utils.log1p(Decimal64Utils.fromDouble(x));
            }
        }, 1);
    }

    @Test
    public void testPow() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 6;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 200;
                testCaseData.doubleOut = Math.pow(x, y);
                testCaseData.decimalOut = Decimal64Utils.pow(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 10);
    }

    @Test
    public void testFmod() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 1e+10;
                testCaseData.doubleOut = x % y;
                testCaseData.decimalOut = Decimal64Utils.fmod(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 30);
    }

    @Test
    public void testHypot() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 1e+10;
                testCaseData.doubleOut = Math.hypot(x, y);
                testCaseData.decimalOut = Decimal64Utils.hypot(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 4);
    }

    @Test
    public void testSin() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                testCaseData.doubleOut = Math.sin(x);
                testCaseData.decimalOut = Decimal64Utils.sin(Decimal64Utils.fromDouble(x));
            }
        }, 54);
    }

    @Test
    public void testCos() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                testCaseData.doubleOut = Math.cos(x);
                testCaseData.decimalOut = Decimal64Utils.cos(Decimal64Utils.fromDouble(x));
            }
        }, 54);
    }

    @Test
    public void testTan() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                testCaseData.doubleOut = Math.tan(x);
                testCaseData.decimalOut = Decimal64Utils.tan(Decimal64Utils.fromDouble(x));
            }
        }, 54);
    }

    @Test
    public void testAsin() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 2;
                testCaseData.doubleOut = Math.asin(x);
                testCaseData.decimalOut = Decimal64Utils.asin(Decimal64Utils.fromDouble(x));
            }
        }, 11);
    }

    @Test
    public void testAcos() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 2;
                testCaseData.doubleOut = Math.acos(x);
                testCaseData.decimalOut = Decimal64Utils.acos(Decimal64Utils.fromDouble(x));
            }
        }, 25);
    }

    @Test
    public void testAtan() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 10;
                testCaseData.doubleOut = Math.atan(x);
                testCaseData.decimalOut = Decimal64Utils.atan(Decimal64Utils.fromDouble(x));
            }
        }, 3);
    }

    @Test
    public void testAtan2() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+10;
                final double y = testCaseData.random.nextDouble() * 1e+10;
                testCaseData.doubleOut = Math.atan2(x, y);
                testCaseData.decimalOut = Decimal64Utils.atan2(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 4);
    }

    @Test
    public void testSinh() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 20;
                testCaseData.doubleOut = Math.sinh(x);
                testCaseData.decimalOut = Decimal64Utils.sinh(Decimal64Utils.fromDouble(x));
            }
        }, 4);
    }

    @Test
    public void testCosh() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 20;
                testCaseData.doubleOut = Math.cosh(x);
                testCaseData.decimalOut = Decimal64Utils.cosh(Decimal64Utils.fromDouble(x));
            }
        }, 4);
    }

    @Test
    public void testTanh() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 20;
                testCaseData.doubleOut = Math.tanh(x);
                testCaseData.decimalOut = Decimal64Utils.tanh(Decimal64Utils.fromDouble(x));
            }
        }, 3);
    }

    @Test
    public void testAsinh() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                testCaseData.doubleOut = Math.log(x + Math.sqrt(x * x + 1));
                testCaseData.decimalOut = Decimal64Utils.asinh(Decimal64Utils.fromDouble(x));
            }
        }, 49);
    }

    @Test
    public void testAcosh() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+10 + 1;
                testCaseData.doubleOut = Math.log(x + Math.sqrt(x * x - 1));
                testCaseData.decimalOut = Decimal64Utils.acosh(Decimal64Utils.fromDouble(x));
            }
        }, 2);
    }

    @Test
    public void testAtanh() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 2;
                testCaseData.doubleOut = 0.5 * Math.log((1 + x) / (1 - x));
                testCaseData.decimalOut = Decimal64Utils.atanh(Decimal64Utils.fromDouble(x));
            }
        }, 25);
    }

    @Test
    public void testErf() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 100;
                testCaseData.doubleOut = Erf.erf(x);
                testCaseData.decimalOut = Decimal64Utils.erf(Decimal64Utils.fromDouble(x));
            }
        }, 5);
    }

    @Test
    public void testErfc() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 100;
                testCaseData.doubleOut = Erf.erfc(x);
                testCaseData.decimalOut = Decimal64Utils.erfc(Decimal64Utils.fromDouble(x));
            }
        }, 12);
    }

    @Test
    public void testTgamma() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 10;
                testCaseData.doubleOut = Gamma.gamma(x);
                testCaseData.decimalOut = Decimal64Utils.tgamma(Decimal64Utils.fromDouble(x));
            }
        }, 27);
    }

    @Test
    public void testLgamma() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 10;
                testCaseData.doubleOut = Gamma.logGamma(x);
                testCaseData.decimalOut = Decimal64Utils.lgamma(Decimal64Utils.fromDouble(x));
            }
        }, 26);
    }

    @Test
    public void testSqrt() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+10;
                testCaseData.doubleOut = Math.sqrt(x);
                testCaseData.decimalOut = Decimal64Utils.sqrt(Decimal64Utils.fromDouble(x));
            }
        }, 3);
    }

    @Test
    public void testCbrt() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = testCaseData.rnd01 * 1e+10;
                testCaseData.doubleOut = Math.cbrt(x);
                testCaseData.decimalOut = Decimal64Utils.cbrt(Decimal64Utils.fromDouble(x));
            }
        }, 3);
    }

    @Test
    public void testAdd() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 10;
                testCaseData.doubleOut = x + y;
                testCaseData.decimalOut = Decimal64Utils.add(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 26);
    }

    @Test
    public void testSub() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 10;
                testCaseData.doubleOut = x - y;
                testCaseData.decimalOut = Decimal64Utils.sub(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 26);
    }

    @Test
    public void testMul() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 1e+10;
                testCaseData.doubleOut = x * y;
                testCaseData.decimalOut = Decimal64Utils.mul(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 4);
    }

    @Test
    public void testDiv() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 1e+10;
                testCaseData.doubleOut = x / y;
                testCaseData.decimalOut = Decimal64Utils.div(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y));
            }
        }, 4);
    }

    @Test
    public void testFma() {
        testMathPrecision(N, new TestUtils.Consumer<TestCaseData>() {
            @Override
            public void accept(TestCaseData testCaseData) {
                final double x = (testCaseData.rnd01 - 0.5) * 1e+10;
                final double y = (testCaseData.random.nextDouble() - 0.5) * 1e+10;
                final double z = (testCaseData.random.nextDouble() - 0.5) * 1e+10;
                testCaseData.doubleOut = x * y + z;
                testCaseData.decimalOut = Decimal64Utils.fma(Decimal64Utils.fromDouble(x), Decimal64Utils.fromDouble(y), Decimal64Utils.fromDouble(z));
            }
        }, 4);
    }
}

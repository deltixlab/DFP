package deltix.dfp;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static deltix.dfp.TestUtils.assertDfp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Decimal64UtilsTest {
    private static final int COUNT = 1000;

    private final Random random = new Random();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static void assertSimilar(double expected, double actual, double epsilon) {
        assertTrue(Math.abs(expected - actual) / Math.max(1.0, Math.abs(expected)) < epsilon);
    }

    private static long round(long x, long precision) {
        if (x >= 0) {
            return ((x + precision / 2) / precision) * precision;
        } else {
            return ((x - precision / 2) / precision) * precision;
        }
    }

    @Test
    public void doubleConversion() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final long y = Decimal64Utils.fromDouble(x);
            final double z = Decimal64Utils.toDouble(y);
            assertSimilar(x, z, 1.0E-09);
        }
    }

    @Test
    public void fixedPointConversion() {
        final int numberOfDigits = 9;
        for (int i = 0; i < COUNT; i += 1){
            final long m = random.nextLong() & 0xFFFF_FFFF_FFFFL;

            final long x = Decimal64Utils.fromFixedPoint(m, numberOfDigits);
            final double y = m * Math.pow(10.0, -numberOfDigits);
            assertSimilar(y, Decimal64Utils.toDouble(x), 1.0E-09);

            final long z = Decimal64Utils.toFixedPoint(x, numberOfDigits);
            assertEquals(z, m);
        }
    }

    @Test
    public void intConversion() {
        int m = Integer.MIN_VALUE; // Test min value first

        for (int i = 0; i < COUNT; i += 1) {
            final long dfp = Decimal64Utils.fromInt(m);
            assertEquals(m, Decimal64Utils.toInt(dfp));
            assertSimilar(m, Decimal64Utils.toDouble(dfp), 1.0E-09);
            m = random.nextInt();
        }
    }

    @Test
    public void longConversion() {
        for (int i = 0; i < COUNT; i += 1){
            final long m = random.nextLong() & 0xFFFF_FFFF_FFFFL;

            final long x = Decimal64Utils.fromLong(m);
            assertEquals(m, Decimal64Utils.toLong(x));
            assertSimilar(m, Decimal64Utils.toDouble(x), 1.0E-09);
        }
    }

    @Test
    public void negation() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final long y = Decimal64Utils.fromDouble(x);
            final long z = Decimal64Utils.negate(y);
            final double w = Decimal64Utils.toDouble(z);
            assertSimilar(-x, w, 1.0E-09);
        }
    }

    @Test
    public void addition() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();
            final double z = random.nextDouble();
            final double w = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long dz = Decimal64Utils.fromDouble(z);
            final long dw = Decimal64Utils.fromDouble(w);

            assertSimilar(x + y, Decimal64Utils.toDouble(Decimal64Utils.add(dx, dy)), 1.0E-09);
            assertSimilar(x + y + z, Decimal64Utils.toDouble(Decimal64Utils.add(dx, dy, dz)), 1.0E-09);
            assertSimilar(x + y + z + w, Decimal64Utils.toDouble(Decimal64Utils.add(dx, dy, dz, dw)), 1.0E-09);
        }
    }

    @Test
    public void subtraction() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long result = Decimal64Utils.subtract(dx, dy);

            assertSimilar(x - y, Decimal64Utils.toDouble(result), 1.0E-09);
        }
    }

    @Test
    public void multiplication() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();
            final double z = random.nextDouble();
            final double w = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long dz = Decimal64Utils.fromDouble(z);
            final long dw = Decimal64Utils.fromDouble(w);

            assertSimilar(x * 13, Decimal64Utils.toDouble(Decimal64Utils.multiplyByInteger(dx, 13)), 1.0E-09);

            assertSimilar(x * 10, Decimal64Utils.toDouble(Decimal64Utils.scaleByPowerOfTen(dx, 1)), 1.0E-09);
            assertSimilar(x * 100, Decimal64Utils.toDouble(Decimal64Utils.scaleByPowerOfTen(dx, 2)), 1.0E-09);
            assertSimilar(x * 1000, Decimal64Utils.toDouble(Decimal64Utils.scaleByPowerOfTen(dx, 3)), 1.0E-09);

            assertSimilar(x * y, Decimal64Utils.toDouble(Decimal64Utils.multiply(dx, dy)), 1.0E-09);
            assertSimilar(x * y * z, Decimal64Utils.toDouble(Decimal64Utils.multiply(dx, dy, dz)), 1.0E-09);
            assertSimilar(x * y * z * w, Decimal64Utils.toDouble(Decimal64Utils.multiply(dx, dy, dz, dw)), 1.0E-09);
        }
    }

    @Test
    public void division() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long result = Decimal64Utils.divide(dx, dy);

            assertSimilar(x / 13, Decimal64Utils.toDouble(Decimal64Utils.divideByInteger(dx, 13)), 1.0E-09);

            assertSimilar(x / 10, Decimal64Utils.toDouble(Decimal64Utils.scaleByPowerOfTen(dx, -1)), 1.0E-09);
            assertSimilar(x / 100, Decimal64Utils.toDouble(Decimal64Utils.scaleByPowerOfTen(dx, -2)), 1.0E-09);
            assertSimilar(x / 1000, Decimal64Utils.toDouble(Decimal64Utils.scaleByPowerOfTen(dx, -3)), 1.0E-09);

            assertSimilar(x / y, Decimal64Utils.toDouble(result), 1.0E-09);
        }
    }

    @Test
    public void min() {
        assertEquals(Decimal64Utils.NaN, Decimal64Utils.min(Decimal64Utils.ZERO, Decimal64Utils.NaN));
        assertEquals(Decimal64Utils.NaN, Decimal64Utils.min(Decimal64Utils.NaN, Decimal64Utils.ZERO));
        assertEquals(Decimal64Utils.NaN, Decimal64Utils.min(Decimal64Utils.NaN, Decimal64Utils.NaN));

        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();
            final double z = random.nextDouble();
            final double w = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long dz = Decimal64Utils.fromDouble(z);
            final long dw = Decimal64Utils.fromDouble(w);

            assertSimilar(Math.min(x, y), Decimal64Utils.toDouble(Decimal64Utils.min(dx, dy)), 1.0E-09);
            assertSimilar(Math.min(x, Math.min(y, z)), Decimal64Utils.toDouble(Decimal64Utils.min(dx, dy, dz)), 1.0E-09);
            assertSimilar(Math.min(Math.min(x, y), Math.min(z, w)), Decimal64Utils.toDouble(Decimal64Utils.min(dx, dy, dz, dw)), 1.0E-09);
        }
    }

    @Test
    public void max() {
        assertEquals(Decimal64Utils.NaN, Decimal64Utils.max(Decimal64Utils.ZERO, Decimal64Utils.NaN));
        assertEquals(Decimal64Utils.NaN, Decimal64Utils.max(Decimal64Utils.NaN, Decimal64Utils.ZERO));
        assertEquals(Decimal64Utils.NaN, Decimal64Utils.max(Decimal64Utils.NaN, Decimal64Utils.NaN));

        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();
            final double z = random.nextDouble();
            final double w = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long dz = Decimal64Utils.fromDouble(z);
            final long dw = Decimal64Utils.fromDouble(w);

            assertSimilar(Math.max(x, y), Decimal64Utils.toDouble(Decimal64Utils.max(dx, dy)), 1.0E-09);
            assertSimilar(Math.max(x, Math.max(y, z)), Decimal64Utils.toDouble(Decimal64Utils.max(dx, dy, dz)), 1.0E-09);
            assertSimilar(Math.max(Math.max(x, y), Math.max(z, w)), Decimal64Utils.toDouble(Decimal64Utils.max(dx, dy, dz, dw)), 1.0E-09);
        }
    }

    @Test
    public void abs() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble() * (random.nextBoolean() ? 1 : -1);

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.abs(dx);

            assertSimilar(Math.abs(x), Decimal64Utils.toDouble(dy), 1.0E-09);
        }
    }

    @Test
    public void average() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);
            final long result = Decimal64Utils.average(dx, dy);

            assertSimilar((x + y) / 2, Decimal64Utils.toDouble(result), 1.0E-09);
        }
    }

    @Test
    public void round() {
        final int numberOfDigits = 9;
        final long multiplier = (long) Math.pow(10, numberOfDigits);
        for (int i = 0; i < COUNT; i += 1) {
            final long x = random.nextLong() & 0xFFFF_FFFF_FFFFL;
            final long p = random.nextLong() & 0xFFFF_FFFFL;

            final long dx = Decimal64Utils.fromFixedPoint(x, numberOfDigits);
            final long dp = Decimal64Utils.fromFixedPoint(p, numberOfDigits);
            final long result = Decimal64Utils.round(dx, dp);

            assertSimilar(round(x, p), Decimal64Utils.toDouble(result) * multiplier, 1.0E-09);
        }
    }

    @Test
    public void equalsObject() {
        Assert.assertTrue(Decimal64Utils.equals(Decimal64Utils.NaN, Decimal64Utils.NaN));
        Assert.assertTrue(Decimal64Utils.equals(5L, Decimal64.fromUnderlying(5L)));
        Assert.assertFalse(Decimal64Utils.equals(5L, (Object) 6L));
        Assert.assertFalse(Decimal64Utils.equals(5L, null));
        Assert.assertFalse(Decimal64Utils.equals(5L, "string"));
    }

    @Test
    public void constants() {
        assertSimilar(0, Decimal64Utils.toDouble(Decimal64Utils.ZERO), 1.0E-09);

        assertSimilar(1, Decimal64Utils.toDouble(Decimal64Utils.ONE), 1.0E-09);
        assertSimilar(2, Decimal64Utils.toDouble(Decimal64Utils.TWO), 1.0E-09);
        assertSimilar(10, Decimal64Utils.toDouble(Decimal64Utils.TEN), 1.0E-09);
        assertSimilar(100, Decimal64Utils.toDouble(Decimal64Utils.HUNDRED), 1.0E-09);
        assertSimilar(1000, Decimal64Utils.toDouble(Decimal64Utils.THOUSAND), 1.0E-09);
        assertSimilar(1000_000, Decimal64Utils.toDouble(Decimal64Utils.MILLION), 1.0E-09);

        assertSimilar(0.1, Decimal64Utils.toDouble(Decimal64Utils.ONE_TENTH), 1.0E-09);
        assertSimilar(0.01, Decimal64Utils.toDouble(Decimal64Utils.ONE_HUNDREDTH), 1.0E-09);
    }

    @Test
    public void testSpecial() {
        assertFalse(Decimal64Utils.isNaN(Decimal64Utils.ZERO));
        assertFalse(Decimal64Utils.isInfinity(Decimal64Utils.ZERO));
        assertFalse(Decimal64Utils.isPositiveInfinity(Decimal64Utils.ZERO));
        assertFalse(Decimal64Utils.isNegativeInfinity(Decimal64Utils.ZERO));

        assertTrue(Decimal64Utils.isNaN(Decimal64Utils.NaN));
        assertFalse(Decimal64Utils.isInfinity(Decimal64Utils.NaN));
        assertFalse(Decimal64Utils.isPositiveInfinity(Decimal64Utils.NaN));
        assertFalse(Decimal64Utils.isNegativeInfinity(Decimal64Utils.NaN));

        assertFalse(Decimal64Utils.isNaN(Decimal64Utils.POSITIVE_INFINITY));
        assertTrue(Decimal64Utils.isInfinity(Decimal64Utils.POSITIVE_INFINITY));
        assertTrue(Decimal64Utils.isPositiveInfinity(Decimal64Utils.POSITIVE_INFINITY));
        assertFalse(Decimal64Utils.isNegativeInfinity(Decimal64Utils.POSITIVE_INFINITY));

        assertFalse(Decimal64Utils.isNaN(Decimal64Utils.NEGATIVE_INFINITY));
        assertTrue(Decimal64Utils.isInfinity(Decimal64Utils.NEGATIVE_INFINITY));
        assertFalse(Decimal64Utils.isPositiveInfinity(Decimal64Utils.NEGATIVE_INFINITY));
        assertTrue(Decimal64Utils.isNegativeInfinity(Decimal64Utils.NEGATIVE_INFINITY));

        final double x = random.nextDouble();

        final long dx = Decimal64Utils.fromDouble(x);
        final long dy = Decimal64Utils.ZERO;
        final long result = Decimal64Utils.add(dx, dy);

        assertSimilar(x, Decimal64Utils.toDouble(result), 1.0E-09);
    }

    @Test
    public void compareTo() {
        for (int i = 0; i < COUNT; i += 1) {
            final double x = random.nextDouble();
            final double y = random.nextDouble();

            final long dx = Decimal64Utils.fromDouble(x);
            final long dy = Decimal64Utils.fromDouble(y);

            assertEquals(Double.compare(x, y), Decimal64Utils.compareTo(dx, dy));
            assertEquals(Double.compare(x, x), Decimal64Utils.compareTo(dx, dx));
            assertEquals(Double.compare(y, y), Decimal64Utils.compareTo(dy, dy));
        }

        assertEquals(0, Decimal64Utils.compareTo(Decimal64Utils.ONE, Decimal64Utils.ONE));
        assertEquals(-1, Decimal64Utils.compareTo(Decimal64Utils.ONE, Decimal64Utils.TWO));
        assertEquals(1, Decimal64Utils.compareTo(Decimal64Utils.TWO, Decimal64Utils.ONE));

        assertEquals(0, Decimal64Utils.compareTo(Decimal64Utils.POSITIVE_INFINITY, Decimal64Utils.POSITIVE_INFINITY));
        assertEquals(0, Decimal64Utils.compareTo(Decimal64Utils.NEGATIVE_INFINITY, Decimal64Utils.NEGATIVE_INFINITY));

        assertEquals(-1, Decimal64Utils.compareTo(Decimal64Utils.ONE, Decimal64Utils.POSITIVE_INFINITY));
        assertEquals(1, Decimal64Utils.compareTo(Decimal64Utils.ONE, Decimal64Utils.NEGATIVE_INFINITY));

        assertEquals(Double.compare(Double.NaN, Double.NaN), Decimal64Utils.compareTo(Decimal64Utils.NaN, Decimal64Utils.NaN));
        assertEquals(Double.compare(1.0, Double.NaN), Decimal64Utils.compareTo(Decimal64Utils.ONE, Decimal64Utils.NaN));
        assertEquals(Double.compare(Double.NaN, 1.0), Decimal64Utils.compareTo(Decimal64Utils.NaN, Decimal64Utils.ONE));
    }

    @Test
    public void comparisonWithZero() {
        final long value0 = Decimal64Utils.negate(Decimal64Utils.ZERO);
        assertTrue(Decimal64Utils.isZero(value0));
        assertFalse(Decimal64Utils.isPositive(value0));
        assertFalse(Decimal64Utils.isNegative(value0));
        assertTrue(Decimal64Utils.isNonPositive(value0));
        assertTrue(Decimal64Utils.isNonNegative(value0));

        final long value1 = Decimal64Utils.negate(Decimal64Utils.ZERO);
        assertTrue(Decimal64Utils.isZero(value1));
        assertFalse(Decimal64Utils.isPositive(value1));
        assertFalse(Decimal64Utils.isNegative(value1));
        assertTrue(Decimal64Utils.isNonPositive(value1));
        assertTrue(Decimal64Utils.isNonNegative(value1));

        final long value2 = Decimal64Utils.ONE;
        assertFalse(Decimal64Utils.isZero(value2));
        assertTrue(Decimal64Utils.isPositive(value2));
        assertFalse(Decimal64Utils.isNegative(value2));
        assertFalse(Decimal64Utils.isNonPositive(value2));
        assertTrue(Decimal64Utils.isNonNegative(value2));

        final long value3 = Decimal64Utils.negate(Decimal64Utils.ONE);
        assertFalse(Decimal64Utils.isZero(value3));
        assertFalse(Decimal64Utils.isPositive(value3));
        assertTrue(Decimal64Utils.isNegative(value3));
        assertTrue(Decimal64Utils.isNonPositive(value3));
        assertFalse(Decimal64Utils.isNonNegative(value3));
    }

    @Test
    public void isEqual() {
        // TODO: better tests
        assertTrue(Decimal64Utils.isEqual(Decimal64Utils.ONE, Decimal64Utils.ONE));
        assertTrue(Decimal64Utils.isEqual(Decimal64Utils.POSITIVE_INFINITY, Decimal64Utils.POSITIVE_INFINITY));
        assertTrue(Decimal64Utils.isEqual(Decimal64Utils.NEGATIVE_INFINITY, Decimal64Utils.NEGATIVE_INFINITY));
    }

    @Test
    public void roundTowardsPositiveInfinity() {
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-1.0"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.7"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.5"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.2"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.0"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.2"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.5"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.7"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("1.0"))));

        @Decimal final long multiple = Decimal64Utils.parse("0.1");

        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.10"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.07"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.05"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("-0.02"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.0"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.02"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.05"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.07"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsPositiveInfinity(Decimal64Utils.parse("0.10"), multiple)));
    }

    @Test
    public void roundTowardsNegativeInfinity() {
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-1.0"))));
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.7"))));
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.5"))));
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.2"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.0"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.2"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.5"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.7"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("1.0"))));

        @Decimal final long multiple = Decimal64Utils.parse("0.1");

        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.10"), multiple)));
        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.07"), multiple)));
        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.05"), multiple)));
        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("-0.02"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.0"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.02"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.05"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.07"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundTowardsNegativeInfinity(Decimal64Utils.parse("0.10"), multiple)));
    }

    @Test
    public void roundTowardsZero() {
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("-1.0"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("-0.7"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("-0.5"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("-0.2"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("0.0"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("0.2"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("0.5"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("0.7"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundTowardsZero(Decimal64Utils.parse("1.0"))));
    }

    @Test
    public void roundToNearestTiesAwayFromZero() {
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-1.0"))));
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.7"))));
        assertEquals("-1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.5"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.2"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.0"))));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.2"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.5"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.7"))));
        assertEquals("1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("1.0"))));

        @Decimal final long multiple = Decimal64Utils.parse("0.1");

        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.10"), multiple)));
        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.07"), multiple)));
        assertEquals("-0.1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.05"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("-0.02"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.0"), multiple)));
        assertEquals("0", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.02"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.05"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.07"), multiple)));
        assertEquals("0.1", Decimal64Utils.toString(Decimal64Utils.roundToNearestTiesAwayFromZero(Decimal64Utils.parse("0.10"), multiple)));
    }


    @Test
    public void canonizeTest() {
        long a = Decimal64Utils.fromFixedPoint(10000, 4);
        long b = Decimal64Utils.fromFixedPoint(1, 0);
        long c = Decimal64Utils.fromFixedPoint(10, 1);
        long d = Decimal64Utils.fromFixedPoint(100, 2);
        long e = Decimal64Utils.fromFixedPoint(1000, 3);

        Assert.assertEquals(false, Decimal64Utils.isIdentical(a, b));
        Assert.assertEquals(false, Decimal64Utils.isIdentical(b, c));
        Assert.assertEquals(false, Decimal64Utils.isIdentical(c, d));
        Assert.assertEquals(false, Decimal64Utils.isIdentical(d, e));
        Assert.assertEquals(false, Decimal64Utils.isIdentical(e, a));

        Assert.assertNotEquals(Decimal64Utils.identityHashCode(a), Decimal64Utils.identityHashCode(b));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(b), Decimal64Utils.identityHashCode(c));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(c), Decimal64Utils.identityHashCode(d));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(d), Decimal64Utils.identityHashCode(e));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(e), Decimal64Utils.identityHashCode(a));

        Assert.assertEquals(true, Decimal64Utils.equals(a, b));
        Assert.assertEquals(true, Decimal64Utils.equals(b, c));
        Assert.assertEquals(true, Decimal64Utils.equals(c, d));
        Assert.assertEquals(true, Decimal64Utils.equals(d, e));
        Assert.assertEquals(true, Decimal64Utils.equals(e, a));

        Assert.assertEquals(Decimal64Utils.hashCode(a), Decimal64Utils.hashCode(b));
        Assert.assertEquals(Decimal64Utils.hashCode(b), Decimal64Utils.hashCode(c));
        Assert.assertEquals(Decimal64Utils.hashCode(c), Decimal64Utils.hashCode(d));
        Assert.assertEquals(Decimal64Utils.hashCode(d), Decimal64Utils.hashCode(e));
        Assert.assertEquals(Decimal64Utils.hashCode(e), Decimal64Utils.hashCode(a));




        a = Decimal64Utils.canonize(a);
        b = Decimal64Utils.canonize(b);
        c = Decimal64Utils.canonize(c);
        d = Decimal64Utils.canonize(d);
        e = Decimal64Utils.canonize(e);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a, c);
        Assert.assertEquals(a, d);
        Assert.assertEquals(a, e);
        Assert.assertEquals(1.0, Decimal64Utils.toDouble(a), 0.00000001);
        long nan1 = Decimal64Utils.NaN;
        long nan2 = nan1 + 20;

        Assert.assertEquals(false, Decimal64Utils.isIdentical(nan1, nan2));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(nan1), Decimal64Utils.identityHashCode(nan2));
        Assert.assertEquals(true, Decimal64Utils.equals(nan1, nan2));
        Assert.assertEquals(Decimal64Utils.hashCode(nan1), Decimal64Utils.hashCode(nan2));

        nan1 = Decimal64Utils.canonize(nan1);
        nan2 = Decimal64Utils.canonize(nan2);
        Assert.assertEquals(nan1, nan2);

        long posInf1 = Decimal64Utils.POSITIVE_INFINITY;
        long posInf2 = posInf1 + 10;

        Assert.assertEquals(false, Decimal64Utils.isIdentical(posInf1, posInf2));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(posInf1), Decimal64Utils.identityHashCode(posInf2));
        Assert.assertEquals(true, Decimal64Utils.equals(posInf1, posInf2));
        Assert.assertEquals(Decimal64Utils.hashCode(posInf1), Decimal64Utils.hashCode(posInf2));


        posInf1 = Decimal64Utils.canonize(posInf1);
        posInf2 = Decimal64Utils.canonize(posInf2);


        Assert.assertEquals(posInf1, posInf2);
        long negInf1 = Decimal64Utils.NEGATIVE_INFINITY;
        long negInf2 = negInf1 + 10;


        Assert.assertEquals(false, Decimal64Utils.isIdentical(negInf1, negInf2));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(negInf1), Decimal64Utils.identityHashCode(negInf2));
        Assert.assertEquals(true, Decimal64Utils.equals(negInf1, negInf2));
        Assert.assertEquals(Decimal64Utils.hashCode(negInf1), Decimal64Utils.hashCode(negInf2));


        negInf1 = Decimal64Utils.canonize(negInf1);
        negInf2 = Decimal64Utils.canonize(negInf2);
        Assert.assertEquals(negInf1, negInf2);
        long zero1 = Decimal64Utils.fromFixedPoint(0, 1);
        long zero2 = Decimal64Utils.fromFixedPoint(0, 2);
        long zero3 = Decimal64Utils.fromFixedPoint(0, 3);


        Assert.assertEquals(false, Decimal64Utils.isIdentical(zero1, zero2));
        Assert.assertNotEquals(Decimal64Utils.identityHashCode(zero1), Decimal64Utils.identityHashCode(zero2));
        Assert.assertEquals(true, Decimal64Utils.equals(zero1, zero2));
        Assert.assertEquals(Decimal64Utils.hashCode(zero1), Decimal64Utils.hashCode(zero2));

        zero1 = Decimal64Utils.canonize(zero1);
        zero2 = Decimal64Utils.canonize(zero2);
        zero3 = Decimal64Utils.canonize(zero3);
        Assert.assertEquals(zero1, zero2);
        Assert.assertEquals(zero1, zero3);
        Assert.assertEquals(0.0, Decimal64Utils.toDouble(zero1), 0.00000001);
    }
}

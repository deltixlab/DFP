package com.epam.deltix.dfp;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static com.epam.deltix.dfp.JavaImpl.MASK_SIGN;
import static com.epam.deltix.dfp.TestUtils.assertDecimalEqual;
import static com.epam.deltix.dfp.TestUtils.assertDecimalIdentical;
import static org.junit.Assert.*;

public class JavaImplTest {
    private final Random random = new Random();

    @Test
    public void constantNaN() {
        final long value = JavaImpl.NaN;
        assertTrue(JavaImpl.isNaN(value));
        assertFalse(JavaImpl.isFinite(value));
        assertFalse(JavaImpl.isInfinity(value));
        assertFalse(JavaImpl.isNegativeInfinity(value));
        assertFalse(JavaImpl.isPositiveInfinity(value));
    }

    @Test
    public void constantPositiveInfinity() {
        final long value = JavaImpl.POSITIVE_INFINITY;
        assertFalse(JavaImpl.signBit(value));
        assertTrue(JavaImpl.isInfinity(value));
        assertFalse(JavaImpl.isNegativeInfinity(value));
        assertTrue(JavaImpl.isPositiveInfinity(value));
        assertFalse(JavaImpl.isFinite(value));
    }

    @Test
    public void constantNegativeInfinity() {
        final long value = JavaImpl.NEGATIVE_INFINITY;
        assertTrue(JavaImpl.signBit(value));
        assertTrue(JavaImpl.isInfinity(value));
        assertFalse(JavaImpl.isPositiveInfinity(value));
        assertTrue(JavaImpl.isNegativeInfinity(value));
        assertFalse(JavaImpl.isFinite(value));
    }

    @Test
    public void testConstants() {
        final int EXPONENT_BIAS = 398;

        // Relationships between internal representation constants
        assertTrue((JavaImpl.MASK_STEERING_BITS & JavaImpl.MASK_INFINITY_AND_NAN) == JavaImpl.MASK_STEERING_BITS);
        assertTrue((JavaImpl.MASK_STEERING_BITS | JavaImpl.MASK_INFINITY_AND_NAN) == JavaImpl.MASK_INFINITY_AND_NAN);

        assertTrue((JavaImpl.MASK_INFINITY_AND_NAN & JavaImpl.MASK_INFINITY_NAN) == JavaImpl.MASK_INFINITY_AND_NAN);
        assertTrue((JavaImpl.MASK_INFINITY_AND_NAN | JavaImpl.MASK_INFINITY_NAN) == JavaImpl.MASK_INFINITY_NAN);


        assertTrue(JavaImpl.fromUInt32(0) == JavaImpl.ZERO);
        assertTrue(JavaImpl.fromUInt32(1) == Decimal64Utils.ONE);
        assertTrue(JavaImpl.fromUInt32(2) == Decimal64Utils.TWO);

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, 0)) == JavaImpl.ZERO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS - 1, 0)) == JavaImpl.ZERO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, MASK_SIGN)) == JavaImpl.ZERO);

        // equals
        assertTrue(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, MASK_SIGN)), JavaImpl.ZERO));
        assertTrue(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS - 1, 0)), JavaImpl.ZERO));

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, MASK_SIGN)) == JavaImpl.negate(JavaImpl.ZERO));
        assertTrue(JavaImpl.negate(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, MASK_SIGN))) == JavaImpl.ZERO);

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(2, EXPONENT_BIAS, 0)) == Decimal64Utils.TWO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(20, EXPONENT_BIAS - 1, 0)) == Decimal64Utils.TWO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(2, EXPONENT_BIAS, MASK_SIGN)) == Decimal64Utils.TWO);

        // equals
        assertFalse(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(1000, EXPONENT_BIAS, MASK_SIGN)), Decimal64Utils.THOUSAND));
        assertTrue(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(10000, EXPONENT_BIAS - 1, 0)), Decimal64Utils.THOUSAND));

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(1000, EXPONENT_BIAS, MASK_SIGN)) == JavaImpl.negate(Decimal64Utils.THOUSAND));
        assertTrue(JavaImpl.negate(JavaImpl.fromParts(new Decimal64Parts(1000, EXPONENT_BIAS, MASK_SIGN))) == Decimal64Utils.THOUSAND);
    }

    @Test
    public void fromInt32() {
        assertTrue(JavaImpl.fromInt32(0) == JavaImpl.ZERO);
        assertTrue(Decimal64Utils.equals(JavaImpl.fromInt32(0), JavaImpl.ZERO));

        assertTrue(JavaImpl.fromInt32(1) == Decimal64Utils.ONE);
        assertTrue(JavaImpl.fromInt32(1000000) == Decimal64Utils.MILLION);
        assertTrue(JavaImpl.fromInt32(-1000000) == Decimal64Utils.fromDouble(-1000000.0));
    }

    @Test
    public void fromInt32Advanced() {
        final int N = 10000;
        int m = Integer.MIN_VALUE; // Test min value first
        for (int i = 0; i < N; ++i) {
            final long dfp = (i & 1) > 0 ? JavaImpl.fromInt32(m) : JavaImpl.fromInt32V2(m);
            assertEquals(Decimal64Utils.toInt(dfp), m);
            assertDecimalIdentical(NativeImpl.fromInt32(m), dfp);
            assertDecimalIdentical(NativeImpl.fromInt64(m), dfp);
            assertDecimalIdentical(NativeImpl.fromFloat64(m), dfp);
            m = random.nextInt();
        }
    }

    @Test
    public void fromUInt32() {
        assertTrue(JavaImpl.fromUInt32(0) == JavaImpl.ZERO);
        assertTrue(Decimal64Utils.equals(JavaImpl.fromUInt32(0), JavaImpl.ZERO));

        assertFalse(JavaImpl.fromParts(new Decimal64Parts()) == JavaImpl.ZERO);


        assertTrue(JavaImpl.fromUInt32(1) == Decimal64Utils.ONE);
        assertFalse(JavaImpl.fromUInt32(-1) == Decimal64Utils.ONE);
        assertTrue(JavaImpl.fromUInt32(1000000) == Decimal64Utils.MILLION);
        assertFalse(JavaImpl.fromUInt32(-1000000) == Decimal64Utils.fromDouble(-1000000.0));
    }

    @Test
    public void appendTo() throws IOException {
        final StringBuilder string = new StringBuilder();

        string.setLength(0);
        assertTrue("NaN".equals(JavaImpl.appendTo(Decimal64Utils.NaN, string).toString()));

        string.setLength(0);
        assertTrue("Infinity".equals(JavaImpl.appendTo(Decimal64Utils.POSITIVE_INFINITY, string).toString()));

        string.setLength(0);
        assertTrue("-Infinity".equals(JavaImpl.appendTo(Decimal64Utils.NEGATIVE_INFINITY, string).toString()));

        string.setLength(0);
        assertTrue("100000010000".equals(JavaImpl.appendTo(Decimal64Utils.fromDouble(10000001E+04), string).toString()));

        string.setLength(0);
        assertTrue("10000001".equals(JavaImpl.appendTo(Decimal64Utils.fromDouble(10000001), string).toString()));

        string.setLength(0);
        assertTrue("1000.0001".equals(JavaImpl.appendTo(Decimal64Utils.fromDouble(10000001E-04), string).toString()));

        string.setLength(0);
        assertTrue("9.2".equals(JavaImpl.appendTo(Decimal64Utils.fromDecimalDouble(92E-01), string).toString()));
    }

    @Test
    public void fromFixedPointFastConsts() {
        assertDecimalIdentical(Decimal64Utils.ZERO, JavaImpl.fromFixedPointFast(0, 0));
        assertDecimalIdentical(Decimal64Utils.ONE, JavaImpl.fromFixedPointFast(1, 0));
        assertDecimalIdentical(Decimal64Utils.TWO, JavaImpl.fromFixedPointFast(2, 0));
        assertDecimalIdentical(Decimal64Utils.TEN, JavaImpl.fromFixedPointFast(10, 0));
        assertDecimalEqual(Decimal64Utils.TEN, JavaImpl.fromFixedPointFast(1, -1));
        assertDecimalIdentical(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFast(100, 0));
        assertDecimalEqual(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFast(1, -2));
        assertDecimalEqual(Decimal64Utils.THOUSAND, JavaImpl.fromFixedPointFast(1, -3));
        assertDecimalEqual(Decimal64Utils.MILLION, JavaImpl.fromFixedPointFast(1, -6));
        assertDecimalIdentical(Decimal64Utils.ONE_TENTH, JavaImpl.fromFixedPointFast(1, 1));
        assertDecimalIdentical(Decimal64Utils.ONE_HUNDREDTH, JavaImpl.fromFixedPointFast(1, 2));

        assertDecimalIdentical(Decimal64Utils.ZERO, JavaImpl.fromFixedPointFastUnsigned(0, 0));
        assertDecimalIdentical(Decimal64Utils.ONE, JavaImpl.fromFixedPointFastUnsigned(1, 0));
        assertDecimalIdentical(Decimal64Utils.TWO, JavaImpl.fromFixedPointFastUnsigned(2, 0));
        assertDecimalIdentical(Decimal64Utils.TEN, JavaImpl.fromFixedPointFastUnsigned(10, 0));
        assertDecimalEqual(Decimal64Utils.TEN, JavaImpl.fromFixedPointFastUnsigned(1, -1));
        assertDecimalIdentical(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFastUnsigned(100, 0));
        assertDecimalEqual(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFastUnsigned(1, -2));
        assertDecimalEqual(Decimal64Utils.THOUSAND, JavaImpl.fromFixedPointFastUnsigned(1, -3));
        assertDecimalEqual(Decimal64Utils.MILLION, JavaImpl.fromFixedPointFastUnsigned(1, -6));
    }

    @Test
    public void fromFixedPointFast() {
        final int N = 10000;
        for (int exp = 398 - 0x2FF; exp <= 398; ++exp) {
            for (int j = 0; j < N; ++j) {
                final int mantissa = random.nextInt();
                assertDecimalIdentical(NativeImpl.fromFixedPoint32(mantissa, exp), JavaImpl.fromFixedPointFast(mantissa, exp));
                assertDecimalIdentical(NativeImpl.fromFixedPoint64(mantissa, exp), JavaImpl.fromFixedPointFast(mantissa, exp));

                if (mantissa >= 0) {
                    assertDecimalIdentical(NativeImpl.fromFixedPoint32(mantissa, exp), JavaImpl.fromFixedPointFastUnsigned(mantissa, exp));
                    assertDecimalIdentical(NativeImpl.fromFixedPoint64(mantissa, exp), JavaImpl.fromFixedPointFastUnsigned(mantissa, exp));
                }
            }

            assertDecimalIdentical(NativeImpl.fromFixedPoint32(0, exp), JavaImpl.fromFixedPointFast(0, exp));
            assertDecimalIdentical(NativeImpl.fromFixedPoint32(Integer.MIN_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MIN_VALUE, exp));
            assertDecimalIdentical(NativeImpl.fromFixedPoint32(Integer.MAX_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MAX_VALUE, exp));
            assertDecimalIdentical(NativeImpl.fromFixedPoint64(0, exp), JavaImpl.fromFixedPointFast(0, exp));
            assertDecimalIdentical(NativeImpl.fromFixedPoint64(Integer.MIN_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MIN_VALUE, exp));
            assertDecimalIdentical(NativeImpl.fromFixedPoint64(Integer.MAX_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MAX_VALUE, exp));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromFixedPointFastMin() {
        JavaImpl.fromFixedPointFast(0, 398 - 0x300);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromFixedPointFastMax() {
        JavaImpl.fromFixedPointFast(0, 399);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromFixedPointFastUMin() {
        JavaImpl.fromFixedPointFastUnsigned(0, 398 - 0x300);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromFixedPointFastUMax() {
        JavaImpl.fromFixedPointFastUnsigned(0, 399);
    }

    @SuppressWarnings("Duplicates")
    public static void checkToString(final String message, final String hex, final String expectedPlain, final String expectedScientific) {
        @Decimal final long dfp64 = UnsignedLong.parse(hex, 16);
        final String actual = Decimal64Utils.toString(dfp64);

        final String info = (message != null ? message : "") + " Hex = " + hex + ", Value = " + expectedScientific;
        assertEquals(info, expectedPlain, actual);

        @Decimal final long result1 = Decimal64Utils.parse(expectedPlain);
        assertEquals(info, expectedPlain, Decimal64Utils.toString(result1));

        @Decimal final long result2 = Decimal64Utils.parse(expectedScientific);
        assertEquals(info, expectedPlain, Decimal64Utils.toString(result2));

        @Decimal final long result3 = Decimal64Utils.tryParse(expectedPlain, Decimal64Utils.NaN);
        assertEquals(info, expectedPlain, Decimal64Utils.toString(result3));

        @Decimal final long result4 = Decimal64Utils.tryParse(expectedScientific, Decimal64Utils.NaN);
        assertEquals(info, expectedPlain, Decimal64Utils.toString(result4));
    }

    @Test
    public void toStringSpecialCase1() {
        checkToString(null, "000201905E5C7474", "0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056466952345714", "+564669523457140E-398");
    }

    @Test
    public void toStringSpecialCase2() {
        checkToString(null, "31800000013474D8", "202150", "+20215000E-2");
    }

    @Test
    public void toStringSpecialCase3() {
        checkToString(null, "8020000000000000", "0", "-0E-397");
    }

    @Test
    public void toStringSpecialCase4() {
        checkToString(null, "30593A484825D4D1", "7100.956540261585", "+7100956540261585E-12");
    }

    @Test(expected = NumberFormatException.class)
    public void parseEmptyString() {
        JavaImpl.parse("asdf", 0, 0, 0);
    }

    @Test(expected = NumberFormatException.class)
    public void parseNonDigits() {
        JavaImpl.parse("asdf", 0, 4, 0);
    }

    @Test
    public void fastSignCheck() {
        @Decimal final long[] testValues = new long[]{
            Decimal64Utils.fromDouble(Math.PI),
            Decimal64Utils.MIN_VALUE,
            Decimal64Utils.MAX_VALUE,
            Decimal64Utils.MIN_POSITIVE_VALUE,
            Decimal64Utils.MAX_NEGATIVE_VALUE,
            Decimal64Utils.ZERO,
            Decimal64Utils.POSITIVE_INFINITY,
            Decimal64Utils.NEGATIVE_INFINITY,
            Decimal64Utils.NaN,
            Decimal64Utils.NULL
        };

        for (@Decimal final long testValue : testValues) {
            @Decimal final long negTestValue = Decimal64Utils.negate(testValue);
            checkValues(testValue, NativeImpl.isPositive(testValue), Decimal64Utils.isPositive(testValue));
            checkValues(negTestValue, NativeImpl.isPositive(negTestValue), Decimal64Utils.isPositive(negTestValue));

            checkValues(testValue, NativeImpl.isNonPositive(testValue), Decimal64Utils.isNonPositive(testValue));
            checkValues(negTestValue, NativeImpl.isNonPositive(negTestValue), Decimal64Utils.isNonPositive(negTestValue));

            checkValues(testValue, NativeImpl.isNegative(testValue), Decimal64Utils.isNegative(testValue));
            checkValues(negTestValue, NativeImpl.isNegative(negTestValue), Decimal64Utils.isNegative(negTestValue));

            checkValues(testValue, NativeImpl.isNonNegative(testValue), Decimal64Utils.isNonNegative(testValue));
            checkValues(negTestValue, NativeImpl.isNonNegative(negTestValue), Decimal64Utils.isNonNegative(negTestValue));

            checkValues(testValue, NativeImpl.isZero(testValue), Decimal64Utils.isZero(testValue));
            checkValues(negTestValue, NativeImpl.isZero(negTestValue), Decimal64Utils.isZero(negTestValue));

            checkValues(testValue, NativeImpl.isNonZero(testValue), Decimal64Utils.isNonZero(testValue));
            checkValues(negTestValue, NativeImpl.isNonZero(negTestValue), Decimal64Utils.isNonZero(negTestValue));

            if (!Decimal64Utils.isNaN(testValue)) {
                checkValues(testValue, NativeImpl.isPositive(testValue), Decimal64Utils.compareTo(testValue, Decimal64Utils.ZERO) > 0);
                checkValues(negTestValue, NativeImpl.isPositive(negTestValue), Decimal64Utils.compareTo(negTestValue, Decimal64Utils.ZERO) > 0);

                checkValues(testValue, NativeImpl.isNonPositive(testValue), Decimal64Utils.compareTo(testValue, Decimal64Utils.ZERO) <= 0);
                checkValues(negTestValue, NativeImpl.isNonPositive(negTestValue), Decimal64Utils.compareTo(negTestValue, Decimal64Utils.ZERO) <= 0);

                checkValues(testValue, NativeImpl.isNegative(testValue), Decimal64Utils.compareTo(testValue, Decimal64Utils.ZERO) < 0);
                checkValues(negTestValue, NativeImpl.isNegative(negTestValue), Decimal64Utils.compareTo(negTestValue, Decimal64Utils.ZERO) < 0);

                checkValues(testValue, NativeImpl.isNonNegative(testValue), Decimal64Utils.compareTo(testValue, Decimal64Utils.ZERO) >= 0);
                checkValues(negTestValue, NativeImpl.isNonNegative(negTestValue), Decimal64Utils.compareTo(negTestValue, Decimal64Utils.ZERO) >= 0);
            }

            checkValues(testValue, NativeImpl.isZero(testValue), Decimal64Utils.compareTo(testValue, Decimal64Utils.ZERO) == 0);
            checkValues(negTestValue, NativeImpl.isZero(negTestValue), Decimal64Utils.compareTo(negTestValue, Decimal64Utils.ZERO) == 0);

            checkValues(testValue, NativeImpl.isNonZero(testValue), Decimal64Utils.compareTo(testValue, Decimal64Utils.ZERO) != 0);
            checkValues(negTestValue, NativeImpl.isNonZero(negTestValue), Decimal64Utils.compareTo(negTestValue, Decimal64Utils.ZERO) != 0);
        }
    }

    private static void checkValues(@Decimal final long value, final boolean refCond, final boolean testCond) {
        if (refCond != testCond)
            throw new RuntimeException("TestValue(=" + Decimal64Utils.toString(value) + ") check error: refCond(=" + refCond + ") != testCond(" + testCond + ").");
    }
}

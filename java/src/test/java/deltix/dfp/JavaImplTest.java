package deltix.dfp;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static deltix.dfp.TestUtils.assertDfp;
import static deltix.dfp.TestUtils.assertDfpEq;
import static org.junit.Assert.*;

public class JavaImplTest {
    private Random random = new Random();

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

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, false)) == JavaImpl.ZERO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS-1, false)) == JavaImpl.ZERO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, true)) == JavaImpl.ZERO);

        // equals
        assertTrue(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, true)), JavaImpl.ZERO));
        assertTrue(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS-1, false)), JavaImpl.ZERO));

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, true)) == JavaImpl.negate(JavaImpl.ZERO));
        assertTrue(JavaImpl.negate(JavaImpl.fromParts(new Decimal64Parts(0, EXPONENT_BIAS, true))) == JavaImpl.ZERO);

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(2, EXPONENT_BIAS, false)) == Decimal64Utils.TWO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(20, EXPONENT_BIAS-1, false)) == Decimal64Utils.TWO);
        assertFalse(JavaImpl.fromParts(new Decimal64Parts(2, EXPONENT_BIAS, true)) == Decimal64Utils.TWO);

        // equals
        assertFalse(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(1000, EXPONENT_BIAS, true)), Decimal64Utils.THOUSAND));
        assertTrue(Decimal64Utils.equals(JavaImpl.fromParts(new Decimal64Parts(10000, EXPONENT_BIAS-1, false)), Decimal64Utils.THOUSAND));

        assertTrue(JavaImpl.fromParts(new Decimal64Parts(1000, EXPONENT_BIAS, true)) == JavaImpl.negate(Decimal64Utils.THOUSAND));
        assertTrue(JavaImpl.negate(JavaImpl.fromParts(new Decimal64Parts(1000, EXPONENT_BIAS, true))) == Decimal64Utils.THOUSAND);
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
        int N = 10000;
        int m = Integer.MIN_VALUE; // Test min value first
        for (int i = 0; i < N ; ++i) {
            final long dfp = (i & 1) > 0 ? JavaImpl.fromInt32(m) : JavaImpl.fromInt32V2(m);
            assertEquals(Decimal64Utils.toInt(dfp), m);
            assertDfp(NativeImpl.fromInt32(m), dfp);
            assertDfp(NativeImpl.fromInt64(m), dfp);
            assertDfp(NativeImpl.fromFloat64(m), dfp);
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
    }

    @Test
    public void fromFixedPointFastConsts() {
        assertDfp(Decimal64Utils.ZERO, JavaImpl.fromFixedPointFast(0, 0));
        assertDfp(Decimal64Utils.ONE, JavaImpl.fromFixedPointFast(1, 0));
        assertDfp(Decimal64Utils.TWO, JavaImpl.fromFixedPointFast(2, 0));
        assertDfp(Decimal64Utils.TEN, JavaImpl.fromFixedPointFast(10, 0));
        assertDfpEq(Decimal64Utils.TEN, JavaImpl.fromFixedPointFast(1, -1));
        assertDfp(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFast(100, 0));
        assertDfpEq(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFast(1, -2));
        assertDfpEq(Decimal64Utils.THOUSAND, JavaImpl.fromFixedPointFast(1, -3));
        assertDfpEq(Decimal64Utils.MILLION, JavaImpl.fromFixedPointFast(1, -6));
        assertDfp(Decimal64Utils.ONE_TENTH, JavaImpl.fromFixedPointFast(1, 1));
        assertDfp(Decimal64Utils.ONE_HUNDREDTH, JavaImpl.fromFixedPointFast(1, 2));

        assertDfp(Decimal64Utils.ZERO, JavaImpl.fromFixedPointFastUnsigned(0, 0));
        assertDfp(Decimal64Utils.ONE, JavaImpl.fromFixedPointFastUnsigned(1, 0));
        assertDfp(Decimal64Utils.TWO, JavaImpl.fromFixedPointFastUnsigned(2, 0));
        assertDfp(Decimal64Utils.TEN, JavaImpl.fromFixedPointFastUnsigned(10, 0));
        assertDfpEq(Decimal64Utils.TEN, JavaImpl.fromFixedPointFastUnsigned(1, -1));
        assertDfp(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFastUnsigned(100, 0));
        assertDfpEq(Decimal64Utils.HUNDRED, JavaImpl.fromFixedPointFastUnsigned(1, -2));
        assertDfpEq(Decimal64Utils.THOUSAND, JavaImpl.fromFixedPointFastUnsigned(1, -3));
        assertDfpEq(Decimal64Utils.MILLION, JavaImpl.fromFixedPointFastUnsigned(1, -6));
    }

    @Test
    public void fromFixedPointFast() {
        int N = 10000;
        for (int exp = 398 - 0x2FF; exp <= 398; ++exp) {
            for (int j = 0; j < N; ++j) {
                int mantissa = random.nextInt();
                assertDfp(NativeImpl.fromFixedPoint32(mantissa, exp), JavaImpl.fromFixedPointFast(mantissa, exp));
                assertDfp(NativeImpl.fromFixedPoint64(mantissa, exp), JavaImpl.fromFixedPointFast(mantissa, exp));

                if (mantissa >= 0) {
                    assertDfp(NativeImpl.fromFixedPoint32(mantissa, exp), JavaImpl.fromFixedPointFastUnsigned(mantissa, exp));
                    assertDfp(NativeImpl.fromFixedPoint64(mantissa, exp), JavaImpl.fromFixedPointFastUnsigned(mantissa, exp));
                }
            }

            assertDfp(NativeImpl.fromFixedPoint32(0, exp), JavaImpl.fromFixedPointFast(0, exp));
            assertDfp(NativeImpl.fromFixedPoint32(Integer.MIN_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MIN_VALUE, exp));
            assertDfp(NativeImpl.fromFixedPoint32(Integer.MAX_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MAX_VALUE, exp));
            assertDfp(NativeImpl.fromFixedPoint64(0, exp), JavaImpl.fromFixedPointFast(0, exp));
            assertDfp(NativeImpl.fromFixedPoint64(Integer.MIN_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MIN_VALUE, exp));
            assertDfp(NativeImpl.fromFixedPoint64(Integer.MAX_VALUE, exp), JavaImpl.fromFixedPointFast(Integer.MAX_VALUE, exp));
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
    public void checkToString(final String message, final String hex, final String expectedPlain, final String expectedScientific) {
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
}

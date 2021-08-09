package com.epam.deltix.dfp;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static com.epam.deltix.dfp.TestUtils.*;


public class FromDoubleTest {

    public void decimalConversionSample() {
        String[] v = {
            "9.200000000000000", "9.199999999999999", "9.200000000000001", "9.200000000000002", "9.200000000000003",
            "9.200000000000005", "9.200000000000006", "9.200000000000007", "9.200000000000008", "9.200000000000009",
            "9.200000000000000E100", "9.199999999999999E100", "9.200000000000001E100", "9.200000000000002E100", "9.200000000000003E100",
            "9.200000000000005E100", "9.200000000000006E100", "9.200000000000007E100", "9.200000000000008E100", "9.200000000000009E100"
        };

        applyTo(
            new Consumer<String>() {
                @Override
                public void accept(String s) {
                    decimalAndDoubleConversion(s);
                }
            }, v);
        applyTo(
            new Consumer<String>() {
                @Override
                public void accept(String s) {
                    bigDecimalAndDoubleConversion(s);
                }
            }, v);
    }

    private void decimalAndDoubleConversion(String s) {
        Decimal64 x0 = Decimal64.parse(s);
        double x1 = x0.toDouble();
        Decimal64 x2 = Decimal64.fromDouble(x1);
        double x3 = x2.toDouble();
        System.out.printf("D64: %17s  ->f64: %17s  ->f64->D64: %17s ->f64->D64->f64: %17s%n", x0, x1, x2, x3);
    }

    private void bigDecimalAndDoubleConversion(String s) {
        BigDecimal y0 = new BigDecimal(s);
        double y1 = y0.doubleValue();
        BigDecimal y2 = new BigDecimal(y1);
        double y3 = y2.doubleValue();
        System.out.printf("BD64: %17s  ->f64: %17s  ->f64->BD64: %17s ->f64->BD64->f64: %17s%n", y0, y1, y2, y3);
    }


    // @Test
    public void etc() {
        decimalConversionSample();
        System.out.print(new BigDecimal(9.2));
    }


    boolean checkDoubleConversionDefault(Decimal64 x) {
        return x.equals(Decimal64.fromDouble(x.toDouble()));
    }

    private static void checkDecimalDoubleConversion(Decimal64 x, String s) {
        Decimal64 x2 = Decimal64.fromDecimalDouble(x.toDouble());
        assertDecimalEqual(x, x2, "fromDecimalDouble(x.toDouble()) failed:");
        assertDecimalIdentical(x.canonize(), x2, "fromDecimalDouble(x.toDouble()) failed:");

        if (null != s) {
            Assert.assertEquals(x.toDouble(), Double.parseDouble(s), 0);
            if (-1 == s.indexOf("E")) {
                Assert.assertEquals(s, x2.toString());
            }
        }
    }

    private static void checkDecimalDoubleConversion(Decimal64 x) {
        checkDecimalDoubleConversion(x, null);
    }


    private static void checkDecimalDoubleConversion(String s) {
        checkDecimalDoubleConversion(Decimal64.fromUnderlying(decimalFromString(s)), s);
    }


    @Test
    public void testFromDecimalDoubleConversionSpecial() {
        // Tests for "Special" number handling
        applyTo(
            new Consumer<String>() {
                @Override
                public void accept(String s) {
                    FromDoubleTest.checkDecimalDoubleConversion(s);
                }
            },
            "0", "NaN", "Infinity", "-Infinity",
            "1E0", "1000000000000000E0",
            "0.000000009412631", "0.95285752",
            "9.2", "25107188000000000000000000000000000000000000000000000000",
            // Exponent limits
            "-1E-308", "-1000000000000000E-322"
        );
    }

    @Test
    public void testFromDecimalDoubleConversion() {
        for (int i = 0; i < N; i++) {
            Decimal64 x = getRandomDecimal();
            checkDecimalDoubleConversion(x);
        }
    }

    @Test
    public void mantissaZerosCombinationsTest() {
        mantissaZerosCombinations(
            new BiConsumer<Long, Integer>() {
                @Override
                public void accept(Long m, Integer l) {
                    checkDecimalDoubleConversion(Decimal64.fromFixedPoint(m, l));
                }
            });
        mantissaZerosCombinations(
            new BiConsumer<Long, Integer>() {
                @Override
                public void accept(Long m, Integer l) {
                    checkDecimalDoubleConversion(Decimal64.fromFixedPoint(m / POWERS_OF_TEN[l], 0));
                }
            });
    }

    @Test
    public void extremeValuesOfExponentTest() {
        Decimal64 x = Decimal64.fromFixedPoint(1, 383 + 15);
        assertDecimalIdentical(x, Decimal64.MIN_POSITIVE_VALUE);
        assertDecimalIdentical(x.canonize(), Decimal64.MIN_POSITIVE_VALUE);
        x = Decimal64.fromFixedPoint(100, 400);
        assertDecimalIdentical(x.canonize(), Decimal64.MIN_POSITIVE_VALUE);
        x = Decimal64.fromFixedPoint(1000000000000000L, 413);
        assertDecimalIdentical(x.canonize(), Decimal64.MIN_POSITIVE_VALUE);

        x = Decimal64.fromFixedPoint(-1, 383 + 15);
        assertDecimalIdentical(x.canonize(), Decimal64.MAX_NEGATIVE_VALUE);

        x = Decimal64.fromFixedPoint(-1, 308);
        checkDecimalDoubleConversion(x);
        x = Decimal64.fromFixedPoint(-1000000000000000L, 322);
        checkDecimalDoubleConversion(x);
        x = Decimal64.fromFixedPoint(1, 0);
        checkDecimalDoubleConversion(x);
        x = Decimal64.fromFixedPoint(1000000000000000L, 0);
        checkDecimalDoubleConversion(x);
    }

    static final int N = 5000000;
}

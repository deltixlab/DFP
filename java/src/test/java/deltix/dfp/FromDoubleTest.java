package deltix.dfp;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static deltix.dfp.JavaImpl.*;

public class FromDoubleTest {

    public void decimalConversionSample() {
        String[] v = {
            "9.200000000000000", "9.199999999999999", "9.200000000000001", "9.200000000000002", "9.200000000000003",
            "9.200000000000005", "9.200000000000006", "9.200000000000007", "9.200000000000008", "9.200000000000009",
            "9.200000000000000E100", "9.199999999999999E100", "9.200000000000001E100", "9.200000000000002E100", "9.200000000000003E100",
            "9.200000000000005E100", "9.200000000000006E100", "9.200000000000007E100", "9.200000000000008E100", "9.200000000000009E100"
        };

        for (int i = 0, n = v.length; i < n; i++) {
            String s = v[i];
            Decimal64 x0 = Decimal64.parse(s);
            double x1 = x0.toDouble();
            Decimal64 x2 = Decimal64.fromDouble(x1);
            double x3 = x2.toDouble();
            System.out.printf("D64: %17s  ->f64: %17s  ->f64->D64: %17s ->f64->D64->f64: %17s%n", x0, x1, x2, x3);

            BigDecimal y0 = new BigDecimal(s);
            double y1 = y0.doubleValue();
            BigDecimal y2 = new BigDecimal(y1);
            double y3 = y2.doubleValue();
            System.out.printf("BD64: %17s  ->f64: %17s  ->f64->BD64: %17s ->f64->BD64->f64: %17s%n", y0, y1, y2, y3);
        }
    }

//    @Test
//    public void etc() {
//        decimalConversionSample();
//        System.out.print(new BigDecimal(9.2));
//    }

    static final Random rng = new Random();
    int N = 5000000;

    Decimal64 getRandomDecimal(long maxMantissa) {
        long mantissa = rng.nextLong() % maxMantissa;
        int exp = (rng.nextInt() & 127) - 64;
        return Decimal64.fromFixedPoint(mantissa, exp);
    }

    Decimal64 getRandomDecimal() {
        return getRandomDecimal(10000000000000000L);
    }

    Decimal64 getRandomRoundedDecimal() {
        //return getRandomDecimal(10000000L);
        return getRandomDecimal(100000000L);
    }

    boolean testDoubleConversionDefault(Decimal64 x) {
        return x.equals(Decimal64.fromDouble(x.toDouble()));
    }

    boolean checkDoubleConversion(Decimal64 x, Decimal64 x2) {
        if (!x.equals(x2)) {
            Decimal64 delta = x2.subtract(x);
            long lDelta = Decimal64.toUnderlying(delta);

            //if ((lDelta & ((1<<53) - 1)) != 1)
                System.out.printf("FAIL: %s != %s,  delta = %s%n", printDecimal(x), printDecimal(x2), delta);

            return false;
        }
        return true;
    }

    private String printDecimal(Decimal64 x) {
        Decimal64Parts parts = new Decimal64Parts();
        toParts(x.value, parts);
        return String.format("%s (%s,%s)", x, parts.coefficient, parts.exponent + MIN_EXPONENT);
    }

    //@Test
    public void testDecimalFromDoubleConversions1() {
        for (int i = 0; i < N; i++) {
            Decimal64 x = getRandomRoundedDecimal();
            checkDoubleConversion(x, Decimal64.fromDouble(x.toDouble()));
        }
    }


    private void checkDecimalDoubleConversion(Decimal64 x, String s) {
        Decimal64 x2;
        checkDoubleConversion(x, x2 = Decimal64.fromDecimalDouble(x.toDouble()));
        if (!x.canonize().isIdentical(x2))
            Assert.fail(String.format("%s(%s, %x) != %s,%x", x, x.canonize(), Decimal64.toUnderlying(x.canonize()),
                x2, Decimal64.toUnderlying(x2)));

        if (null != s) {
            Assert.assertEquals(s, x2.toString());
        }
    }

    private void checkDecimalDoubleConversion(Decimal64 x) {
        checkDecimalDoubleConversion(x, null);
    }


    private void checkDecimalDoubleConversion(String s) {
        checkDecimalDoubleConversion(Decimal64.parse(s), s);
    }

    @Test
    public void testFromDecimalDoubleConversionSpecial() {
        // Tests for "Special" number handling

        checkDecimalDoubleConversion("0.000000009412631");
        checkDecimalDoubleConversion("0.95285752");
        checkDecimalDoubleConversion("NaN");
    }

    @Test
    public void testFromDecimalDoubleConversions1() {
        checkDecimalDoubleConversion("9.2");
        checkDecimalDoubleConversion("25107188000000000000000000000000000000000000000000000000");
    }

    @Test
    public void testFromDecimalDoubleConversions2() {

        for (int i = 0; i < N; i++) {
            Decimal64 x = getRandomRoundedDecimal();
            checkDecimalDoubleConversion(x);
        }
    }
}

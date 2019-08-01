package deltix.dfp;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

public class Decimal64Test {
    @Test
    public void unbox() {
        Assert.assertEquals(5L, Decimal64.toUnderlying(Decimal64.fromUnderlying(5L)));
        Assert.assertEquals(Decimal64Utils.NULL, Decimal64.toUnderlying(null));
    }

    @Test
    public void equality() {

        Decimal64 a = Decimal64.fromDouble(12345.6789);
        Decimal64 a2 = Decimal64.fromDouble(12345.6789);
        Decimal64 b = Decimal64.fromDouble(42);
        String s = "12345.6789";

        Assert.assertTrue("null == null", Decimal64.equals(null, (Object) null));
        Assert.assertTrue("null == null", Decimal64.equals(null, (Decimal64) null));
        Assert.assertTrue("null == null", Decimal64.isIdentical(null, (Object) null));
        Assert.assertTrue("null == null", Decimal64.isIdentical(null, (Decimal64) null));


        Assert.assertFalse("null != a", Decimal64.equals(null, (Object) a));
        Assert.assertFalse("null != a", Decimal64.equals(null, (Decimal64) a));
        Assert.assertFalse("null != a", Decimal64.isIdentical(null, (Object) a));
        Assert.assertFalse("null != a", Decimal64.isIdentical(null, (Decimal64) a));



        Assert.assertFalse("null != s", Decimal64.equals(null, (Object) s));
        Assert.assertFalse("null != s", Decimal64.equals(null, s));
        Assert.assertFalse("null != s", Decimal64.isIdentical(null, s));
        Assert.assertFalse("null != s", Decimal64.isIdentical(null, (Object)s));


        Assert.assertFalse("a != null ", Decimal64.equals(a, null));
        Assert.assertFalse("a != null ", Decimal64.isIdentical(a, null));

        Assert.assertFalse("a != null ", a.equals(null));
        Assert.assertFalse("a != null ", a.isIdentical(null));

        Assert.assertFalse("a != string", Decimal64.equals(a, s));
        Assert.assertFalse("a != string", Decimal64.isIdentical(a, s));
        Assert.assertFalse("a != string", a.equals(s));
        Assert.assertFalse("a != string", a.isIdentical(s));

        Assert.assertTrue("a == a", a == a);
        Assert.assertTrue("a == a", Decimal64.equals(a, a));
        Assert.assertTrue("a == a", Decimal64.isIdentical(a, a));
        Assert.assertTrue("a == a2", Decimal64.equals(a, a2));
        Assert.assertTrue("a == a2", Decimal64.isIdentical(a, a2));

            // Note: these comparisons may be of interest for Value Type Agent
        Assert.assertTrue("a != b", a != b);
        Assert.assertTrue("a != b", (Object)a != (Object)b);
        Assert.assertTrue("a != s", (Object)a != (Object) s);

        Assert.assertFalse("a != b", Decimal64.equals(a, b));
        Assert.assertFalse("a != b", Decimal64.isIdentical(a, b));

        Assert.assertTrue("+NaN == -NaN", Decimal64.equals(Decimal64.NaN, Decimal64.NaN.negate()));
        Assert.assertTrue("+NaN == -NaN", Decimal64.NaN.equals(Decimal64.NaN.negate()));
        Assert.assertTrue("+NaN == -NaN", Decimal64.equals(Decimal64.NaN.negate(), Decimal64.NaN));
        Assert.assertTrue("+NaN == -NaN", Decimal64.NaN.negate().equals(Decimal64.NaN));
        Assert.assertFalse("+Nan != -NaN", Decimal64.isIdentical(Decimal64.NaN, Decimal64.NaN.negate()));
        Assert.assertFalse("+NaN != -NaN", Decimal64.NaN.isIdentical(Decimal64.NaN.negate()));
        Assert.assertFalse("+Nan != -NaN", Decimal64.isIdentical(Decimal64.NaN.negate(), Decimal64.NaN));
        Assert.assertFalse("+NaN != -NaN", Decimal64.NaN.negate().isIdentical(Decimal64.NaN));

        // Equality-only tests for compareTo()
        Assert.assertFalse("+NaN != +NaN", 0 != Decimal64.NaN.compareTo(Decimal64.NaN));
        Assert.assertFalse("+NaN != -NaN", 0 != Decimal64.NaN.compareTo(Decimal64.NaN.negate()));
        Assert.assertFalse("+NaN != -NaN", 0 != Decimal64.NaN.negate().compareTo(Decimal64.NaN.negate()));

        Assert.assertTrue("a == a", 0 == a.compareTo(a));
        Assert.assertTrue("a == a2", 0 == a.compareTo(a2));
        Assert.assertFalse("a != b", 0 == a.compareTo(b));
        // TODO: Check if compareTo throws
    }

    @Test
    public void canonizeTest() {
        long al = Decimal64Utils.fromFixedPoint(10000, 4);
        long bl = Decimal64Utils.fromFixedPoint(1, 0);
        long cl = Decimal64Utils.fromFixedPoint(10, 1);
        long dl = Decimal64Utils.fromFixedPoint(100, 2);
        long el = Decimal64Utils.fromFixedPoint(1000, 3);

        Decimal64 a = Decimal64.fromUnderlying(al), b = Decimal64.fromUnderlying(bl), c = Decimal64.fromUnderlying(cl), d = Decimal64.fromUnderlying(dl), e = Decimal64.fromUnderlying(el);


        Assert.assertEquals(false, a.isIdentical(b));
        Assert.assertEquals(false, b.isIdentical(c));
        Assert.assertEquals(false, c.isIdentical(d));
        Assert.assertEquals(false, d.isIdentical(e));
        Assert.assertEquals(false, e.isIdentical(a));

        Assert.assertNotEquals(a.identityHashCode(), b.identityHashCode());
        Assert.assertNotEquals(b.identityHashCode(), c.identityHashCode());
        Assert.assertNotEquals(c.identityHashCode(), d.identityHashCode());
        Assert.assertNotEquals(d.identityHashCode(), e.identityHashCode());
        Assert.assertNotEquals(e.identityHashCode(), a.identityHashCode());

        Assert.assertEquals(true, a.equals(b));
        Assert.assertEquals(true, b.equals(c));
        Assert.assertEquals(true, c.equals(d));
        Assert.assertEquals(true, d.equals(e));
        Assert.assertEquals(true, e.equals(a));

        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertEquals(b.hashCode(), c.hashCode());
        Assert.assertEquals(c.hashCode(), d.hashCode());
        Assert.assertEquals(d.hashCode(), e.hashCode());
        Assert.assertEquals(e.hashCode(), a.hashCode());

        a = a.canonize();
        b = b.canonize();
        c = c.canonize();
        d = d.canonize();
        e = e.canonize();
        Assert.assertTrue(a.isIdentical(b));
        Assert.assertTrue(a.isIdentical(c));
        Assert.assertTrue(a.isIdentical(d));
        Assert.assertTrue(a.isIdentical(e));

        long nan1l = Decimal64Utils.NaN;
        long nan2l = nan1l + 20;
        Decimal64 nan1 = Decimal64.fromUnderlying(nan1l), nan2 = Decimal64.fromUnderlying(nan2l);

        Assert.assertEquals(false, nan1.isIdentical(nan2));
        Assert.assertNotEquals(nan1.identityHashCode(), nan2.identityHashCode());
        Assert.assertEquals(true, nan1.equals(nan2));
        Assert.assertEquals(nan1.hashCode(), nan2.hashCode());

        nan1 = nan1.canonize();
        nan2 = nan2.canonize();
        Assert.assertTrue(nan1.isIdentical(nan2));

        long posInf1l = Decimal64Utils.POSITIVE_INFINITY;
        long posInf2l = posInf1l + 10;

        Decimal64 posInf1 = Decimal64.fromUnderlying(posInf1l), posInf2 = Decimal64.fromUnderlying(posInf2l);

        Assert.assertEquals(false, posInf1.isIdentical(posInf2));
        Assert.assertNotEquals(posInf1.identityHashCode(), posInf2.identityHashCode());
        Assert.assertEquals(true, posInf1.equals(posInf2));
        Assert.assertEquals(posInf1.hashCode(), posInf2.hashCode());


        posInf1 = posInf1.canonize();
        posInf2 = posInf2.canonize();

        Assert.assertEquals(true, posInf1.isIdentical(posInf2));

        long negInf1l = Decimal64Utils.NEGATIVE_INFINITY;
        long negInf2l = negInf1l + 10;

        Decimal64 negInf1 = Decimal64.fromUnderlying(negInf1l), negInf2 = Decimal64.fromUnderlying(negInf2l);

        Assert.assertEquals(false, negInf1.isIdentical(negInf2));
        Assert.assertNotEquals(negInf1.identityHashCode(), negInf2.identityHashCode());
        Assert.assertEquals(true, negInf1.equals(negInf2));
        Assert.assertEquals(negInf1.hashCode(), negInf2.hashCode());


        negInf1 = negInf1.canonize();
        negInf2 = negInf2.canonize();
        Assert.assertEquals(true, negInf1.isIdentical(negInf2));

        long zero1l = Decimal64Utils.fromFixedPoint(0, 1);
        long zero2l = Decimal64Utils.fromFixedPoint(0, 2);

        Decimal64 zero1 = Decimal64.fromUnderlying(zero1l);
        Decimal64 zero2 = Decimal64.fromUnderlying(zero2l);

        Assert.assertEquals(false, zero1.isIdentical(zero2));
        Assert.assertNotEquals(zero1.identityHashCode(), zero2.identityHashCode());
        Assert.assertEquals(true, zero1.equals(zero2));
        Assert.assertEquals(zero1.hashCode(), zero2.hashCode());

        zero1 = zero1.canonize();
        zero2 = zero2.canonize();
        Assert.assertEquals(zero1, zero2);
    }

    @Test
    public void numberConversionTest() {
        for (long i0 = 0; i0 < 9999999999999999L; i0 = (i0 << 1) + 1) {
            long x = i0;
            for (int i = 0; i < 2; i++, x = -x) {
                Assert.assertEquals(x, (long)Decimal64.fromDouble(x).toDouble());
                Assert.assertEquals(x, (long)Decimal64.fromDouble(x).toLong());
                Assert.assertTrue(Decimal64.fromDouble(x).equals(Decimal64.fromLong(x)));
                Assert.assertTrue(Decimal64.fromLong(x).equals(Decimal64.fromDouble(x)));

                if (Math.abs(x) <= Integer.MAX_VALUE) {
                    Assert.assertEquals(x, (long) Decimal64.fromLong(x).toInt());
                    Assert.assertEquals(x, (long) Decimal64.fromInt((int) x).toDouble());
                    Assert.assertEquals(x, (long) Decimal64.fromInt((int) x).toLong());
                    Assert.assertEquals(x, (long) Decimal64.fromInt((int) x).toInt());
                    Assert.assertTrue(Decimal64.fromDouble(x).equals(Decimal64.fromInt((int) x)));
                    Assert.assertTrue(Decimal64.fromLong(x).equals(Decimal64.fromInt((int) x)));
                } else {
                    // Expect integer overflow at some stage
                    Assert.assertNotEquals(x, (long) Decimal64.fromLong(x).toInt());
                    Assert.assertNotEquals(x, (long) Decimal64.fromInt((int) x).toDouble());
                    Assert.assertNotEquals(x, (long) Decimal64.fromInt((int) x).toLong());
                    Assert.assertNotEquals(x, (long) Decimal64.fromInt((int) x).toInt());
                    Assert.assertFalse(Decimal64.fromDouble(x).equals(Decimal64.fromInt((int) x)));
                    Assert.assertFalse(Decimal64.fromLong(x).equals(Decimal64.fromInt((int) x)));
                }
            }
        }
    }

    @Test
    public void toStringTest() {

        Assert.assertEquals("0", Decimal64.fromLong(0).toString());
        Assert.assertEquals("42", Decimal64.fromLong(42).toString());
        Assert.assertEquals(String.valueOf(Integer.MAX_VALUE), Decimal64.fromInt(Integer.MAX_VALUE).toString());
        Assert.assertEquals(String.valueOf(Integer.MIN_VALUE), Decimal64.fromInt(Integer.MIN_VALUE).toString());
        Assert.assertEquals(String.valueOf(Integer.MAX_VALUE), Decimal64.fromLong(Integer.MAX_VALUE).toString());
        Assert.assertEquals(String.valueOf(Integer.MIN_VALUE), Decimal64.fromLong(Integer.MIN_VALUE).toString());
        Assert.assertEquals(String.valueOf(Integer.MAX_VALUE), Decimal64.fromFixedPoint(Integer.MAX_VALUE, 0).toString());
        Assert.assertEquals(String.valueOf(Integer.MIN_VALUE), Decimal64.fromFixedPoint(Integer.MIN_VALUE, 0).toString());
        Assert.assertEquals("123.456", Decimal64.fromDouble(123.456).toString());
        Assert.assertEquals("123.4567", Decimal64.fromFixedPoint(1234567, 4).toString());

        Assert.assertEquals("null", String.valueOf((Object)null));
        Assert.assertEquals("null", String.valueOf((Decimal64)null));
        Assert.assertEquals("null", Decimal64.toString(null));
        Assert.assertEquals("null", Decimal64Utils.toString(Decimal64.toUnderlying((Decimal64)null)));

        Assert.assertEquals("NaN", String.valueOf(Decimal64.NaN));
        Assert.assertEquals("NaN", Decimal64.toString(Decimal64.NaN.negate()));
        Assert.assertEquals("Infinity", Decimal64.toString(Decimal64.POSITIVE_INFINITY));
        Assert.assertEquals("-Infinity", Decimal64.toString(Decimal64.NEGATIVE_INFINITY));

        Assert.assertEquals("0", Decimal64.toString(Decimal64.ZERO));
        Assert.assertEquals("1000000", Decimal64.toString(Decimal64.MILLION));
        Assert.assertEquals("0.01", Decimal64.toString(Decimal64.ONE_HUNDREDTH));
    }
}

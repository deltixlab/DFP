package deltix.dfp;

import junit.framework.TestCase;
import org.junit.Test;

import static deltix.dfp.TestUtils.assertDfp;
import static deltix.dfp.TestUtils.assertDfpEq;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class NativeImplTest {

    @Test
    public void fromInt32() {

        assertDfpEq(JavaImpl.ZERO, NativeImpl.fromInt32(0));
        assertDfp(JavaImpl.ZERO, NativeImpl.fromInt32(0));

        assertDfp(Decimal64Utils.ONE, NativeImpl.fromInt32(1));
        assertDfp(Decimal64Utils.MILLION, NativeImpl.fromInt32(1000000));
        assertDfp(Decimal64Utils.fromDouble(-1000000.0), NativeImpl.fromInt32(-1000000));
    }

    /**
     * Test binary and numeric values of the constants defined in Decimal64Utils
     * Decimal64Utils static constructor and tests are supposed to only be dependent from JavaImpl class, not NativeImpl
     * and therefore we want a separate test to verify the validity of its constants that invoked NativeImpl
     */
    @Test
    public void testConstants() {

        assertDfp(NativeImpl.fromInt32(0), Decimal64Utils.ZERO);
        assertDfp(NativeImpl.fromInt32(1), Decimal64Utils.ONE);
        assertDfp(NativeImpl.fromInt32(2), Decimal64Utils.TWO);
        assertDfp(NativeImpl.fromInt32(10), Decimal64Utils.TEN);
        assertDfp(NativeImpl.fromInt32(100), Decimal64Utils.HUNDRED);
        assertDfp(NativeImpl.fromInt32(1000), Decimal64Utils.THOUSAND);
        assertDfp(NativeImpl.fromInt32(1000_000), Decimal64Utils.MILLION);
        assertDfp(NativeImpl.fromFixedPoint32(1, 1), Decimal64Utils.ONE_TENTH);
        assertDfp(NativeImpl.fromFixedPoint32(1, 2), Decimal64Utils.ONE_HUNDREDTH);

        assertDfpEq(NativeImpl.fromFloat64(0), Decimal64Utils.ZERO);
        assertDfpEq(NativeImpl.fromFloat64(1), Decimal64Utils.ONE);
        assertDfpEq(NativeImpl.fromFloat64(2), Decimal64Utils.TWO);
        assertDfpEq(NativeImpl.fromFloat64(10), Decimal64Utils.TEN);
        assertDfpEq(NativeImpl.fromFloat64(100), Decimal64Utils.HUNDRED);
        assertDfpEq(NativeImpl.fromFloat64(1000), Decimal64Utils.THOUSAND);
        assertDfpEq(NativeImpl.fromFloat64(1000_000), Decimal64Utils.MILLION);
        assertDfpEq(NativeImpl.fromFloat64(0.1), Decimal64Utils.ONE_TENTH);
        assertDfpEq(NativeImpl.fromFloat64(0.01), Decimal64Utils.ONE_HUNDREDTH);
    }
}

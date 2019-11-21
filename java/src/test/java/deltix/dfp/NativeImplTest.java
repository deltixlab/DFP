package deltix.dfp;

import org.junit.Test;

import static deltix.dfp.TestUtils.assertDecimalIdentical;
import static deltix.dfp.TestUtils.assertDecimalEqual;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class NativeImplTest {

    @Test
    public void fromInt32() {

        assertDecimalEqual(JavaImpl.ZERO, NativeImpl.fromInt32(0));
        assertDecimalIdentical(JavaImpl.ZERO, NativeImpl.fromInt32(0));

        assertDecimalIdentical(Decimal64Utils.ONE, NativeImpl.fromInt32(1));
        assertDecimalIdentical(Decimal64Utils.MILLION, NativeImpl.fromInt32(1000000));
        assertDecimalIdentical(Decimal64Utils.fromDouble(-1000000.0), NativeImpl.fromInt32(-1000000));
    }

    /**
     * Test binary and numeric values of the constants defined in Decimal64Utils
     * Decimal64Utils static constructor and tests are supposed to only be dependent from JavaImpl class, not NativeImpl
     * and therefore we want a separate test to verify the validity of its constants that invoked NativeImpl
     */
    @Test
    public void testConstants() {

        assertDecimalIdentical(NativeImpl.fromInt32(0), Decimal64Utils.ZERO);
        assertDecimalIdentical(NativeImpl.fromInt32(1), Decimal64Utils.ONE);
        assertDecimalIdentical(NativeImpl.fromInt32(2), Decimal64Utils.TWO);
        assertDecimalIdentical(NativeImpl.fromInt32(10), Decimal64Utils.TEN);
        assertDecimalIdentical(NativeImpl.fromInt32(100), Decimal64Utils.HUNDRED);
        assertDecimalIdentical(NativeImpl.fromInt32(1000), Decimal64Utils.THOUSAND);
        assertDecimalIdentical(NativeImpl.fromInt32(1000_000), Decimal64Utils.MILLION);
        assertDecimalIdentical(NativeImpl.fromFixedPoint32(1, 1), Decimal64Utils.ONE_TENTH);
        assertDecimalIdentical(NativeImpl.fromFixedPoint32(1, 2), Decimal64Utils.ONE_HUNDREDTH);

        assertDecimalEqual(NativeImpl.fromFloat64(0), Decimal64Utils.ZERO);
        assertDecimalEqual(NativeImpl.fromFloat64(1), Decimal64Utils.ONE);
        assertDecimalEqual(NativeImpl.fromFloat64(2), Decimal64Utils.TWO);
        assertDecimalEqual(NativeImpl.fromFloat64(10), Decimal64Utils.TEN);
        assertDecimalEqual(NativeImpl.fromFloat64(100), Decimal64Utils.HUNDRED);
        assertDecimalEqual(NativeImpl.fromFloat64(1000), Decimal64Utils.THOUSAND);
        assertDecimalEqual(NativeImpl.fromFloat64(1000_000), Decimal64Utils.MILLION);
        assertDecimalEqual(NativeImpl.fromFloat64(0.1), Decimal64Utils.ONE_TENTH);
        assertDecimalEqual(NativeImpl.fromFloat64(0.01), Decimal64Utils.ONE_HUNDREDTH);
    }
}

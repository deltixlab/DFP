package deltix.dfp;

import static junit.framework.TestCase.assertEquals;

public class TestUtils {
    public static void assertDfp(long expected, long actual) {
        if (expected != actual)
            assertEquals(Decimal64Utils.toDebugString(expected), Decimal64Utils.toDebugString(actual));
    }

    public static void assertDfpEq(long expected, long actual) {
        if (!Decimal64Utils.equals(expected, actual))
            assertEquals(Decimal64Utils.toDebugString(expected), Decimal64Utils.toDebugString(actual));
    }
}

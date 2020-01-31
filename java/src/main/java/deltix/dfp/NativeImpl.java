package deltix.dfp;

import rtmath.utilities.ResourceLoader;

class NativeImpl {
    static final String NATIVE_API_VERSION = "2";

    static {
        try {
            String version = new Decimal64Parts().getClass().getPackage().getImplementationVersion();
            if (version == null)
                version = Version.version;

            boolean alwaysOverwrite = false;

            if (version.endsWith("-SNAPSHOT")) {
                //alwaysOverwrite = true;
                version = version.substring(0, version.lastIndexOf('-'));
            }
            ResourceLoader
                //.from(NativeImpl.class, "$(OS)/$(ARCH)/*") // This version now also works, but is probably less efficient
                .from(NativeImpl.class, "$(OS)/$(ARCH)/DecimalNative.$(DLLEXT).zst")
                .to(".rtmath/dfp/" + version + ".0/$(ARCH)")
                .alwaysOverwrite(alwaysOverwrite)
                .tryRandomFallbackSubDirectory(true)
                .addDllSuffix(NATIVE_API_VERSION)
                .load();

            int ver = 0;
            ver = version();
            if (Integer.parseInt(NATIVE_API_VERSION) != ver)
                throw new UnsatisfiedLinkError(String.format("Native library API version mismatch. Found v%s, expected: v" + NATIVE_API_VERSION, ver));
        } catch (Throwable exception) {
            //exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    public static native int version();

    /// region Conversion & Rounding

    public static native long fromFixedPoint64(final long mantissa, final int numberOfDigits);

    public static native long fromFixedPoint32(final int mantissa, final int numberOfDigits);

    public static native long toFixedPoint(final long value, final int numberOfDigits);

    public static native long fromInt64(final long value);

    public static native long toInt64(final long value);

    public static native long fromInt32(final int value);

    public static native int toInt32(final long value);

    public static native long fromFloat64(final double value);

    public static native double toFloat64(final long value);

    public static native long roundTowardsPositiveInfinity(final long value);

    public static native long roundTowardsNegativeInfinity(final long value);

    public static native long roundTowardsZero(final long value);

    public static native long roundToNearestTiesAwayFromZero(final long value);

    /// endregion

    /// region Classification

    public static native boolean isNaN(final long value);

    public static native boolean isInfinity(final long value);

    public static native boolean isPositiveInfinity(final long value);

    public static native boolean isNegativeInfinity(final long value);

    public static native boolean isFinite(final long value);

    public static native boolean isNormal(final long value);

    public static native boolean signBit(final long value);

    /// endregion

    /// region Comparison

    public static native int compare(final long a, final long b);

    public static native boolean isEqual(final long a, final long b);

    public static native boolean isNotEqual(final long a, final long b);

    public static native boolean isLess(final long a, final long b);

    public static native boolean isLessOrEqual(final long a, final long b);

    public static native boolean isGreater(final long a, final long b);

    public static native boolean isGreaterOrEqual(final long a, final long b);

    public static native boolean isZero(final long value);

    public static native boolean isNonZero(final long value);

    public static native boolean isPositive(final long value);

    public static native boolean isNegative(final long value);

    public static native boolean isNonPositive(final long value);

    public static native boolean isNonNegative(final long value);

    /// endregion

    /// region Minimum & Maximum

    public static native long max2(final long a, final long b);

    public static native long max3(final long a, final long b, final long c);

    public static native long max4(final long a, final long b, final long c, final long d);

    public static native long min2(final long a, final long b);

    public static native long min3(final long a, final long b, final long c);

    public static native long min4(final long a, final long b, final long c, final long d);

    /// endregion

    /// region Arithmetic

    public static native long negate(final long value);

    public static native long abs(final long value);

    public static native long add2(final long a, final long b);

    public static native long add3(final long a, final long b, final long c);

    public static native long add4(final long a, final long b, final long c, final long d);

    public static native long subtract(final long a, final long b);

    public static native long multiply2(final long a, final long b);

    public static native long multiply3(final long a, final long b, final long c);

    public static native long multiply4(final long a, final long b, final long c, final long d);

    public static native long multiplyByInt32(final long a, final int b);

    public static native long multiplyByInt64(final long a, final long b);

    public static native long divide(final long a, final long b);

    public static native long divideByInt32(final long a, final int b);

    public static native long divideByInt64(final long a, final long b);

    public static native long multiplyAndAdd(final long a, final long b, final long c);

    public static native long scaleByPowerOfTen(final long a, final int n);

    public static native long mean2(final long a, final long b);

    /// endregion

    /// region Special

    public static native long nextUp(final long value);

    public static native long nextDown(final long value);

    /// endregion
}

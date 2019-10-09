package deltix.dfp;

import java.io.IOException;

/**
 * Contains common arithmetic routines for dfp floating point numbers as defined by IEEE-754 2008.
 * <p>
 * Decimal floating-point (DFP) arithmetic refers to both a representation and operations on dfp floating-point
 * numbers. Working directly with dfp (base-10) fractions can avoid the rounding errors that otherwise typically
 * occur when converting between dfp fractions (common in human-entered data, such as measurements or financial
 * information) and binary (base-2) fractions.
 */
public class Decimal64Utils {
    /// region Constants

    /**
     * A constant holding a Not-a-Number (NaN) value of dfp type.
     */
    @Decimal
    public static final long NaN = JavaImpl.NaN;

    /**
     * A constant holding the positive infinity of dfp type.
     */
    @Decimal
    public static final long POSITIVE_INFINITY = JavaImpl.POSITIVE_INFINITY;

    /**
     * A constant holding the negative infinity of dfp type.
     */
    @Decimal
    public static final long NEGATIVE_INFINITY = JavaImpl.NEGATIVE_INFINITY;

    /**
     * Maximum number of significand dfp digits that dfp value can store.
     */
    public static final int MAX_SIGNIFICAND_DIGITS = 16;

    /**
     * Number of bits used to represent a dfp value.
     */
    public static final int SIZE = Long.SIZE;

    /**
     * A constant holding the maximum possible dfp exponent for normalized values: {@code 384}.
     */
    public static final int MAX_EXPONENT = JavaImpl.MAX_EXPONENT;

    /**
     * A constant holding the minimum possible dfp exponent for normalized values: {@code -383}.
     */
    public static final int MIN_EXPONENT = JavaImpl.MIN_EXPONENT;

    /**
     * A constant holding the largest representable number: {@code 9999999999999999E+369}.
     */
    @Decimal
    public static final long MAX_VALUE = JavaImpl.MAX_VALUE;

    /**
     * A constant holding the smallest representable number: {@code -9999999999999999E+369}.
     */
    @Decimal
    public static final long MIN_VALUE = JavaImpl.MIN_VALUE;

    /**
     * A constant holding the smallest representable positive number: {@code 1E-398}.
     */
    @Decimal
    public static final long MIN_POSITIVE_VALUE = JavaImpl.MIN_POSITIVE_VALUE;

    /**
     * Largest negative number: {@code -1E-398}.
     */
    @Decimal
    public static final long MAX_NEGATIVE_VALUE = JavaImpl.MAX_NEGATIVE_VALUE;

    /**
     * Zero: @{code 0}.
     */
    @Decimal
    public static final long ZERO = JavaImpl.ZERO;

    @Decimal
    public static final long ONE = Decimal64Utils.fromInt(1);
    @Decimal
    public static final long TWO = Decimal64Utils.fromInt(2);
    @Decimal
    public static final long TEN = Decimal64Utils.fromInt(10);
    @Decimal
    public static final long HUNDRED = Decimal64Utils.fromInt(100);
    @Decimal
    public static final long THOUSAND = Decimal64Utils.fromInt(1000);
    @Decimal
    public static final long MILLION = Decimal64Utils.fromInt(1000_000);

    @Decimal
    public static final long ONE_TENTH = JavaImpl.fromFixedPointFastUnchecked(1, 1);
    @Decimal
    public static final long ONE_HUNDREDTH = JavaImpl.fromFixedPointFastUnchecked(1, 2);

    @Decimal
    public static final long NULL = JavaImpl.NULL;


    private static final int NaN_HASH_CODE = 1;
    private static final int POSITIVE_INF_HASH_CODE = 2;
    private static final int NEGATIVE_INF_HASH_CODE = 3;

    /// endregion

    /// region Object Implementation

    /**
     * Hash code of binary representation of given decimal.
     *
     * @param value Given decimal.
     * @return HashCode of given decimal.
     */
    public static int identityHashCode(final long value) {
        return (int) (value ^ (value >>> 32));
    }

    /**
     * Return hash code of arithmetic value of given decimal.
     *
     * We consider that all POSITIVE_INFINITYs have equal hashCode,
     * all NEGATIVE_INFINITYs have equal hashCode,
     * all NaNs have equal hashCode.
     *
     * @param value Given decimal.
     * @return HashCode of given decimal.
     */
    public static int hashCode(@Decimal final long value) {
        if (Decimal64Utils.isNaN(value)) {
            return NaN_HASH_CODE;
        }
        if (Decimal64Utils.isInfinity(value)) {
            if (Decimal64Utils.isPositiveInfinity(value)) {
                return POSITIVE_INF_HASH_CODE;
            } else {
                return NEGATIVE_INF_HASH_CODE;
            }
        }
        long canonizeValue = JavaImpl.canonize(value);
        return (int) (canonizeValue ^ (canonizeValue >>> 32));
    }


    public static String toString(@Decimal final long value) {
        return NULL == value ? "null" : appendTo(value, new StringBuilder()).toString();
    }

    static String toDebugString(@Decimal final long value) {
        return JavaImpl.toDebugString(value);
    }

    /**
     * Return true if two decimals represents the same arithmetic value.
     *
     * We consider that all POSITIVE_INFINITYs is equal to another POSITIVE_INFINITY,
     * all NEGATIVE_INFINITYs is equal to another NEGATIVE_INFINITY,
     * all NaNs is equal to another NaN.
     *
     * @param a First argument
     * @param b Second argument
     * @return True if two decimals represents the same arithmetic value.
     */
    public static boolean equals(@Decimal final long a, @Decimal final long b) {
        long canonizedA = canonize(a);
        long canonizedB = canonize(b);
        return canonizedA == canonizedB;
    }

    /**
     * Return true if two decimals have the same binary representation.
     *
     * @param a First argument.
     * @param b Second argument.
     * @return True if two decimals have the same binary representation.
     */
    public static boolean isIdentical(@Decimal final long a, @Decimal final long b) {
        return a == b;
    }

    public static boolean isIdentical(@Decimal final long a, final Object b) {
        return (b == null && NULL == a) || (b instanceof Decimal64 && (a == ((Decimal64) b).value));
    }

    public static boolean equals(@Decimal final long a, final Object b) {
        return (b == null && NULL == a)
            || (b instanceof Decimal64 && equals(a, ((Decimal64) b).value));
    }

    public static boolean isNull(@Decimal final long value) {
        return JavaImpl.isNull(value);
    }

    /// endregion

    /// region Conversion

    @Decimal
    public static long fromUnderlying(@Decimal final long value) {
        return value;
    }

    @Decimal
    public static long toUnderlying(@Decimal final long value) {
        return value;
    }

    /**
     * Converts fixed-point dfp to dfp floating-point value.
     * <p>
     * Essentially, the value behind the fixed-point dfp representation can be
     * computed as {@code mantissa} divided by 10 to the power of {@code numberOfDigits}.
     * <p>
     * E.g., 1.23 can be represented as pair (1230, 3), where 1230 is a mantissa, and 3 is a number of digits after
     * the dot. Thus, {@code Decimal64Utils.fromFixedPoint(1230, 3)} will return a dfp floating-point representation of
     * value {@code 1.23}.
     *
     * @param mantissa       Integer part of fixed-point dfp.
     * @param numberOfDigits Number of digits after the dot.
     * @return Floating-point dfp.
     */
    @Decimal
    public static long fromFixedPoint(final long mantissa, final int numberOfDigits) {
        // TODO: Can also create java version for this one
        return NativeImpl.fromFixedPoint64(mantissa, numberOfDigits);
    }

    // Creation from int is much faster, therefore there is a separate version created
    @Decimal
    public static long fromFixedPoint(final int mantissa, final int numberOfDigits) {
        return JavaImpl.fromFixedPoint32(mantissa, numberOfDigits);
    }


    @Decimal
    public static long fromDecimalDouble(double value) {
        return JavaImpl.fromDecimalDouble(value);
    }

    /**
     * Converts from floating-point dfp representation to fixed-point with given number of digits after the dot.
     * <p>
     * For example, dfp value of 1.23 can be represented as pair (1230, 3), where 1230 is a mantissa, and 3 is
     * a number of digits after the dot. Essentially, the value behind the fixed-point dfp representation can be
     * computed as {@code mantissa} divided by 10 to the power of {@code numberOfDigits}.
     *
     * @param value          64-bit floating point dfp value.
     * @param numberOfDigits Number of digits after the dot.
     * @return Floating-point dfp.
     */
    public static long toFixedPoint(@Decimal final long value, final int numberOfDigits) {
        return NativeImpl.toFixedPoint(value, numberOfDigits);
    }

    @Decimal
    public static long fromDouble(final double value) {
        return NativeImpl.fromFloat64(value);
    }

    public static double toDouble(@Decimal final long value) {
        return NativeImpl.toFloat64(value);
    }

    @Decimal
    public static long fromLong(final long value) {
        return NativeImpl.fromInt64(value);
    }

    public static long toLong(@Decimal final long value) {
        return NativeImpl.toInt64(value);
    }

    @Decimal
    public static long fromInt(final int value) {
        return JavaImpl.fromInt32(value);
    }

    public static int toInt(@Decimal final long value) {
        return NativeImpl.toInt32(value);
    }

    /// endregion

    /// region Classification

    public static boolean isNaN(@Decimal final long value) {
        return JavaImpl.isNaN(value);
    }

    public static boolean isInfinity(@Decimal final long value) {
        return JavaImpl.isInfinity(value);
    }

    public static boolean isPositiveInfinity(@Decimal final long value) {
        return JavaImpl.isPositiveInfinity(value);
    }

    public static boolean isNegativeInfinity(@Decimal final long value) {
        return JavaImpl.isNegativeInfinity(value);
    }

    public static boolean isFinite(@Decimal final long value) {
        return JavaImpl.isFinite(value);
    }

    public static boolean isNormal(@Decimal final long value) {
        return NativeImpl.isNormal(value);
    }

    /// endregion

    /// region Comparison

    public static int compareTo(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.compare(a, b);
    }

    public static boolean isEqual(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.isEqual(a, b);
    }

    public static boolean isNotEqual(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.isNotEqual(a, b);
    }

    public static boolean isLess(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.isLess(a, b);
    }

    public static boolean isLessOrEqual(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.isLessOrEqual(a, b);
    }

    public static boolean isGreater(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.isGreater(a, b);
    }

    public static boolean isGreaterOrEqual(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.isGreaterOrEqual(a, b);
    }

    public static boolean isZero(@Decimal final long value) {
        return JavaImpl.isZero(value);
    }

    public static boolean isNonZero(@Decimal final long value) {
        return NativeImpl.isNonZero(value);
    }

    public static boolean isPositive(@Decimal final long value) {
        return NativeImpl.isPositive(value);
    }

    public static boolean isNegative(@Decimal final long value) {
        return NativeImpl.isNegative(value);
    }

    public static boolean isNonPositive(@Decimal final long value) {
        return NativeImpl.isNonPositive(value);
    }

    public static boolean isNonNegative(@Decimal final long value) {
        return NativeImpl.isNonNegative(value);
    }

    /// endregion

    /// region Minimum & Maximum

    @Decimal
    public static long max(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.max2(a, b);
    }

    @Decimal
    public static long max(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        return NativeImpl.max3(a, b, c);
    }

    @Decimal
    public static long max(@Decimal final long a, @Decimal final long b, @Decimal final long c, @Decimal final long d) {
        return NativeImpl.max4(a, b, c, d);
    }

    @Decimal
    public static long min(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.min2(a, b);
    }

    @Decimal
    public static long min(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        return NativeImpl.min3(a, b, c);
    }

    @Decimal
    public static long min(@Decimal final long a, @Decimal final long b, @Decimal final long c, @Decimal final long d) {
        return NativeImpl.min4(a, b, c, d);
    }

    /// endregion

    /// region Arithmetic

    @Decimal
    public static long negate(@Decimal final long value) {
        return JavaImpl.negate(value);
    }

    @Decimal
    public static long abs(@Decimal final long value) {
        return JavaImpl.abs(value);
    }

    @Decimal
    public static long add(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.add2(a, b);
    }

    @Decimal
    public static long add(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        return NativeImpl.add3(a, b, c);
    }

    @Decimal
    public static long add(@Decimal final long a, @Decimal final long b, @Decimal final long c, @Decimal final long d) {
        return NativeImpl.add4(a, b, c, d);
    }

    @Decimal
    public static long subtract(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.subtract(a, b);
    }

    @Decimal
    public static long multiply(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.multiply2(a, b);
    }

    @Decimal
    public static long multiply(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        return NativeImpl.multiply3(a, b, c);
    }

    @Decimal
    public static long multiply(@Decimal final long a, @Decimal final long b, @Decimal final long c, @Decimal final long d) {
        return NativeImpl.multiply4(a, b, c, d);
    }

    @Decimal
    public static long multiplyByInteger(@Decimal final long a, final int b) {
        return NativeImpl.multiplyByInt32(a, b);
    }

    @Decimal
    public static long multiplyByInteger(@Decimal final long a, final long b) {
        return NativeImpl.multiplyByInt64(a, b);
    }

    @Decimal
    public static long divide(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.divide(a, b);
    }

    @Decimal
    public static long divideByInteger(@Decimal final long a, final int b) {
        return NativeImpl.divideByInt32(a, b);
    }

    @Decimal
    public static long divideByInteger(@Decimal final long a, final long b) {
        return NativeImpl.divideByInt64(a, b);
    }

    @Decimal
    public static long multiplyAndAdd(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        return NativeImpl.multiplyAndAdd(a, b, c);
    }

    @Decimal
    public static long scaleByPowerOfTen(@Decimal final long a, @Decimal final int n) {
        return NativeImpl.scaleByPowerOfTen(a, n);
    }

    @Decimal
    public static long average(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.mean2(a, b);
    }

    @Decimal
    public static long mean(@Decimal final long a, @Decimal final long b) {
        return NativeImpl.mean2(a, b);
    }

    /// endregion

    /// region Rounding

    @Decimal
    @Deprecated
    public static long ceil(@Decimal final long value) {
        return NativeImpl.roundTowardsPositiveInfinity(value);
    }

    @Decimal
    public static long ceiling(@Decimal final long value) {
        return NativeImpl.roundTowardsPositiveInfinity(value);
    }

    @Decimal
    public static long roundTowardsPositiveInfinity(@Decimal final long value) {
        return NativeImpl.roundTowardsPositiveInfinity(value);
    }

    @Decimal
    public static long floor(@Decimal final long value) {
        return NativeImpl.roundTowardsNegativeInfinity(value);
    }

    @Decimal
    public static long roundTowardsNegativeInfinity(@Decimal final long value) {
        return NativeImpl.roundTowardsNegativeInfinity(value);
    }

    @Decimal
    public static long truncate(@Decimal final long value) {
        return NativeImpl.roundTowardsZero(value);
    }

    @Decimal
    public static long roundTowardsZero(@Decimal final long value) {
        return NativeImpl.roundTowardsZero(value);
    }

    @Decimal
    public static long round(@Decimal final long value, @Decimal final long precision) {
        return roundToNearestTiesAwayFromZero(value, precision);
    }

    @Decimal
    public static long round(@Decimal final long value) {
        return NativeImpl.roundToNearestTiesAwayFromZero(value);
    }

    @Decimal
    public static long roundToNearestTiesAwayFromZero(@Decimal final long value) {
        return NativeImpl.roundToNearestTiesAwayFromZero(value);
    }

    @Decimal
    public static long roundTowardsPositiveInfinity(@Decimal final long value, @Decimal final long multiple) {
        if (!isFinite(multiple) || isNonPositive(multiple))
            throw new IllegalArgumentException("Multiple must be a positive finite number.");
        if (isNaN(value))
            return value;

        @Decimal final long ratio = ceiling(divide(value, multiple));
        return multiply(ratio, multiple);
    }

    @Decimal
    public static long roundTowardsNegativeInfinity(@Decimal final long value, @Decimal final long multiple) {
        if (!isFinite(multiple) || isNonPositive(multiple))
            throw new IllegalArgumentException("Multiple must be a positive finite number.");
        if (isNaN(value))
            return value;

        @Decimal final long ratio = floor(divide(value, multiple));
        return multiply(ratio, multiple);
    }

    @Decimal
    public static long roundToNearestTiesAwayFromZero(@Decimal final long value, @Decimal final long multiple) {
        if (!isFinite(multiple) || isNonPositive(multiple))
            throw new IllegalArgumentException("Multiple must be a positive finite number.");
        if (isNaN(value))
            return value;

        @Decimal final long ratio = NativeImpl.roundToNearestTiesAwayFromZero(divide(value, multiple));
        return multiply(ratio, multiple);
    }

    /// endregion

    /// region Special

    @Decimal
    public static long nextUp(@Decimal final long value) {
        return NativeImpl.nextUp(value);
    }

    @Decimal
    public static long nextDown(@Decimal final long value) {
        return NativeImpl.nextDown(value);
    }

    /**
     * Returns canonical representation of Decimal.
     * We consider that all binary representations of one arithmetic value have the same canonical binary representation.
     * Canonical representation of zeros = {@link #ZERO ZERO}
     * Canonical representation of NaNs = {@link #NaN NaN}
     * Canonical representation of POSITIVE_INFINITYs = {@link #POSITIVE_INFINITY POSITIVE_INFINITY}
     * Canonical representation of NEGATIVE_INFINITYs = {@link #NEGATIVE_INFINITY NEGATIVE_INFINITY}
     *
     * @param value Decimal argument.
     * @return Canonical representation of decimal argument.
     */
    @Decimal
    public static long canonize(@Decimal final long value) {
        if (Decimal64Utils.isNaN(value)) {
            return NaN;
        }
        if (Decimal64Utils.isInfinity(value)) {
            if (Decimal64Utils.isPositiveInfinity(value)) {
                return POSITIVE_INFINITY;
            } else {
                return NEGATIVE_INFINITY;
            }
        }
        return JavaImpl.canonize(value);
    }

    /// endregion

    /// region Parsing & Formatting

    public static Appendable appendTo(@Decimal final long value, final Appendable text) throws IOException {
        return JavaImpl.appendTo(value, text);
    }

    public static StringBuilder appendTo(@Decimal final long value, final StringBuilder text) {
        try {
            JavaImpl.appendTo(value, text);
            return text;
        } catch (IOException exception) {
            throw new RuntimeException("IO exception was unexpected.", exception);
        }
    }

    /**
     * Parses a dfp floating-point value from the given textual representation.
     * <p>
     * Besides regular floating-point values (possibly in scientific notation) the following special cases are accepted:
     * <ul>
     * <li>{@code +Inf}, {@code Inf}, {@code +Infinity}, {@code Infinity} in any character case result in
     * {@code Decimal64Utils.POSITIVE_INFINITY}</li>
     * <li>{@code -Inf}, {@code -Infinity} in any character case result in
     * {@code Decimal64Utils.NEGATIVE_INFINITY}</li>
     * <li>{@code +NaN}, {@code -NaN}, {@code NaN} in any character case result in
     * {@code Decimal64Utils.NaN}</li>
     * </ul>
     *
     * @param text       Textual representation of dfp floating-point value.
     * @param startIndex Index of character to start parsing at.
     * @param endIndex   Index of character to stop parsing at, non-inclusive.
     * @return 64-bit dfp floating-point.
     * @throws NumberFormatException if {@code text} does not contain valid dfp floating value.
     */
    @Decimal
    public static long parse(final CharSequence text, final int startIndex, final int endIndex) {
        return JavaImpl.parse(text, startIndex, endIndex, 0);
    }

    /**
     * Parses a dfp floating-point value from the given textual representation.
     * <p>
     * Besides regular floating-point values (possibly in scientific notation) the following special cases are accepted:
     * <ul>
     * <li>{@code +Inf}, {@code Inf}, {@code +Infinity}, {@code Infinity} in any character case result in
     * {@code Decimal64Utils.POSITIVE_INFINITY}</li>
     * <li>{@code -Inf}, {@code -Infinity} in any character case result in
     * {@code Decimal64Utils.NEGATIVE_INFINITY}</li>
     * <li>{@code +NaN}, {@code -NaN}, {@code NaN} in any character case result in
     * {@code Decimal64Utils.NaN}</li>
     * </ul>
     *
     * @param text       Textual representation of dfp floating-point value.
     * @param startIndex Index of character to start parsing at.
     * @return 64-bit dfp floating-point.
     * @throws NumberFormatException if {@code text} does not contain valid dfp floating value.
     */
    @Decimal
    public static long parse(final CharSequence text, final int startIndex) {
        return parse(text, startIndex, text.length());
    }

    /**
     * Parses a dfp floating-point value from the given textual representation.
     * <p>
     * Besides regular floating-point values (possibly in scientific notation) the following special cases are accepted:
     * <ul>
     * <li>{@code +Inf}, {@code Inf}, {@code +Infinity}, {@code Infinity} in any character case result in
     * {@code Decimal64Utils.POSITIVE_INFINITY}</li>
     * <li>{@code -Inf}, {@code -Infinity} in any character case result in
     * {@code Decimal64Utils.NEGATIVE_INFINITY}</li>
     * <li>{@code +NaN}, {@code -NaN}, {@code NaN} in any character case result in
     * {@code Decimal64Utils.NaN}</li>
     * </ul>
     *
     * @param text Textual representation of dfp floating-point value.
     * @return 64-bit dfp floating-point.
     * @throws NumberFormatException if {@code text} does not contain valid dfp floating value.
     */
    @Decimal
    public static long parse(final CharSequence text) {
        return parse(text, 0, text.length());
    }

    /**
     * Tries to parse a dfp floating-point value from the given textual representation.
     * Returns the default value in case of fail.
     *
     * @param text         Textual representation of dfp floating-point value.
     * @param startIndex   Index of character to start parsing at.
     * @param endIndex     Index of character to stop parsing at, non-inclusive.
     * @param defaultValue Default value in case of fail.
     * @return 64-bit dfp floating-point.
     */
    @Decimal
    public static long tryParse(final CharSequence text, final int startIndex, final int endIndex, @Decimal final long defaultValue) {
        @Decimal long value = defaultValue;

        try {
            value = parse(text, startIndex, endIndex);
        } catch (final NumberFormatException ignore) {
            // ignore
        }

        return value;
    }

    /**
     * Tries to parse a dfp floating-point value from the given textual representation.
     * Returns the default value in case of fail.
     *
     * @param text         Textual representation of dfp floating-point value.
     * @param startIndex   Index of character to start parsing at.
     * @param defaultValue Default value in case of fail.
     * @return 64-bit dfp floating-point.
     */
    @Decimal
    public static long tryParse(final CharSequence text, final int startIndex, @Decimal final long defaultValue) {
        return tryParse(text, startIndex, text.length(), defaultValue);
    }

    /**
     * Tries to parse a dfp floating-point value from the given textual representation.
     * Returns the default value in case of fail.
     *
     * @param text         Textual representation of dfp floating-point value.
     * @param defaultValue Default value in case of fail.
     * @return 64-bit dfp floating-point.
     */
    @Decimal
    public static long tryParse(final CharSequence text, @Decimal final long defaultValue) {
        return tryParse(text, 0, text.length(), defaultValue);
    }

    /// endregion

    /// region Null-checking wrappers for non-static methods

    static protected void checkNull(@Decimal final long value) {
        if (isNull(value)) {
            throw new NullPointerException();
        }
    }

    static protected void checkNull(@Decimal final long a, @Decimal final long b) {
        if (isNull(a) || isNull(b)) {
            throw new NullPointerException();
        }
    }

    public static long toFixedPointChecked(@Decimal final long value, final int numberOfDigits) {
        checkNull(value);
        return toFixedPoint(value, numberOfDigits);
    }

    public static double toDoubleChecked(@Decimal final long value) {
        checkNull(value);
        return toDouble(value);
    }

    @Decimal
    public static long fromLongChecked(final long value) {
        checkNull(value);
        return fromLong(value);
    }

    public static long toLongChecked(@Decimal final long value) {
        checkNull(value);
        return toLong(value);
    }

    @Decimal
    public static long fromIntChecked(final int value) {
        checkNull(value);
        return fromInt(value);
    }

    public static int toIntChecked(@Decimal final long value) {
        checkNull(value);
        return toInt(value);
    }

    @Decimal
    public static boolean isNaNChecked(@Decimal final long value) {
        checkNull(value);
        return isNaN(value);
    }

    @Decimal
    public static boolean isInfinityChecked(@Decimal final long value) {
        checkNull(value);
        return isInfinity(value);
    }

    @Decimal
    public static boolean isPositiveInfinityChecked(@Decimal final long value) {
        checkNull(value);
        return isPositiveInfinity(value);
    }

    public static boolean isNegativeInfinityChecked(@Decimal final long value) {
        checkNull(value);
        return isNegativeInfinity(value);
    }

    public static boolean isFiniteChecked(@Decimal final long value) {
        checkNull(value);
        return isFinite(value);
    }

    public static boolean isNormalChecked(@Decimal final long value) {
        checkNull(value);
        return isNormal(value);
    }

    public static boolean isIdenticalChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return a == b;
    }

    public static boolean isIdenticalChecked(@Decimal final long value, Object obj) {
        checkNull(value);
        return obj instanceof Decimal64 && value == ((Decimal64)obj).value;
    }

    public static boolean isEqualChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return isEqual(a, b);
    }

    public static boolean isNotEqualChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return isNotEqual(a, b);
    }

    public static boolean isLessChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return isLess(a, b);
    }

    public static boolean isLessOrEqualChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return isLessOrEqual(a, b);
    }

    public static boolean isGreaterChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return isGreater(a, b);
    }

    public static boolean isGreaterOrEqualChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return isGreaterOrEqual(a, b);
    }

    public static boolean isZeroChecked(@Decimal final long value) {
        checkNull(value);
        return isZero(value);
    }

    public static boolean isNonZeroChecked(@Decimal final long value) {
        checkNull(value);
        return isNonZero(value);
    }

    public static boolean isPositiveChecked(@Decimal final long value) {
        checkNull(value);
        return isPositive(value);
    }

    public static boolean isNegativeChecked(@Decimal final long value) {
        checkNull(value);
        return isNegative(value);
    }

    public static boolean isNonPositiveChecked(@Decimal final long value) {
        checkNull(value);
        return isNonPositive(value);
    }

    public static boolean isNonNegativeChecked(@Decimal final long value) {
        checkNull(value);
        return isNonNegative(value);
    }

    @Decimal
    public static long negateChecked(@Decimal final long value) {
        checkNull(value);
        return negate(value);
    }

    @Decimal
    public static long absChecked(@Decimal final long value) {
        checkNull(value);
        return abs(value);
    }

    @Decimal
    public static long addChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return add(a, b);
    }

    @Decimal
    public static long addChecked(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        checkNull(a, b);
        checkNull(c);
        return add(a, b, c);
    }

    @Decimal
    public static long addChecked(@Decimal final long a, @Decimal final long b,
                                  @Decimal final long c, @Decimal final long d) {
        checkNull(a, b);
        checkNull(c, d);
        return add(a, b, c, d);
    }

    @Decimal
    public static long subtractChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return subtract(a, b);
    }

    @Decimal
    public static long multiplyChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return multiply(a, b);
    }

    @Decimal
    public static long multiplyChecked(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        checkNull(a, b);
        checkNull(c);
        return multiply(a, b, c);
    }

    @Decimal
    public static long multiplyChecked(@Decimal final long a, @Decimal final long b,
                                       @Decimal final long c, @Decimal final long d) {
        checkNull(a, b);
        checkNull(c, d);
        return multiply(a, b, c, d);
    }

    @Decimal
    public static long divideChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return divide(a, b);
    }

    @Decimal
    public static long multiplyByIntegerChecked(@Decimal final long a, final int b) {
        checkNull(a);
        return multiplyByInteger(a, b);
    }

    @Decimal
    public static long multiplyByIntegerChecked(@Decimal final long a, final long b) {
        checkNull(a);
        return multiplyByInteger(a, b);
    }

    @Decimal
    public static long divideByIntegerChecked(@Decimal final long a, final int b) {
        checkNull(a);
        return divideByInteger(a, b);
    }

    @Decimal
    public static long divideByIntegerChecked(@Decimal final long a, final long b) {
        checkNull(a);
        return divideByInteger(a, b);
    }

    @Decimal
    public static long multiplyAndAddChecked(@Decimal final long a, @Decimal final long b, @Decimal final long c) {
        checkNull(a);
        checkNull(b, c);
        return multiplyAndAdd(a, b, c);
    }

    @Decimal
    public static long averageChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return average(a, b);
    }

    @Decimal
    public static long maxChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return max(a, b);
    }

    @Decimal
    public static long minChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return min(a, b);
    }

    @Decimal
    public static long ceilChecked(@Decimal final long value) {
        checkNull(value);
        return ceil(value);
    }

    @Decimal
    public static long floorChecked(@Decimal final long value) {
        checkNull(value);
        return floor(value);
    }

    @Decimal
    public static long roundChecked(@Decimal final long value) {
        checkNull(value);
        return round(value);
    }

    @Decimal
    public static long roundChecked(@Decimal final long value, final long precision) {
        checkNull(value);
        return round(value, precision);
    }

    @Decimal
    public static long ceilingChecked(@Decimal final long value) {
        checkNull(value);
        return ceiling(value);
    }

    @Decimal
    public static long roundTowardsPositiveInfinityChecked(@Decimal final long value) {
        checkNull(value);
        return roundTowardsPositiveInfinity(value);
    }

    @Decimal
    public static long roundTowardsNegativeInfinityChecked(@Decimal final long value) {
        checkNull(value);
        return roundTowardsNegativeInfinity(value);
    }

    @Decimal
    public static long truncateChecked(@Decimal final long value) {
        checkNull(value);
        return truncate(value);
    }

    @Decimal
    public static long roundTowardsZeroChecked(@Decimal final long value) {
        checkNull(value);
        return roundTowardsZeroChecked(value);
    }

    @Decimal
    public static long roundToNearestTiesAwayFromZeroChecked(@Decimal final long value) {
        checkNull(value);
        return roundToNearestTiesAwayFromZero(value);
    }

    @Decimal
    public static long roundTowardsPositiveInfinityChecked(@Decimal final long value, @Decimal final long multiple) {
        checkNull(value);
        return roundTowardsPositiveInfinity(value, multiple);
    }

    @Decimal
    public static long roundTowardsNegativeInfinityChecked(@Decimal final long value, @Decimal final long multiple) {
        checkNull(value);
        return roundTowardsNegativeInfinity(value, multiple);
    }

    @Decimal
    public static long roundToNearestTiesAwayFromZeroChecked(@Decimal final long value, @Decimal final long multiple) {
        checkNull(value);
        return roundToNearestTiesAwayFromZero(value, multiple);
    }

    public static int identityHashCodeChecked(@Decimal final long value) {
        checkNull(value);
        return identityHashCode(value);
    }


    public static int hashCodeChecked(@Decimal final long value) {
        checkNull(value);
        return hashCode(value);
    }

    public static Appendable appendToChecked(@Decimal long value, final Appendable appendable) throws IOException {
        checkNull(value);
        return appendTo(value, appendable);
    }

    public static StringBuilder appendToChecked(@Decimal long value, final StringBuilder builder) {
        checkNull(value);
        return appendTo(value, builder);
    }

    public static String toStringChecked(@Decimal final long value) {
        checkNull(value);
        return toString(value);
    }

    public static boolean equalsChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return equals(a, b);
    }

    public static boolean equalsChecked(@Decimal final long a, Object b) {
        checkNull(a);
        return equals(a, ((Decimal64)b).value);
    }

    @Decimal
    public static long nextUpChecked(@Decimal final long value) {
        checkNull(value);
        return nextUp(value);
    }

    @Decimal
    public static long nextDownChecked(@Decimal final long value) {
        checkNull(value);
        return nextDown(value);
    }

    @Decimal
    public static long canonizeChecked(@Decimal final long value) {
        checkNull(value);
        return canonize(value);
    }

    public static int intValueChecked(@Decimal final long value) {
        checkNull(value);
        return toInt(value);
    }

    public static long longValueChecked(@Decimal final long value) {
        checkNull(value);
        return toLong(value);
    }

    public static float floatValueChecked(@Decimal final long value) {
        checkNull(value);
        return (float) toDouble(value);
    }

    public static double doubleValueChecked(@Decimal final long value) {
        checkNull(value);
        return toDouble(value);
    }

    public static int compareToChecked(@Decimal final long a, @Decimal final long b) {
        checkNull(a, b);
        return compareTo(a, b);
    }

    // Required by Comparable<>
    public static int compareToChecked(@Decimal final long a, Object b) {
        checkNull(a);
        return compareTo(a, ((Decimal64)b).value);
    }

    /// endregion

    /// region Array boxing/unboxing (array conversions from long[] / to long[])

    public static Decimal64[] fromUnderlyingLongArray(@Decimal long[] src, int srcOffset, Decimal64[] dst, int dstOffset, int length) {

        int srcLength = src.length;
        int srcEndOffset = srcOffset + length;

        // NOTE: no bounds checks
        for (int i = 0; i < length; ++i) {
            dst[dstOffset + i] = Decimal64.fromUnderlying(src[srcOffset + i]);
        }

        return dst;
    }


    public static long[] toUnderlyingLongArray(Decimal64[] src, int srcOffset, @Decimal long[] dst, int dstOffset, int length) {

        int srcLength = src.length;
        int srcEndOffset = srcOffset + length;

        // NOTE: no bounds checks
        for (int i = 0; i < length; ++i) {
            dst[dstOffset + i] = Decimal64.toUnderlying(src[srcOffset + i]);
        }

        return dst;
    }

    public static @Decimal long[] toUnderlyingLongArray(Decimal64[] src) {
        return null == src ? null : toUnderlyingLongArray(src, 0, new long[src.length], 0, src.length);
    }

    public static Decimal64[] fromUnderlyingLongArray(@Decimal long[] src) {
        return null == src ? null : fromUnderlyingLongArray(src, 0, new Decimal64[src.length], 0, src.length);
    }

    /// endregion
}

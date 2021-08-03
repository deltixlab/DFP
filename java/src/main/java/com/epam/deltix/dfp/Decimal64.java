package com.epam.deltix.dfp;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Holds(wraps) a scalar 64-bit Decimal Floating Point value.
 * <p>
 * This class is immutable.
 * <p>
 * Every method that returns a {@code Decimal64}, returns a new instance.
 * <p>
 * Constructors are non-public. Can be instantiated only through static constructor methods.
 *
 * @see Decimal64Utils
 */
public class Decimal64 extends Number implements Comparable<Decimal64> {
    /// region Constants

    /**
     * Special null constant to be used with {@code Decimal64} instances.
     * Use this constant instead of Java 'null' to initialize or nullify Decimal64 scalar variables, or pass null as a
     * Decimal64 scalar argument.
     * If a variable is assigned with normal java null reference or left uninitialized, ValueType Agent may generate
     * less efficient code, in which case it will print a warning.
     * This limitation may be removed in the future versions of ValueType Agent.
     * Of course, you may use another constant to initialize Decimal64 variables, just don't leave them uninitialized.
     * You don't need to use this constant in equality comparisons. 'if (a == null)' is ok.
     * Also, you are not expected to use Decimal64Utils.NULL constant directly anywhere, if you work with Decimal64 class.
     *
     * @see Decimal64Utils#NULL
     */
    public static final Decimal64 NULL = null;

    /**
     * A constant holding canonical representation of Not-a-Number DFP value(not signaling NaN)
     */
    public static final Decimal64 NaN = new Decimal64(Decimal64Utils.NaN);

    /**
     * A constant holding canonical representation of Positive Infinity value
     */
    public static final Decimal64 POSITIVE_INFINITY = new Decimal64(Decimal64Utils.POSITIVE_INFINITY);

    /**
     * A constant holding canonical representation of Negative Infinity value
     */
    public static final Decimal64 NEGATIVE_INFINITY = new Decimal64(Decimal64Utils.NEGATIVE_INFINITY);

    /**
     * A constant holding the smallest representable number: {@code -9999999999999999E+369}
     */
    public static final Decimal64 MIN_VALUE = new Decimal64(Decimal64Utils.MIN_VALUE);

    /**
     * A constant holding the largest representable number: {@code 9999999999999999E+369}
     */
    public static final Decimal64 MAX_VALUE = new Decimal64(Decimal64Utils.MAX_VALUE);

    /**
     * A constant holding the smallest representable positive number: {@code 1E-398}
     */
    public static final Decimal64 MIN_POSITIVE_VALUE = new Decimal64(Decimal64Utils.MIN_POSITIVE_VALUE);

    /**
     * A constant holding the largest representable negative number: {@code -1E-398}
     */
    public static final Decimal64 MAX_NEGATIVE_VALUE = new Decimal64(Decimal64Utils.MAX_NEGATIVE_VALUE);

    /**
     * Zero: {@code 0}
     */
    public static final Decimal64 ZERO = new Decimal64(Decimal64Utils.ZERO);

    /**
     * One: {@code 1}
     */
    public static final Decimal64 ONE = new Decimal64(Decimal64Utils.ONE);

    /**
     * Two: {@code 2}
     */
    public static final Decimal64 TWO = new Decimal64(Decimal64Utils.TWO);

    /**
     * Ten: {@code 10}
     */
    public static final Decimal64 TEN = new Decimal64(Decimal64Utils.TEN);

    /**
     * One Hundred: {@code 100}
     */
    public static final Decimal64 HUNDRED = new Decimal64(Decimal64Utils.HUNDRED);

    /**
     * One Thousand: {@code 1000}
     */
    public static final Decimal64 THOUSAND = new Decimal64(Decimal64Utils.THOUSAND);

    /**
     * One million: {@code 1000_000}
     */
    public static final Decimal64 MILLION = new Decimal64(Decimal64Utils.MILLION);

    /**
     * One tenth: {@code 0.1}
     */
    public static final Decimal64 ONE_TENTH = new Decimal64(Decimal64Utils.ONE_TENTH);

    /**
     * One hundredth: {@code 0.01}
     */
    public static final Decimal64 ONE_HUNDREDTH = new Decimal64(Decimal64Utils.ONE_HUNDREDTH);

    /// endregion

    final long value;

    private Decimal64(final long value) {
        this.value = value;
    }

    /// region Conversion

    /**
     * Create {@code Decimal64} instance from underlying binary value (boxing operation).
     *
     * @param value 64-bit DFP value
     * @return new {@code Decimal64} instance
     */
    public static Decimal64 fromUnderlying(final long value) {
        return Decimal64Utils.NULL == value ? null : new Decimal64(value);
    }

    /**
     * Get binary representation as {@code long} (unboxing)
     *
     * @param obj {@code Decimal64} instance, {@code null} can be passed too
     * @return underlying binary representation as {@code long}
     */
    public static long toUnderlying(final Decimal64 obj) {
        return null == obj ? Decimal64Utils.NULL : obj.value;
    }

    /**
     * Create {@code Decimal64} instance from fixed point decimal value: (12345, 2) -&gt; 123.45
     *
     * @param mantissa       source fixed point value represented as {@code long}
     * @param numberOfDigits number of decimal digits representing fractional part
     * @return new {@code Decimal64} instance
     * @see Decimal64Utils#fromFixedPoint(long, int)
     */
    public static Decimal64 fromFixedPoint(final long mantissa, final int numberOfDigits) {
        return new Decimal64(Decimal64Utils.fromFixedPoint(mantissa, numberOfDigits));
    }

    /**
     * Create {@code Decimal64} instance from fixed point decimal value: (12345, 2) -&gt; 123.45
     * Overload of {@link #fromFixedPoint(long, int)} for mantissa representable by {@code int}.
     * Faster than the full-range version.
     *
     * @param mantissa       source fixed point value represented as {@code int}
     * @param numberOfDigits number of decimal digits representing fractional part
     * @return new {@code Decimal64} instance
     * @see Decimal64Utils#fromFixedPoint(int, int)
     */
    public static Decimal64 fromFixedPoint(final int mantissa, final int numberOfDigits) {
        return new Decimal64(Decimal64Utils.fromFixedPoint(mantissa, numberOfDigits));
    }

    public static Decimal64 fromDecimalDouble(final double value) {
        return new Decimal64(Decimal64Utils.fromDecimalDouble(value));
    }

    /**
     * Convert to fixed-point representation: (123.4567, 2) -&gt; 12346
     *
     * @param numberOfDigits number of decimal digits representing fractional part
     * @return fixed-point decimal value represented as @{code long}
     */
    public long toFixedPoint(final int numberOfDigits) {
        return Decimal64Utils.toFixedPoint(value, numberOfDigits);
    }

    /**
     * Create {@code Decimal64} instance from {@code long} integer
     * Faster than the full-range version.
     *
     * @param value source {@code long} integer value
     * @return new {@code Decimal64} instance
     */
    public static Decimal64 fromLong(final long value) {
        return new Decimal64(Decimal64Utils.fromLong(value));
    }

    /**
     * Convert {@code Decimal64} instance to {@code long} integer value by truncating fractional part towards zero
     *
     * @return {@code long} integer value
     */
    public long toLong() {
        return Decimal64Utils.toLong(value);
    }

    /**
     * Create {@code Decimal64} instance from {@code int}
     * <p>
     * Faster than the version that takes {@code long}.
     *
     * @param value source {@code int} value
     * @return new {@code Decimal64} instance
     */
    public static Decimal64 fromInt(final int value) {
        return new Decimal64(Decimal64Utils.fromInt(value));
    }

    /**
     * Convert {@code Decimal64} instance to {@code int} value by truncating fractional part towards zero
     *
     * @return {@code int} value
     */
    public int toInt() {
        return Decimal64Utils.toInt(value);
    }

    /**
     * Create {@code Decimal64} instance from 64-bit binary floating point value({@code double})
     * <p>Note that not all binary FP values can be exactly represented as decimal FP values.
     *
     * @param value source 64-bit binary floating point value
     * @return new {@code Decimal64} instance
     */
    public static Decimal64 fromDouble(final double value) {
        return new Decimal64(Decimal64Utils.fromDouble(value));
    }

    /**
     * Convert {@code Decimal64} instance to 64-bit binary floating point ({@code double}) value.
     * <p>Note that not all decimal FP values can be exactly represented as binary FP values.
     *
     * @return {@code double} value
     */
    public double toDouble() {
        return Decimal64Utils.toDouble(value);
    }

    /**
     * Create {@code Decimal64} instance from {@code BigDecimal} binary floating point value.
     * <p>Note that not all binary FP values can be exactly represented as decimal FP values.
     *
     * @param value source {@code BigDecimal} binary floating point value
     * @return new {@code Decimal64} instance
     */
    public static Decimal64 fromBigDecimal(final BigDecimal value) {
        return new Decimal64(Decimal64Utils.fromBigDecimal(value));
    }

    /**
     * Create {@code Decimal64} instance from {@code BigDecimal} binary floating point value.
     *
     * @param value source {@code BigDecimal} binary floating point value
     * @return new {@code Decimal64} instance
     * @throws IllegalArgumentException if the value can't be converted to {@code Decimal64} without precision loss
     */
    public static Decimal64 fromBigDecimalExact(final BigDecimal value) throws IllegalArgumentException {
        return new Decimal64(Decimal64Utils.fromBigDecimalExact(value));
    }

    /**
     * Convert {@code Decimal64} instance to {@code BigDecimal} binary floating point value.
     * <p>Note that not all decimal FP values can be exactly represented as binary FP values.
     *
     * @return {@code BigDecimal} value
     */
    public BigDecimal toBigDecimal() {
        return Decimal64Utils.toBigDecimal(value);
    }

    /// endregion

    /// region Classification

    /**
     * Check, if this {@code Decimal64} instance holds a Not-a-Number value.
     *
     * @return {@code true}, if the value is NaN
     */
    public boolean isNaN() {
        return Decimal64Utils.isNaN(value);
    }

    /**
     * Check, if this {@code Decimal64} instance holds is a positive or negative infinity.
     *
     * @return {@code true}, if the value is an infinity
     */
    public boolean isInfinity() {
        return Decimal64Utils.isInfinity(value);
    }

    /**
     * Check, if this {@code Decimal64} instance holds a Positive Infinity value.
     *
     * @return {@code true}, if Positive Infinity
     */
    public boolean isPositiveInfinity() {
        return Decimal64Utils.isPositiveInfinity(value);
    }

    /**
     * Check, if this {@code Decimal64} instance holds a Negative Infinity value.
     *
     * @return {@code true}, if Negative Infinity
     */
    public boolean isNegativeInfinity() {
        return Decimal64Utils.isNegativeInfinity(value);
    }

    /**
     * Check, if this {@code Decimal64} instance holds a finite value(Not infinity or NaN).
     *
     * @return {@code true}, if finite. {@code false} if Infinity or NaN.
     */
    public boolean isFinite() {
        return Decimal64Utils.isFinite(value);
    }

    /**
     * Check, if the value held by this {@code Decimal64} instance is normalized.
     *
     * @return {@code true}, if normalized.
     */
    public boolean isNormal() {
        return Decimal64Utils.isNormal(value);
    }

    /// endregion

    /// region Comparison

    /**
     * Check, if this instance and the specified {@code Decimal64} instance represent the same mathematical value.
     * <p>
     * We consider that all possible encodings of {@link #POSITIVE_INFINITY} are equal,
     * all possible encodings of {@link #NEGATIVE_INFINITY} are equal,
     * all possible encodings of {@code NaN} and {@code SNaN} are equal,
     * all invalid encodings that aren't NaN or Infinity are equal to {@link #ZERO}.
     * Negative and Positive(default) {@link #ZERO} are equal.
     *
     * @param other {@code Decimal64} instance being compared to this instance.
     * @return {@code true} if this instance and the other instance represent the same mathematical value;
     * {@code false} otherwise.
     * @see #equals(Decimal64, Decimal64)
     * @see #equals(Object)
     */
    public boolean equals(final Decimal64 other) {
        return this == other || other != null && Decimal64Utils.equals(this.value, other.value);
    }

    /**
     * Check, if this instance and the specified {@code Decimal64} instance have exactly the same underlying representation.
     * <p>
     * This method returns {@code true} if and only if the other {@code Decimal64} is exactly the same
     * (underlying values are equal). This means that {@code Decimal64.NaN.isIdentical(Decimal64.NaN)} evaluates to
     * {@code true}, while at the same time comparing two different representations of equal real values will cause this
     * method to return {@code false}.
     * E.g. various representations of 0 are not considered same.
     * <p>
     * {@code isIdentical(x, y) => equals(x, y)}
     *
     * @param other {@code Decimal64} instance being compared to this instance.
     * @return {@code true} if the binary representation held by this instance is the same as the underlying value
     * held by the other instance;
     * {@code false} otherwise.
     */
    public boolean isIdentical(final Decimal64 other) {
        return this == other || other != null && value == other.value;
    }

    /**
     * Check, if the specified object is a {@code Decimal64} instance that holds the same underlying value.
     * <p>
     * This method returns {@code true} if and only if the argument is of type {@code Decimal64} and their
     * underlying binary values are equal. This means that {@code Decimal64.NaN.isIdentical(Decimal64.NaN)} evaluates to
     * {@code true}, while at the same time comparing two different representations of equal real values will cause this
     * method to return {@code false}.
     * E.g. various representations of 0 are not considered same.
     * <p>
     * {@code isIdentical(x, y) => equals(x, y)}
     *
     * @param other Object being compared to this instance.
     * @return {@code true} if the binary representation of this value is the same as the underlying value of the other;
     * {@code false} otherwise.
     */
    public boolean isIdentical(final Object other) {
        return this == other || other instanceof Decimal64 && value == ((Decimal64) other).value;
    }

    /**
     * Check, if two {@code Decimal64} instances represent the same mathematical value.
     * <p>
     * We consider that all possible encodings of {@link #POSITIVE_INFINITY} are equal,
     * all possible encodings of {@link #NEGATIVE_INFINITY} are equal,
     * all possible encodings of {@code NaN} and {@code SNaN} are equal,
     * all invalid encodings that aren't NaN or Infinity are equal to {@link #ZERO}.
     * Negative and Positive(default) {@link #ZERO} are equal.
     *
     * @param a the first {@code Decimal64} instance.
     * @param b the second {@code Decimal64} instance.
     * @return {@code true} if both {@code Decimal64} instances represent the same arithmetical value;
     * {@code false} otherwise.
     * @see #equals(Decimal64)
     * @see #equals(Object)
     */
    public static boolean equals(final Decimal64 a, final Decimal64 b) {
        return a == b || a != null && b != null && Decimal64Utils.equals(a.value, b.value);
    }

    /**
     * Check, if two {@code Decimal64} instances hold the same underlying value.
     * <p>
     * This method returns {@code true} if and only if the underlying values of both {@code Decimal64} instances are the same.
     * This means that {@code Decimal64.NaN.isIdentical(Decimal64.NaN)} evaluates to {@code true},
     * while at the same time comparing two different representations of equal real values will cause this
     * method to return {@code false}.
     * E.g. various representations of 0 are not considered same.
     * <p>
     * {@code isIdentical(x, y) => equals(x, y)}
     *
     * @param a the first {@code Decimal64} instance.
     * @param b the second {@code Decimal64} instance.
     * @return {@code true} if the binary representations of a and b are equal;
     * {@code false} otherwise.
     */
    public static boolean isIdentical(final Decimal64 a, final Decimal64 b) {
        return a == b || a != null && b != null && a.value == b.value;
    }

    /**
     * Check, if the second argument is an instance of {@code Decimal64} that represents the same mathematical value as the first argument.
     * <p>
     * We consider that all possible encodings of {@link #POSITIVE_INFINITY} are equal,
     * all possible encodings of {@link #NEGATIVE_INFINITY} are equal,
     * all possible encodings of {@code NaN} and {@code SNaN} are equal,
     * all invalid encodings that aren't NaN or Infinity are equal to {@link #ZERO}.
     * Negative and Positive(default) {@link #ZERO} are equal.
     *
     * @param a {@code Decimal64} instance.
     * @param b Object, to which the first argument is compared.
     * @return {@code true} if the second argument is an instance of {@code Decimal64} instances and both arguments represent the same arithmetical value;
     * {@code false} otherwise.
     */
    public static boolean equals(final Decimal64 a, final Object b) {
        return a == b || a != null && b instanceof Decimal64 && Decimal64Utils.equals(a.value, ((Decimal64) b).value);
    }

    /**
     * Check, if the second argument is an instance of {@code Decimal64} that holds the same underlying value as the first argument.
     * <p>
     * This method returns {@code true} if and only if the 2nd argument is of type {@code Decimal64} and the underlying values
     * of both objects match.  This means that {@code Decimal64.NaN.isIdentical(Decimal64.NaN)} evaluates to {@code true},
     * while at the same time comparing two different representations of equal real values will cause this
     * method to return {@code false}.
     * E.g. various representations of 0 are not considered same.
     * <p>
     * {@code isIdentical(x, y) => equals(x, y)}
     *
     * @param a {@code Decimal64} instance.
     * @param b Object, to which the first argument is compared.
     * @return {@code true} if two instances of {@code Decimal64} hold the same underlying value;
     * {@code false} otherwise.
     */
    public static boolean isIdentical(final Decimal64 a, final Object b) {
        return a == b || a != null && b instanceof Decimal64 && a.value == ((Decimal64) b).value;
    }

    public boolean isEqual(final Decimal64 other) {
        return Decimal64Utils.isEqual(value, other.value);
    }

    public boolean isNotEqual(final Decimal64 other) {
        return Decimal64Utils.isNotEqual(value, other.value);
    }

    public boolean isLess(final Decimal64 other) {
        return Decimal64Utils.isLess(value, other.value);
    }

    public boolean isLessOrEqual(final Decimal64 other) {
        return Decimal64Utils.isLessOrEqual(value, other.value);
    }

    public boolean isGreater(final Decimal64 other) {
        return Decimal64Utils.isGreater(value, other.value);
    }

    public boolean isGreaterOrEqual(final Decimal64 other) {
        return Decimal64Utils.isGreaterOrEqual(value, other.value);
    }

    public boolean isZero() {
        return Decimal64Utils.isZero(value);
    }

    public boolean isNonZero() {
        return Decimal64Utils.isNonZero(value);
    }

    public boolean isPositive() {
        return Decimal64Utils.isPositive(value);
    }

    public boolean isNegative() {
        return Decimal64Utils.isNegative(value);
    }

    public boolean isNonPositive() {
        return Decimal64Utils.isNonPositive(value);
    }

    public boolean isNonNegative() {
        return Decimal64Utils.isNonNegative(value);
    }

    /// endregion

    /// region Minimum & Maximum

    /**
     * Returns the smallest of two given values.
     *
     * @param a first argument
     * @param b second argument
     * @return The smallest of two values.
     */
    public static Decimal64 min(final Decimal64 a, final Decimal64 b) {
        return new Decimal64(Decimal64Utils.min(a.value, b.value));
    }

    public static Decimal64 min(final Decimal64 a, final Decimal64 b, final Decimal64 c) {
        return new Decimal64(Decimal64Utils.min(a.value, b.value, c.value));
    }

    public static Decimal64 min(final Decimal64 a, final Decimal64 b, final Decimal64 c, final Decimal64 d) {
        return new Decimal64(Decimal64Utils.min(a.value, b.value, c.value, d.value));
    }

    /**
     * Returns the greatest of two given values.
     *
     * @param a first value.
     * @param b second value.
     * @return The greatest of two values.
     */
    public static Decimal64 max(final Decimal64 a, final Decimal64 b) {
        return new Decimal64(Decimal64Utils.max(a.value, b.value));
    }

    public static Decimal64 max(final Decimal64 a, final Decimal64 b, final Decimal64 c) {
        return new Decimal64(Decimal64Utils.max(a.value, b.value, c.value));
    }

    public static Decimal64 max(final Decimal64 a, final Decimal64 b, final Decimal64 c, final Decimal64 d) {
        return new Decimal64(Decimal64Utils.max(a.value, b.value, c.value, d.value));
    }

    /**
     * Returns the smallest of two values: this value and {@code a}.
     *
     * @param other another value.
     * @return The smallest of two values.
     */
    public Decimal64 min(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.min(value, other.value));
    }

    /**
     * Returns the greatest of two values: this value and {@code a}.
     *
     * @param other another value.
     * @return The greatest of two values.
     */
    public Decimal64 max(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.max(value, other.value));
    }

    /// endregion

    /// region Arithmetic

    public Decimal64 negate() {
        return new Decimal64(Decimal64Utils.negate(value));
    }

    public Decimal64 abs() {
        return new Decimal64(Decimal64Utils.abs(value));
    }

    public Decimal64 add(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.add(value, other.value));
    }

    public Decimal64 add(final Decimal64 a, final Decimal64 b) {
        return new Decimal64(Decimal64Utils.add(value, a.value, b.value));
    }

    public Decimal64 add(final Decimal64 a, final Decimal64 b, final Decimal64 c) {
        return new Decimal64(Decimal64Utils.add(value, a.value, b.value, c.value));
    }

    public Decimal64 subtract(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.subtract(value, other.value));
    }

    public Decimal64 multiply(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.multiply(value, other.value));
    }

    public Decimal64 multiply(final Decimal64 a, final Decimal64 b) {
        return new Decimal64(Decimal64Utils.multiply(value, a.value, b.value));
    }

    public Decimal64 multiply(final Decimal64 a, final Decimal64 b, final Decimal64 c) {
        return new Decimal64(Decimal64Utils.multiply(value, a.value, b.value, c.value));
    }

    public Decimal64 multiplyByInteger(final int value) {
        return new Decimal64(Decimal64Utils.multiplyByInteger(this.value, value));
    }

    public Decimal64 multiplyByInteger(final long value) {
        return new Decimal64(Decimal64Utils.multiplyByInteger(this.value, value));
    }

    public Decimal64 divide(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.divide(value, other.value));
    }

    public Decimal64 divideByInteger(final int value) {
        return new Decimal64(Decimal64Utils.divideByInteger(this.value, value));
    }

    public Decimal64 divideByInteger(final long value) {
        return new Decimal64(Decimal64Utils.divideByInteger(this.value, value));
    }

    public Decimal64 multiplyAndAdd(final Decimal64 m, final Decimal64 a) {
        return new Decimal64(Decimal64Utils.multiplyAndAdd(value, m.value, a.value));
    }

    public Decimal64 average(final Decimal64 other) {
        return new Decimal64(Decimal64Utils.average(value, other.value));
    }

//    // Same as average
//    public Decimal64 mean(Decimal64 other) {
//        return new Decimal64(Decimal64Utils.average(value, other.value));
//    }

    /// endregion

    /// region Rounding

    public Decimal64 round(final int n, final RoundType roundType) {
        return new Decimal64(Decimal64Utils.round(value, n, roundType));
    }

    public Decimal64 round() {
        return new Decimal64(Decimal64Utils.round(value));
    }

    public Decimal64 round(final Decimal64 multiple) {
        return new Decimal64(Decimal64Utils.round(value, multiple.value));
    }

    @Deprecated
    public Decimal64 ceil() {
        return new Decimal64(Decimal64Utils.ceil(value));
    }

    public Decimal64 ceiling() {
        return new Decimal64(Decimal64Utils.ceiling(value));
    }

    public Decimal64 roundTowardsPositiveInfinity() {
        return new Decimal64(Decimal64Utils.roundTowardsPositiveInfinity(value));
    }

    public Decimal64 floor() {
        return new Decimal64(Decimal64Utils.floor(value));
    }

    public Decimal64 roundTowardsNegativeInfinity() {
        return new Decimal64(Decimal64Utils.roundTowardsNegativeInfinity(value));
    }

    public Decimal64 truncate() {
        return new Decimal64(Decimal64Utils.truncate(value));
    }

    public Decimal64 roundTowardsZero() {
        return new Decimal64(Decimal64Utils.roundTowardsZero(value));
    }

    public Decimal64 roundToNearestTiesAwayFromZero() {
        return new Decimal64(Decimal64Utils.roundToNearestTiesAwayFromZero(value));
    }

    public Decimal64 roundTowardsPositiveInfinity(final Decimal64 multiple) {
        return new Decimal64(Decimal64Utils.roundTowardsPositiveInfinity(value, multiple.value));
    }

    public Decimal64 roundTowardsNegativeInfinity(final Decimal64 multiple) {
        return new Decimal64(Decimal64Utils.roundTowardsNegativeInfinity(value, multiple.value));
    }

    public Decimal64 roundToNearestTiesAwayFromZero(final Decimal64 multiple) {
        return new Decimal64(Decimal64Utils.roundToNearestTiesAwayFromZero(value, multiple.value));
    }

    /// endregion

    /// region Special

    @Decimal
    public Decimal64 nextUp() {
        return new Decimal64(Decimal64Utils.nextUp(value));
    }

    @Decimal
    public Decimal64 nextDown() {
        return new Decimal64(Decimal64Utils.nextDown(value));
    }

    /**
     * Returns canonical representation of s {@code Decimal64} value.
     * <p>
     * We consider that all binary representations of one arithmetical value have the same canonical binary representation.
     * Canonical representation of zeros = {@link #ZERO ZERO}
     * Canonical representation of NaNs = {@link #NaN NaN}
     * Canonical representation of POSITIVE_INFINITYs = {@link #POSITIVE_INFINITY POSITIVE_INFINITY}
     * Canonical representation of NEGATIVE_INFINITYs = {@link #NEGATIVE_INFINITY NEGATIVE_INFINITY}
     *
     * @return Canonical representation of decimal.
     */
    public Decimal64 canonize() {
        return new Decimal64(Decimal64Utils.canonize(value));
    }

    /// endregion

    /// region Parsing & Formatting

    public static String toString(final Decimal64 decimal64) {
        return null == decimal64 ? "null" : Decimal64Utils.toString(decimal64.value);
    }

    public Appendable appendTo(final Appendable appendable) throws IOException {
        return Decimal64Utils.appendTo(value, appendable);
    }

    public StringBuilder appendTo(final StringBuilder builder) {
        return Decimal64Utils.appendTo(value, builder);
    }

    public static Appendable appendTo(final Decimal64 decimal64, final Appendable appendable) throws IOException {
        return null == decimal64 ? appendable.append("null") : Decimal64Utils.appendTo(decimal64.value, appendable);
    }

    public static StringBuilder appendTo(final Decimal64 decimal64, final StringBuilder builder) {
        return null == decimal64 ? builder.append("null") : Decimal64Utils.appendTo(decimal64.value, builder);
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
     * @param stopIndex  Index of character to stop parsing at.
     * @return 64-bit dfp floating-point.
     * @throws NumberFormatException if {@code text} does not contain valid dfp floating value.
     */
    @Decimal
    public static Decimal64 parse(final CharSequence text, final int startIndex, final int stopIndex) {
        return Decimal64.fromUnderlying(Decimal64Utils.parse(text, startIndex, stopIndex));
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
    public static Decimal64 parse(final CharSequence text, final int startIndex) {
        return Decimal64.fromUnderlying(Decimal64Utils.parse(text, startIndex, text.length()));
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
    public static Decimal64 parse(final CharSequence text) {
        return Decimal64.fromUnderlying(Decimal64Utils.parse(text, 0, text.length()));
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
    public static Decimal64 tryParse(final CharSequence text, final int startIndex, final int endIndex,
                                     final Decimal64 defaultValue) {
        try {
            return Decimal64.fromUnderlying(Decimal64Utils.parse(text, startIndex, endIndex));
        } catch (final NumberFormatException ignore) {
            return defaultValue;
        }
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
    public static Decimal64 tryParse(final CharSequence text, final int startIndex, final Decimal64 defaultValue) {
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
    public static Decimal64 tryParse(final CharSequence text, final Decimal64 defaultValue) {
        return tryParse(text, 0, text.length(), defaultValue);
    }

    /// endregion

    /// region Object Interface Implementation

    /**
     * Returns {@code true} if the given Object is an instance of Decimal64 and has the same arithmetical value.
     * <p>
     * We consider that all POSITIVE_INFINITYs is equal to another POSITIVE_INFINITY,
     * all NEGATIVE_INFINITYs is equal to another NEGATIVE_INFINITY,
     * all NaNs is equal to another NaN.
     *
     * @param other value to compare
     * @return True if two decimals represents the same arithmetical value.
     * @see #equals(Decimal64, Decimal64)
     * @see #equals(Decimal64)
     */
    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof Decimal64 && Decimal64Utils.equals(value, ((Decimal64) other).value);
    }

    /**
     * Hash code of binary representation of given decimal.
     *
     * @return HashCode of given decimal.
     */
    public int identityHashCode() {
        return Decimal64Utils.identityHashCode(value);
    }

    /**
     * Return hash code of arithmetical value of given decimal.
     * <p>
     * We consider that all POSITIVE_INFINITYs have equal hashCode,
     * all NEGATIVE_INFINITYs have equal hashCode,
     * all NaNs have equal hashCode.
     *
     * @return HashCode of given decimal.
     */
    @Override
    public int hashCode() {
        return Decimal64Utils.hashCode(value);
    }

    @Override
    public String toString() {
        return Decimal64Utils.toString(value);
    }

    /// endregion

    /// region Number Interface Implementation
    @Override
    public int intValue() {
        return toInt();
    }

    @Override
    public long longValue() {
        return toLong();
    }

    @Override
    public float floatValue() {
        return (float) toDouble();
    }

    @Override
    public double doubleValue() {
        return toDouble();
    }

    /// endregion

    /// region Comparable<T> Interface Implementation

    @Override
    public int compareTo(final Decimal64 o) {
        return Decimal64Utils.compareTo(value, o.value);
    }

    /// endregion
}

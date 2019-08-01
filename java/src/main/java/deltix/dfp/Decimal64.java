package deltix.dfp;

import java.io.IOException;

/**
 * Represents a dfp floating point value.
 * <p>
 * This class is immutable.
 * Can be instantiated only through static constructors.
 */
public class Decimal64 extends Number implements Comparable<Decimal64> {
    /// region Constants

    /*
     Use this constant instead of Java 'null' to initialize or nullify Decimal64 scalar variables, or pass null as a
     Decimal64 scalar argument.
     If a variable is assigned with normal java null reference or left uninitialized, ValueType Agent may generate
     less efficient code, in which case it will print a warning.
     This limitation may be removed in the future versions of ValueType Agent.
     Of course, you may use another constant to initialize Decimal64 variables, just don't leave them uninitialized.
     You don't need to use this constant in equality comparisons. 'if (a == null)' is ok.
     Also, you are not expected to use Decimal64Utils.NULL constant directly anywhere, if you work with Decimal64 class.
     */
    public static final Decimal64 NULL = null;

    public static final Decimal64 NaN = new Decimal64(Decimal64Utils.NaN);

    public static final Decimal64 POSITIVE_INFINITY = new Decimal64(Decimal64Utils.POSITIVE_INFINITY);
    public static final Decimal64 NEGATIVE_INFINITY = new Decimal64(Decimal64Utils.NEGATIVE_INFINITY);

    public static final Decimal64 MIN_VALUE = new Decimal64(Decimal64Utils.MIN_VALUE);
    public static final Decimal64 MAX_VALUE = new Decimal64(Decimal64Utils.MAX_VALUE);

    public static final Decimal64 MIN_POSITIVE_VALUE = new Decimal64(Decimal64Utils.MIN_POSITIVE_VALUE);
    public static final Decimal64 MAX_NEGATIVE_VALUE = new Decimal64(Decimal64Utils.MAX_NEGATIVE_VALUE);

    public static final Decimal64 ZERO = new Decimal64(Decimal64Utils.ZERO);
    public static final Decimal64 ONE = new Decimal64(Decimal64Utils.ONE);
    public static final Decimal64 TWO = new Decimal64(Decimal64Utils.TWO);
    public static final Decimal64 TEN = new Decimal64(Decimal64Utils.TEN);
    public static final Decimal64 HUNDRED = new Decimal64(Decimal64Utils.HUNDRED);
    public static final Decimal64 THOUSAND = new Decimal64(Decimal64Utils.THOUSAND);
    public static final Decimal64 MILLION = new Decimal64(Decimal64Utils.MILLION);

    public static final Decimal64 ONE_TENTH = new Decimal64(Decimal64Utils.ONE_TENTH);
    public static final Decimal64 ONE_HUNDREDTH = new Decimal64(Decimal64Utils.ONE_HUNDREDTH);

    /// endregion

    final long value;

    private Decimal64(long value) {
        this.value = value;
    }

    /// region Conversion

    public static Decimal64 fromUnderlying(long value) {
        return Decimal64Utils.NULL == value ? null : new Decimal64(value);
    }

    public static long toUnderlying(Decimal64 obj) {
        return null == obj ? Decimal64Utils.NULL : obj.value;
    }

    public static Decimal64 fromFixedPoint(long mantissa, int numberOfDigits) {
        return new Decimal64(Decimal64Utils.fromFixedPoint(mantissa, numberOfDigits));
    }

    public static Decimal64 fromFixedPoint(int mantissa, int numberOfDigits) {
        return new Decimal64(Decimal64Utils.fromFixedPoint(mantissa, numberOfDigits));
    }

    public long toFixedPoint(int numberOfDigits) {
        return Decimal64Utils.toFixedPoint(value, numberOfDigits);
    }

    public static Decimal64 fromLong(final long value) {
        return new Decimal64(Decimal64Utils.fromLong(value));
    }

    public long toLong() {
        return Decimal64Utils.toLong(value);
    }

    public static Decimal64 fromInt(final int value) {
        return new Decimal64(Decimal64Utils.fromInt(value));
    }

    public int toInt() {
        return Decimal64Utils.toInt(value);
    }

    public static Decimal64 fromDouble(double d) {
        return new Decimal64(Decimal64Utils.fromDouble(d));
    }

    public double toDouble() {
        return Decimal64Utils.toDouble(value);
    }

    /// endregion

    /// region Classification

    public boolean isNaN() {
        return Decimal64Utils.isNaN(value);
    }

    public boolean isInfinity() {
        return Decimal64Utils.isInfinity(value);
    }

    public boolean isPositiveInfinity() {
        return Decimal64Utils.isPositiveInfinity(value);
    }

    public boolean isNegativeInfinity() {
        return Decimal64Utils.isNegativeInfinity(value);
    }

    public boolean isFinite() {
        return Decimal64Utils.isFinite(value);
    }

    public boolean isNormal() {
        return Decimal64Utils.isNormal(value);
    }

    /// endregion

    /// region Comparison

    /**
     * Return true if this decimal and given decimal represents the same arithmetic value.
     *
     * We consider that all POSITIVE_INFINITYs are equal to another POSITIVE_INFINITY,
     * all NEGATIVE_INFINITYs are equal to another NEGATIVE_INFINITY,
     * all NaNs is equal to another NaN.
     *
     * @param other value to compare
     * @return True if two decimals represents the same arithmetic value.
     */
    public boolean equals(Decimal64 other) {
        return this == other || other != null && Decimal64Utils.equals(this.value, other.value);
    }

    /**
     * Compares this instance with another one.
     * <p>
     * This method returns {@code true} if and only if {@param other} is not of type {@see Decimal64} and their
     * underlying values match. This means that {@code Decimal64.NaN.equals(Decimal64.NaN)} evaluates to
     * {@code true}, while on the same time two different representation of real values might be not equal according
     * to this method. E.g. various representation of 0 are not considered the same.
     *
     * @param other Other instance to compareTo to.
     * @return {@code true} if this instance equals the {@param other}; otherwise - {@code false}.
     */
    public boolean isIdentical(Decimal64 other) {
        return this == other || other != null && value == other.value;
    }

    /**
     * Compares this instance with another one.
     * <p>
     * This method returns {@code true} if and only if {@param other} is not of type {@see Decimal64} and their
     * underlying values match. This means that {@code Decimal64.NaN.equals(Decimal64.NaN)} evaluates to
     * {@code true}, while on the same time two different representation of real values might be not equal according
     * to this method. E.g. various representation of 0 are not considered the same.
     *
     * @param other Other instance to compareTo to.
     * @return {@code true} if this instance equals the {@param other}; otherwise - {@code false}.
     */
    public boolean isIdentical(Object other) {
        return this == other || other instanceof Decimal64 && value == ((Decimal64) other).value;
    }

    /**
     * Return true if two decimals represents the same arithmetic value.
     * <p>
     * We consider that all POSITIVE_INFINITYs are equal to another POSITIVE_INFINITY,
     * all NEGATIVE_INFINITYs are equal to another NEGATIVE_INFINITY,
     * all NaNs is equal to another NaN.
     *
     * @param a First argument
     * @param b Second argument
     * @return True if two decimals represents the same arithmetic value.
     */
    public static boolean equals(Decimal64 a, Decimal64 b) {
        return a == b || a != null && b != null && Decimal64Utils.equals(a.value, b.value);
    }

    /**
     * Compares two instances of {@see Decimal64}
     * <p>
     * This method returns {@code true} if and only if the underlying values of both objects match. This means that
     * {@code Decimal64.NaN.equals(Decimal64.NaN)} evaluates to {@code true}, while on the same time two different
     * representation of real values might be not equal according to this method. E.g. various representation of 0 are
     * not considered the same.
     *
     * @param a First value to compareTo.
     * @param b Second value to compareTo.
     * @return {@code true} if the value {@param a} equals the value of {@param a}; otherwise - {@code false}.
     */
    public static boolean isIdentical(Decimal64 a, Decimal64 b) {
        return a == b || a != null && b != null && a.value == b.value;
    }

    /**
     * Return true if two decimals represents the same arithmetic value.
     * <p>
     * We consider that all POSITIVE_INFINITYs are equal to another POSITIVE_INFINITY,
     * all NEGATIVE_INFINITYs are equal to another NEGATIVE_INFINITY,
     * all NaNs is equal to another NaN.
     *
     * @param a First argument
     * @param b Second argument
     * @return True if two decimals represents the same arithmetic value.
     */
    public static boolean equals(Decimal64 a, Object b) {
        return a == b || a != null && b instanceof Decimal64 && Decimal64Utils.equals(a.value, ((Decimal64) b).value);
    }

    /**
     * Compares an instance of {@see Decimal64} with an object.
     * <p>
     * This method returns {@code true} if and only if {@param b} is of type {@see Decimal64} and the underlying values
     * of both objects match. This means that {@code Decimal64.NaN.equals(Decimal64.NaN)} evaluates to {@code true},
     * while on the same time two different representation of real values might be not equal according to this method.
     * E.g. various representation of 0 are not considered the same.
     *
     * @param a First value to compareTo.
     * @param b Second value to compareTo.
     * @return {@code true} if this instance equals the {@param b}; otherwise - {@code false}.
     */

    public static boolean isIdentical(Decimal64 a, Object b) {
        return a == b || a != null && b instanceof Decimal64 && a.value == ((Decimal64) b).value;
    }

    public boolean isEqual(Decimal64 other) {
        return Decimal64Utils.isEqual(value, other.value);
    }

    public boolean isNotEqual(Decimal64 other) {
        return Decimal64Utils.isNotEqual(value, other.value);
    }

    public boolean isLess(Decimal64 other) {
        return Decimal64Utils.isLess(value, other.value);
    }

    public boolean isLessOrEqual(Decimal64 other) {
        return Decimal64Utils.isLessOrEqual(value, other.value);
    }

    public boolean isGreater(Decimal64 other) {
        return Decimal64Utils.isGreater(value, other.value);
    }

    public boolean isGreaterOrEqual(Decimal64 other) {
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
     * @param a first value.
     * @param b second value.
     * @return The smallest of two values.
     */
    public static Decimal64 min(Decimal64 a, Decimal64 b) {
        return new Decimal64(Decimal64Utils.min(a.value, b.value));
    }

    public static Decimal64 min(Decimal64 a, Decimal64 b, Decimal64 c) {
        return new Decimal64(Decimal64Utils.min(a.value, b.value, c.value));
    }

    public static Decimal64 min(Decimal64 a, Decimal64 b, Decimal64 c, Decimal64 d) {
        return new Decimal64(Decimal64Utils.min(a.value, b.value, c.value, d.value));
    }

    /**
     * Returns the greatest of two given values.
     *
     * @param a first value.
     * @param b second value.
     * @return The greatest of two values.
     */
    public static Decimal64 max(Decimal64 a, Decimal64 b) {
        return new Decimal64(Decimal64Utils.max(a.value, b.value));
    }

    public static Decimal64 max(Decimal64 a, Decimal64 b, Decimal64 c) {
        return new Decimal64(Decimal64Utils.max(a.value, b.value, c.value));
    }

    public static Decimal64 max(Decimal64 a, Decimal64 b, Decimal64 c, Decimal64 d) {
        return new Decimal64(Decimal64Utils.max(a.value, b.value, c.value, d.value));
    }

    /**
     * Returns the smallest of two values: this value and {@code a}.
     *
     * @param other another value.
     * @return The smallest of two values.
     */
    public Decimal64 min(Decimal64 other) {
        return new Decimal64(Decimal64Utils.min(value, other.value));
    }

    /**
     * Returns the greatest of two values: this value and {@code a}.
     *
     * @param other another value.
     * @return The greatest of two values.
     */
    public Decimal64 max(Decimal64 other) {
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

    public Decimal64 add(Decimal64 other) {
        return new Decimal64(Decimal64Utils.add(value, other.value));
    }

    public Decimal64 add(Decimal64 a, Decimal64 b) {
        return new Decimal64(Decimal64Utils.add(value, a.value, b.value));
    }

    public Decimal64 add(Decimal64 a, Decimal64 b, Decimal64 c) {
        return new Decimal64(Decimal64Utils.add(value, a.value, b.value, c.value));
    }

    public Decimal64 subtract(Decimal64 other) {
        return new Decimal64(Decimal64Utils.subtract(value, other.value));
    }

    public Decimal64 multiply(Decimal64 other) {
        return new Decimal64(Decimal64Utils.multiply(value, other.value));
    }

    public Decimal64 multiply(Decimal64 a, Decimal64 b) {
        return new Decimal64(Decimal64Utils.multiply(value, a.value, b.value));
    }

    public Decimal64 multiply(Decimal64 a, Decimal64 b, Decimal64 c) {
        return new Decimal64(Decimal64Utils.multiply(value, a.value, b.value, c.value));
    }

    public Decimal64 multiplyByInteger(int value) {
        return new Decimal64(Decimal64Utils.multiplyByInteger(this.value, value));
    }

    public Decimal64 multiplyByInteger(long value) {
        return new Decimal64(Decimal64Utils.multiplyByInteger(this.value, value));
    }

    public Decimal64 divide(Decimal64 other) {
        return new Decimal64(Decimal64Utils.divide(value, other.value));
    }

    public Decimal64 divideByInteger(int value) {
        return new Decimal64(Decimal64Utils.divideByInteger(this.value, value));
    }

    public Decimal64 divideByInteger(long value) {
        return new Decimal64(Decimal64Utils.divideByInteger(this.value, value));
    }

    public Decimal64 multiplyAndAdd(Decimal64 m, Decimal64 a) {
        return new Decimal64(Decimal64Utils.multiplyAndAdd(value, m.value, a.value));
    }

    public Decimal64 average(Decimal64 other) {
        return new Decimal64(Decimal64Utils.average(value, other.value));
    }

//    // Same as average
//    public Decimal64 mean(Decimal64 other) {
//        return new Decimal64(Decimal64Utils.average(value, other.value));
//    }

    /// endregion

    /// region Rounding

    public Decimal64 round() {
        return new Decimal64(Decimal64Utils.round(value));
    }

    public Decimal64 round(Decimal64 precision) {
        return new Decimal64(Decimal64Utils.round(value, precision.value));
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

    public Decimal64 roundTowardsPositiveInfinity(Decimal64 multiple) {
        return new Decimal64(Decimal64Utils.roundTowardsPositiveInfinity(value, multiple.value));
    }

    public Decimal64 roundTowardsNegativeInfinity(Decimal64 multiple) {
        return new Decimal64(Decimal64Utils.roundTowardsNegativeInfinity(value, multiple.value));
    }

    public Decimal64 roundToNearestTiesAwayFromZero(Decimal64 multiple) {
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
     * Returns canonical representation of Decimal.
     * We consider that all binary representations of one arithmetic value have the same canonical binary representation.
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
     * Return true if this decimal and given decimal represents the same arithmetic value.
     *
     * We consider that all POSITIVE_INFINITYs is equal to another POSITIVE_INFINITY,
     * all NEGATIVE_INFINITYs is equal to another NEGATIVE_INFINITY,
     * all NaNs is equal to another NaN.
     *
     * @param other value to compare
     * @return True if two decimals represents the same arithmetic value.
     */
    @Override
    public boolean equals(Object other) {
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
     * Return hash code of arithmetic value of given decimal.
     *
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

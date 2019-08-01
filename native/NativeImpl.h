/**
 * DO NOT FORGET to update native library version when making any changes to the binary library API
 */

FN(int32_t, version) NO_ARGS // NOTE: Can't ARGS() here due to quirk with handling empty vararg macro on Linux
{
    return NATIVE_API_VERSION;
}

//
// Conversion & Rounding
//


FN(dfp_long_t, fromFixedPoint64) ARGS(int64_t value, int32_t numberOfDigits)
{
    return decimal_as_long(scalbnd64((_Decimal64)value, -numberOfDigits));
}

FN(dfp_long_t, fromFixedPoint32) ARGS(int32_t value, int32_t numberOfDigits)
{
    return decimal_as_long(scalbnd64((_Decimal64)value, -numberOfDigits));
}

FN(dfp_long_t, fromFixedPointU32) ARGS(uint32_t value, int32_t numberOfDigits)
{
    return decimal_as_long(scalbnd64((_Decimal64)value, -numberOfDigits));
}

FN(int64_t, toFixedPoint) ARGS(dfp_long_t value, int32_t numberOfDigits)
{
    return (int64_t)scalbnd64(long_as_decimal(value), numberOfDigits);
}

FN(dfp_long_t, fromInt64) ARGS(int64_t value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(int64_t, toInt64) ARGS(dfp_long_t value)
{
    return (int64_t)long_as_decimal(value);
}

FN(dfp_long_t, fromUInt64) ARGS(uint64_t value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(uint64_t, toUInt64) ARGS(dfp_long_t value)
{
    return (uint64_t)long_as_decimal(value);
}

FN(dfp_long_t, fromInt32) ARGS(int32_t value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(int32_t, toInt32) ARGS(dfp_long_t value)
{
    return (int32_t)long_as_decimal(value);
}

FN(dfp_long_t, fromUInt32) ARGS(uint32_t value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(uint32_t, toUInt32) ARGS(dfp_long_t value)
{
    return (uint32_t)long_as_decimal(value);
}

FN(dfp_long_t, fromInt16) ARGS(int16_t value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(int16_t, toInt16) ARGS(dfp_long_t value)
{
    return (int16_t)long_as_decimal(value);
}

FN(dfp_long_t, fromInt8) ARGS(int8_t value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(int8_t, toInt8) ARGS(dfp_long_t value)
{
    return (int8_t)long_as_decimal(value);
}

FN(uint8_t, toUInt8) ARGS(dfp_long_t value)
{
    return (uint8_t)long_as_decimal(value);
}

FN(dfp_long_t, fromFloat64) ARGS(double value)
{
    return decimal_as_long((_Decimal64) value);
}

FN(double, toFloat64) ARGS(dfp_long_t value)
{
    return (double)long_as_decimal(value);
}

FN(dfp_long_t, roundTowardsPositiveInfinity) ARGS(dfp_long_t a)
{
    return decimal_as_long(ceild64(long_as_decimal(a)));
}

FN(dfp_long_t, roundTowardsNegativeInfinity) ARGS(dfp_long_t a)
{
    return decimal_as_long(floord64(long_as_decimal(a)));
}

FN(dfp_long_t, roundTowardsZero) ARGS(dfp_long_t a)
{
    return decimal_as_long(truncd64(long_as_decimal(a)));
}

FN(dfp_long_t, roundToNearestTiesAwayFromZero) ARGS(dfp_long_t a)
{
    return decimal_as_long(roundd64(long_as_decimal(a)));
}

//
// Classification
//

FN(uint8_t, isNaN) ARGS(dfp_long_t a)
{
    return (uint8_t) isnand64(long_as_decimal(a));
}

FN(uint8_t, isInfinity) ARGS(dfp_long_t a)
{
    return (uint8_t) isinfd64(long_as_decimal(a));
}

FN(uint8_t, isPositiveInfinity) ARGS(dfp_long_t a)
{
    return (uint8_t) (isinfd64(long_as_decimal(a)) && !signbitd64(long_as_decimal(a)));
}

FN(uint8_t, isNegativeInfinity) ARGS(dfp_long_t a)
{
    return (uint8_t) (isinfd64(long_as_decimal(a)) && signbitd64(long_as_decimal(a)));
}

FN(uint8_t, isFinite) ARGS(dfp_long_t a)
{
    return (uint8_t) isfinited64(long_as_decimal(a));
}

FN(uint8_t, isNormal) ARGS(dfp_long_t a)
{
    return (uint8_t) isnormald64(long_as_decimal(a));
}

FN(uint8_t, signBit) ARGS(dfp_long_t a)
{
    return (uint8_t) signbitd64(long_as_decimal(a));
}

//
// Comparison
//

FN(int32_t, compare) ARGS(dfp_long_t a, dfp_long_t b)
{
    // NOTE: Here comparison is implemented according to either C# or Java CompareTo rules for double values
    _Decimal64 da = long_as_decimal(a), db = long_as_decimal(b);
    if (da < db)
        return -1;
    if (da > db)
        return 1;
    if (da == db)
        return 0;
    // Now, at least one of the numbers is NaN.
#if MULTIAPI_DOTNET
#pragma message ("Using .NET compareTo")
    return isnand64(db) - isnand64(da);
#else // Java
#pragma message ("Using Java compareTo")
    return isnand64(da) - isnand64(db);
#endif
}

FN(uint8_t, isEqual) ARGS(dfp_long_t a, dfp_long_t b)
{
    return (uint8_t) (isequald64(long_as_decimal(a), long_as_decimal(b)) ? 1 : 0);
}

FN(uint8_t, isNotEqual) ARGS(dfp_long_t a, dfp_long_t b)
{
    return (uint8_t) (long_as_decimal(a) != long_as_decimal(b) ? 1 : 0);
}

FN(uint8_t, isLess) ARGS(dfp_long_t a, dfp_long_t b)
{
    return (uint8_t) (islessd64(long_as_decimal(a), long_as_decimal(b)) ? 1 : 0);
}

FN(uint8_t, isLessOrEqual) ARGS(dfp_long_t a, dfp_long_t b)
{
    return (uint8_t) (islessequald64(long_as_decimal(a), long_as_decimal(b)) ? 1 : 0);
}

FN(uint8_t, isGreater) ARGS(dfp_long_t a, dfp_long_t b)
{
    return (uint8_t) (isgreaterd64(long_as_decimal(a), long_as_decimal(b)) ? 1 : 0);
}

FN(uint8_t, isGreaterOrEqual) ARGS(dfp_long_t a, dfp_long_t b)
{
    return (uint8_t) (isgreaterequald64(long_as_decimal(a), long_as_decimal(b)) ? 1 : 0);
}

FN(uint8_t, isZero) ARGS(dfp_long_t a)
{
    return (uint8_t)(long_as_decimal(a) == DFP_ZERO ? 1 : 0);
}

FN(uint8_t, isNonZero) ARGS(dfp_long_t a)
{
    return (uint8_t)(long_as_decimal(a) != DFP_ZERO ? 1 : 0);
}

FN(uint8_t, isPositive) ARGS(dfp_long_t a)
{
    return (uint8_t)(long_as_decimal(a) > DFP_ZERO ? 1 : 0);
}

FN(uint8_t, isNegative) ARGS(dfp_long_t a)
{
    return (uint8_t)(long_as_decimal(a) < DFP_ZERO ? 1 : 0);
}

FN(uint8_t, isNonPositive) ARGS(dfp_long_t a)
{
    return (uint8_t)(long_as_decimal(a) <= DFP_ZERO ? 1 : 0);
}

FN(uint8_t, isNonNegative) ARGS(dfp_long_t a)
{
    return (uint8_t)(long_as_decimal(a) >= DFP_ZERO ? 1 : 0);
}

//
// Minimum & Maximum
//


FN(dfp_long_t, max2) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long(dfp_max(long_as_decimal(a), long_as_decimal(b)));
}

FN(dfp_long_t, max3) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c)
{
    return decimal_as_long(dfp_max(dfp_max(long_as_decimal(a), long_as_decimal(b)), long_as_decimal(c)));
}

FN(dfp_long_t, max4) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c, dfp_long_t d)
{
    return decimal_as_long(dfp_max(dfp_max(long_as_decimal(a), long_as_decimal(b)), dfp_max(long_as_decimal(c), long_as_decimal(d))));
}

FN(dfp_long_t, min2) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long(dfp_min(long_as_decimal(a), long_as_decimal(b)));
}

FN(dfp_long_t, min3) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c)
{
    return decimal_as_long(dfp_min(dfp_min(long_as_decimal(a), long_as_decimal(b)), long_as_decimal(c)));
}

FN(dfp_long_t, min4) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c, dfp_long_t d)
{
    return decimal_as_long(dfp_min(dfp_min(long_as_decimal(a), long_as_decimal(b)), dfp_min(long_as_decimal(c), long_as_decimal(d))));
}

//
// Arithmetic
//

FN(dfp_long_t, negate) ARGS(dfp_long_t a)
{
    return decimal_as_long(-long_as_decimal(a));
}

FN(dfp_long_t, abs) ARGS(dfp_long_t x)
{
    return decimal_as_long(fabsd64(long_as_decimal(x)));
}

FN(dfp_long_t, add2) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long(long_as_decimal(a) + long_as_decimal(b));
}

FN(dfp_long_t, add3) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c)
{
    return decimal_as_long(long_as_decimal(a) + long_as_decimal(b) + long_as_decimal(c));
}

FN(dfp_long_t, add4) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c, dfp_long_t d)
{
    return decimal_as_long(long_as_decimal(a) + long_as_decimal(b) + long_as_decimal(c) + long_as_decimal(d));
}

FN(dfp_long_t, subtract) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long(long_as_decimal(a) - long_as_decimal(b));
}

FN(dfp_long_t, multiply2) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long(long_as_decimal(a) * long_as_decimal(b));
}

FN(dfp_long_t, multiply3) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c)
{
    return decimal_as_long(long_as_decimal(a) * long_as_decimal(b) * long_as_decimal(c));
}

FN(dfp_long_t, multiply4) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c, dfp_long_t d)
{
    return decimal_as_long(long_as_decimal(a) * long_as_decimal(b) * long_as_decimal(c) * long_as_decimal(d));
}

FN(dfp_long_t, multiplyByInt32) ARGS(dfp_long_t a, int32_t b)
{
    return decimal_as_long(long_as_decimal(a) * b);
}

FN(dfp_long_t, multiplyByInt64) ARGS(dfp_long_t a, int64_t b)
{
    return decimal_as_long(long_as_decimal(a) * b);
}

FN(dfp_long_t, divide) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long(long_as_decimal(a) / long_as_decimal(b));
}

FN(dfp_long_t, divideByInt32) ARGS(dfp_long_t a, int32_t b)
{
    return decimal_as_long(long_as_decimal(a) / b);
}

FN(dfp_long_t, divideByInt64) ARGS(dfp_long_t a, int64_t b)
{
    return decimal_as_long(long_as_decimal(a) / b);
}

FN(dfp_long_t, multiplyAndAdd) ARGS(dfp_long_t a, dfp_long_t b, dfp_long_t c)
{
    return decimal_as_long(fmad64(long_as_decimal(a), long_as_decimal(b), long_as_decimal(c)));
}

FN(dfp_long_t, scaleByPowerOfTen) ARGS(dfp_long_t a, int32_t n)
{
    return decimal_as_long(scalbnd64(long_as_decimal(a), n));
}

FN(dfp_long_t, mean2) ARGS(dfp_long_t a, dfp_long_t b)
{
    return decimal_as_long((long_as_decimal(a) + long_as_decimal(b)) / 2);
}

//
// Special
//

FN(dfp_long_t, nextUp) ARGS(dfp_long_t a)
{
    return decimal_as_long(nextupd64(long_as_decimal(a)));
}

FN(dfp_long_t, nextDown) ARGS(dfp_long_t a)
{
    return decimal_as_long(nextdownd64(long_as_decimal(a)));
}

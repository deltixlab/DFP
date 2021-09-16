#include <bid_conf.h>
#include <bid_functions.h>

#ifndef API_PREFIX
#define API_PREFIX    ddfp_
#endif

#ifndef JAVA_PREFIX
#define JAVA_PREFIX    com_epam_deltix_dfp_NativeImpl_
#endif

#if defined(_WIN32)
#define JNI_API(x) __declspec(dllexport) x __stdcall
#else
#define JNI_API(x) x __attribute__ ((externally_visible,visibility("default")))
#endif

#if defined(_WIN32)
#define DDFP_API(x) __declspec(dllexport) x __cdecl
#else
#define DDFP_API(x) x __attribute__ ((externally_visible,visibility("default")))
#endif


/*
 * Concatenate preprocessor tokens A and B without expanding macro definitions
 * (however, if invoked from a macro, macro arguments are expanded).
 */
#define PPCAT_NX(A, B) A ## B

/*
 * Concatenate preprocessor tokens A and B after macro-expanding them.
 */
#define PPCAT(A, B) PPCAT_NX(A, B)

typedef BID_UINT64          decimal64;
typedef char                int8;
typedef unsigned char       uint8;
typedef short               int16;
typedef unsigned short      uint16;
typedef int                 int32;
typedef unsigned int        uint32;
typedef long long           int64;
typedef unsigned long long  uint64;
typedef int                 intBool;

//https://stackoverflow.com/questions/4079243/how-can-i-use-sizeof-in-a-preprocessor-macro
#define STATIC_ASSERT(condition) typedef char p__LINE__[ (condition) ? 1 : -1];
STATIC_ASSERT(sizeof(int8) == 1)
STATIC_ASSERT(sizeof(uint8) == 1)
STATIC_ASSERT(sizeof(int16) == 2)
STATIC_ASSERT(sizeof(uint16) == 2)
STATIC_ASSERT(sizeof(int32) == 4)
STATIC_ASSERT(sizeof(uint32) == 4)
STATIC_ASSERT(sizeof(int64) == 8)
STATIC_ASSERT(sizeof(uint64) == 8)

#define OPNRR(mcr__name, mcr__type, mcr__body, ...)                                                         \
DDFP_API(mcr__type) PPCAT(API_PREFIX, mcr__name) (__VA_ARGS__) {                                            \
    mcr__body                                                                                               \
}                                                                                                           \
JNI_API(mcr__type) PPCAT(PPCAT(Java_, JAVA_PREFIX), mcr__name) (void *jEnv, void *jClass, __VA_ARGS__) {    \
    mcr__body                                                                                               \
}                                                                                                           \
JNI_API(mcr__type) PPCAT(PPCAT(JavaCritical_, JAVA_PREFIX), mcr__name) (__VA_ARGS__) {                      \
    mcr__body                                                                                               \
}

#define OPNR(mcr__name, mcr__type, mcr__body, ...)  OPNRR(mcr__name, mcr__type, return (mcr__body);, __VA_ARGS__)

#define OPN(mcr__name, mcr__body, ...)              OPNR(mcr__name, BID_UINT64, mcr__body, __VA_ARGS__)

#define OPN_BOOL(mcr__name, mcr__body, ...)         OPNR(mcr__name, intBool, mcr__body, __VA_ARGS__)

static const BID_UINT64 nanConst =  0x7C00000000000000ull;
static const BID_UINT64 zeroConst = 0x31C0000000000000ull;
static const BID_UINT64 twoConst =  0x31C0000000000002ull;

//region Conversion

typedef double      Float64;
typedef float       Float32;
typedef int64       Int64;
typedef uint64      UInt64;
typedef int32       Int32;
typedef uint32      UInt32;
typedef int16       Int16;
typedef uint16      UInt16;
typedef int8        Int8;
typedef uint8       UInt8;

OPN(fromFloat64, binary64_to_bid64(x), double x)
OPN(fromFloat32, binary32_to_bid64(x), float x)
OPN(fromInt64, bid64_from_int64(x), int64 x)
OPN(fromUInt64, bid64_from_uint64(x), uint64 x)
OPNR(toFloat64, double, bid64_to_binary64(x), BID_UINT64 x)
OPNR(toFloat32, float, bid64_to_binary32(x), BID_UINT64 x)
OPNR(toInt64, int64, bid64_to_int64_xint(x), BID_UINT64 x)
OPNR(toUInt64, uint64, bid64_to_uint64_xint(x), BID_UINT64 x)
OPN(fromFixedPoint64, bid64_scalbn(bid64_from_int64(mantissa), -tenPowerFactor), int64 mantissa, int32 tenPowerFactor)
OPNR(toFixedPoint, int64, bid64_to_int64_xint(bid64_scalbn(value, numberOfDigits)), BID_UINT64 value, int32 numberOfDigits)

//endregion

//region Classification

OPN_BOOL(isNaN, bid64_isNaN(x), BID_UINT64 x)
OPN_BOOL(isInfinity, bid64_isInf(x), BID_UINT64 x)
OPN_BOOL(isPositiveInfinity, (intBool)(bid64_isInf(x) && !bid64_isSigned(x)), BID_UINT64 x)
OPN_BOOL(isNegativeInfinity, (intBool)(bid64_isInf(x) && bid64_isSigned(x)), BID_UINT64 x)
OPN_BOOL(isFinite, bid64_isFinite(x), BID_UINT64 x)
OPN_BOOL(isNormal, bid64_isNormal(x), BID_UINT64 x)
OPN_BOOL(signBit, bid64_isSigned(x), BID_UINT64 x)

//endregion

//region Comparison

DDFP_API(int32) PPCAT(API_PREFIX, compare) ( BID_UINT64 a, BID_UINT64 b) {
    if (bid64_quiet_less(a, b))
        return -1;
    if (bid64_quiet_greater(a, b))
        return 1;
    if (bid64_quiet_equal(a, b))
        return 0;
    return bid64_isNaN(b) - bid64_isNaN(a);
}
JNI_API(int32) PPCAT(PPCAT(Java_, JAVA_PREFIX), compare) (void *jEnv, void *jClass,  BID_UINT64 a, BID_UINT64 b) {
    if (bid64_quiet_less(a, b))
        return -1;
    if (bid64_quiet_greater(a, b))
        return 1;
    if (bid64_quiet_equal(a, b))
        return 0;
    return bid64_isNaN(a) - bid64_isNaN(b);
}
JNI_API(int32) PPCAT(PPCAT(JavaCritical_, JAVA_PREFIX), compare) ( BID_UINT64 a, BID_UINT64 b) {
    if (bid64_quiet_less(a, b))
        return -1;
    if (bid64_quiet_greater(a, b))
        return 1;
    if (bid64_quiet_equal(a, b))
        return 0;
    return bid64_isNaN(a) - bid64_isNaN(b);
}

OPN_BOOL(isEqual, bid64_quiet_equal(a, b), BID_UINT64 a, BID_UINT64 b)
OPN_BOOL(isNotEqual, bid64_quiet_not_equal(a, b), BID_UINT64 a, BID_UINT64 b)
OPN_BOOL(isLess, bid64_quiet_less(a, b), BID_UINT64 a, BID_UINT64 b)
OPN_BOOL(isLessOrEqual, bid64_quiet_less_equal(a, b), BID_UINT64 a, BID_UINT64 b)
OPN_BOOL(isGreater, bid64_quiet_greater(a, b), BID_UINT64 a, BID_UINT64 b)
OPN_BOOL(isGreaterOrEqual, bid64_quiet_greater_equal(a, b), BID_UINT64 a, BID_UINT64 b)
OPN_BOOL(isZero, bid64_isZero(a), BID_UINT64 a)
OPN_BOOL(isNonZero, bid64_quiet_not_equal(a, zeroConst), BID_UINT64 a)
OPN_BOOL(isPositive, bid64_quiet_greater(a, zeroConst), BID_UINT64 a)
OPN_BOOL(isNegative, bid64_quiet_less(a, zeroConst), BID_UINT64 a)
OPN_BOOL(isNonPositive, bid64_quiet_less_equal(a, zeroConst), BID_UINT64 a)
OPN_BOOL(isNonNegative, bid64_quiet_greater_equal(a, zeroConst), BID_UINT64 a)

//endregion

//region Rounding

OPN(roundTowardsPositiveInfinity, bid64_round_integral_positive(x), BID_UINT64 x)
OPN(roundTowardsNegativeInfinity, bid64_round_integral_negative(x), BID_UINT64 x)
OPN(roundTowardsZero, bid64_round_integral_zero(x), BID_UINT64 x)
OPN(roundToNearestTiesAwayFromZero, bid64_round_integral_nearest_away(x), BID_UINT64 x)

//endregion

//region Minimum & Maximum

OPN(max2, bid64_isNaN(a) || bid64_isNaN(b) ? nanConst : bid64_maxnum(a, b), BID_UINT64 a, BID_UINT64 b)
OPN(max3, bid64_isNaN(a) || bid64_isNaN(b) || bid64_isNaN(c) ? nanConst : bid64_maxnum(bid64_maxnum(a, b), c), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c)
OPN(max4, bid64_isNaN(a) || bid64_isNaN(b) || bid64_isNaN(c) || bid64_isNaN(d) ? nanConst : bid64_maxnum(bid64_maxnum(a, b), bid64_maxnum(c, d)), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c, BID_UINT64 d)
OPN(min2, bid64_isNaN(a) || bid64_isNaN(b) ? nanConst : bid64_minnum(a, b), BID_UINT64 a, BID_UINT64 b)
OPN(min3, bid64_isNaN(a) || bid64_isNaN(b) || bid64_isNaN(c) ? nanConst : bid64_minnum(bid64_minnum(a, b), c), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c)
OPN(min4, bid64_isNaN(a) || bid64_isNaN(b) || bid64_isNaN(c) || bid64_isNaN(d) ? nanConst : bid64_minnum(bid64_minnum(a, b), bid64_minnum(c, d)), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c, BID_UINT64 d)

//endregion

//region Arithmetic

OPN(negate, bid64_negate(x), BID_UINT64 x)
OPN(abs, bid64_abs(x), BID_UINT64 x)
OPN(add2, bid64_add(a, b), BID_UINT64 a, BID_UINT64 b)
OPN(add3, bid64_add(bid64_add(a, b), c), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c)
OPN(add4, bid64_add(bid64_add(a, b), bid64_add(c, d)), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c, BID_UINT64 d)
OPN(subtract, bid64_sub(a, b), BID_UINT64 a, BID_UINT64 b)
OPN(multiply2, bid64_mul(a, b), BID_UINT64 a, BID_UINT64 b)
OPN(multiply3, bid64_mul(bid64_mul(a, b), c), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c)
OPN(multiply4, bid64_mul(bid64_mul(a, b), bid64_mul(c, d)), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c, BID_UINT64 d)
OPN(multiplyByInt32, bid64_mul(a, bid64_from_int32(integer)), BID_UINT64 a, int32 integer)
OPN(multiplyByInt64, bid64_mul(a, bid64_from_int64(integer)), BID_UINT64 a, int64 integer)
OPN(divide, bid64_div(a, b), BID_UINT64 a, BID_UINT64 b)
OPN(divideByInt32, bid64_div(x, bid64_from_int32(integer)), BID_UINT64 x, int32 integer)
OPN(divideByInt64, bid64_div(x, bid64_from_int64(integer)), BID_UINT64 x, int64 integer)
OPN(multiplyAndAdd, bid64_fma(a, b, c), BID_UINT64 a, BID_UINT64 b, BID_UINT64 c)
OPN(scaleByPowerOfTen, bid64_scalbn(a, n) , BID_UINT64 a, int32 n)
OPN(mean2, bid64_div(bid64_add(a, b), twoConst), BID_UINT64 a, BID_UINT64 b)


OPN(bid64_exp, bid64_exp(x), BID_UINT64 x)
OPN(bid64_exp2, bid64_exp2(x), BID_UINT64 x)
OPN(bid64_exp10, bid64_exp10(x), BID_UINT64 x)
OPN(bid64_expm1, bid64_expm1(x), BID_UINT64 x)
OPN(bid64_log, bid64_log(x), BID_UINT64 x)
OPN(bid64_log2, bid64_log2(x), BID_UINT64 x)
OPN(bid64_log10, bid64_log10(x), BID_UINT64 x)
OPN(bid64_log1p, bid64_log1p(x), BID_UINT64 x)
OPN(bid64_pow, bid64_pow(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_fmod, bid64_fmod(x, y), BID_UINT64 x, BID_UINT64 y)
//OPN(bid64_modf, bid64_modf(x, iptr), BID_UINT64 x, BID_UINT64 *iptr)
OPN(bid64_hypot, bid64_hypot(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_sin, bid64_sin(x), BID_UINT64 x)
OPN(bid64_cos, bid64_cos(x), BID_UINT64 x)
OPN(bid64_tan, bid64_tan(x), BID_UINT64 x)
OPN(bid64_asin, bid64_asin(x), BID_UINT64 x)
OPN(bid64_acos, bid64_acos(x), BID_UINT64 x)
OPN(bid64_atan, bid64_atan(x), BID_UINT64 x)
OPN(bid64_atan2, bid64_atan2(y, x), BID_UINT64 y, BID_UINT64 x)
OPN(bid64_sinh, bid64_sinh(x), BID_UINT64 x)
OPN(bid64_cosh, bid64_cosh(x), BID_UINT64 x)
OPN(bid64_tanh, bid64_tanh(x), BID_UINT64 x)
OPN(bid64_asinh, bid64_asinh(x), BID_UINT64 x)
OPN(bid64_acosh, bid64_acosh(x), BID_UINT64 x)
OPN(bid64_atanh, bid64_atanh(x), BID_UINT64 x)
OPN(bid64_erf, bid64_erf(x), BID_UINT64 x)
OPN(bid64_erfc, bid64_erfc(x), BID_UINT64 x)
OPN(bid64_tgamma, bid64_tgamma(x), BID_UINT64 x)
OPN(bid64_lgamma, bid64_lgamma(x), BID_UINT64 x)
OPN(bid64_add, bid64_add(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_sub, bid64_sub(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_mul, bid64_mul(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_div, bid64_div(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_fma, bid64_fma(x, y, z), BID_UINT64 x,  BID_UINT64 y, BID_UINT64 z)
OPN(bid64_sqrt, bid64_sqrt(x), BID_UINT64 x)
OPN(bid64_cbrt, bid64_cbrt(x), BID_UINT64 x)

OPN_BOOL(bid64_quiet_equal, bid64_quiet_equal(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_greater, bid64_quiet_greater(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_greater_equal, bid64_quiet_greater_equal(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_greater_unordered, bid64_quiet_greater_unordered(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_less, bid64_quiet_less(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_less_equal, bid64_quiet_less_equal(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_less_unordered, bid64_quiet_less_unordered(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_not_equal, bid64_quiet_not_equal(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_not_greater, bid64_quiet_not_greater(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_not_less, bid64_quiet_not_less(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_ordered, bid64_quiet_ordered(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_quiet_unordered, bid64_quiet_unordered(x, y), BID_UINT64 x, BID_UINT64 y)

OPN(bid64_round_integral_exact, bid64_round_integral_exact(x), BID_UINT64 x)
OPN(bid64_round_integral_nearest_even, bid64_round_integral_nearest_even(x), BID_UINT64 x)
OPN(bid64_round_integral_negative, bid64_round_integral_negative(x), BID_UINT64 x)
OPN(bid64_round_integral_positive, bid64_round_integral_positive(x), BID_UINT64 x)
OPN(bid64_round_integral_zero, bid64_round_integral_zero(x), BID_UINT64 x)
OPN(bid64_round_integral_nearest_away, bid64_round_integral_nearest_away(x), BID_UINT64 x)

OPN(bid64_nextup, bid64_nextup(x), BID_UINT64 x)
OPN(bid64_nextdown, bid64_nextdown(x), BID_UINT64 x)
OPN(bid64_nextafter, bid64_nextafter(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_minnum, bid64_minnum(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_minnum_mag, bid64_minnum_mag(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_maxnum, bid64_maxnum(x, y), BID_UINT64 x, BID_UINT64 y)
OPN(bid64_maxnum_mag, bid64_maxnum_mag(x, y), BID_UINT64 x, BID_UINT64 y)

OPN(bid64_from_int32, bid64_from_int32(x), int32 x)
OPN(bid64_from_uint32, bid64_from_uint32(x), uint32 x)
OPN(bid64_from_int64, bid64_from_int64(x), int64 x)
OPN(bid64_from_uint64, bid64_from_uint64(x), uint64 x)

OPN_BOOL(bid64_isSigned, bid64_isSigned(x), BID_UINT64 x)
OPN_BOOL(bid64_isNormal, bid64_isNormal(x), BID_UINT64 x)
OPN_BOOL(bid64_isSubnormal, bid64_isSubnormal(x), BID_UINT64 x)
OPN_BOOL(bid64_isFinite, bid64_isFinite(x), BID_UINT64 x)
OPN_BOOL(bid64_isZero, bid64_isZero(x), BID_UINT64 x)
OPN_BOOL(bid64_isInf, bid64_isInf(x), BID_UINT64 x)
OPN_BOOL(bid64_isSignaling, bid64_isSignaling(x), BID_UINT64 x)
OPN_BOOL(bid64_isCanonical, bid64_isCanonical(x), BID_UINT64 x)
OPN_BOOL(bid64_isNaN, bid64_isNaN(x), BID_UINT64 x)

OPN(bid64_copy, bid64_copy(x), BID_UINT64 x)
OPN(bid64_negate, bid64_negate(x), BID_UINT64 x)
OPN(bid64_abs, bid64_abs(x), BID_UINT64 x)
OPN(bid64_copySign, bid64_copySign(x, y), BID_UINT64 x, BID_UINT64 y)

OPNR(bid64_class, int32, bid64_class(x), BID_UINT64 x)
OPN_BOOL(bid64_sameQuantum, bid64_sameQuantum(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_totalOrder, bid64_totalOrder(x, y), BID_UINT64 x, BID_UINT64 y)
OPN_BOOL(bid64_totalOrderMag, bid64_totalOrderMag(x, y), BID_UINT64 x, BID_UINT64 y)
OPNR(bid64_radix, int32, bid64_radix(x), BID_UINT64 x)

OPN(bid64_rem, bid64_rem(x, y), BID_UINT64 x, BID_UINT64 y)
OPNR(bid64_ilogb, int32, bid64_ilogb(x), BID_UINT64 x)
OPN(bid64_scalbn, bid64_scalbn(x, n), BID_UINT64 x, int32 n)
OPN(bid64_ldexp, bid64_ldexp(x, n), BID_UINT64 x, int32 n)

//BID_EXTERN_C void bid64_to_string (char *ps, BID_UINT64 x
//BID_EXTERN_C BID_UINT64 bid64_from_string (char *ps

OPN(bid64_quantize, bid64_quantize(x, y), BID_UINT64 x, BID_UINT64 y)
OPNR(bid64_to_binary32, float, bid64_to_binary32(x), BID_UINT64 x)
OPNR(bid64_to_binary64, double, bid64_to_binary64(x), BID_UINT64 x)
//BID_EXTERN_C BID_UINT64 bid64_frexp (BID_UINT64 x, int *exp);
OPN(bid64_logb, bid64_logb(x), BID_UINT64 x)
OPN(bid64_scalbln, bid64_scalbln(x, n), BID_UINT64 x, int64 n)
OPN(bid64_nearbyint, bid64_nearbyint(x), BID_UINT64 x)
//BID_EXTERN_C long int bid64_lrint (BID_UINT64 x
OPNR(bid64_llrint, int64, bid64_llrint(x), BID_UINT64 x)
//BID_EXTERN_C long int bid64_lround (BID_UINT64 x
OPNR(bid64_llround, int64, bid64_llround(x), BID_UINT64 x)
//BID_EXTERN_C BID_UINT64 bid64_nan (const char *tagp);
OPN(bid64_fdim, bid64_fdim(x, y), BID_UINT64 x, BID_UINT64 y)
OPNR(bid64_quantexp, int32, bid64_quantexp(x), BID_UINT64 x)
OPN(bid64_quantum, bid64_quantum(x), BID_UINT64 x)
OPNR(bid64_llquantexp, int64, bid64_llquantexp(x), BID_UINT64 x)
//BID_EXTERN_C BID_UINT64 bid64_inf (void);


//endregion

//region Special

OPN(nextUp, bid64_nextup(x), BID_UINT64 x)
OPN(nextDown, bid64_nextdown(x), BID_UINT64 x)

//endregion


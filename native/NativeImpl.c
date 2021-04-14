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
#define JNI_API(x) x __attribute__ ((visibility("default")))
#endif

#if defined(_WIN32)
#define DDFP_API(x) __declspec(dllexport) x __cdecl
#else
#define DDFP_API(x) x __attribute__ ((visibility("default")))
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

typedef union D64Bits_t
{
    BID_UINT64  d64;
    int64       i64;
    uint64      u64;
} D64Bits;

inline D64Bits decimal64ToUnion(BID_UINT64 x) {
    D64Bits un64;
    un64.d64 = x;
    return un64;
}

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

#define OPN(mcr__name, mcr__body, ...)              OPNR(mcr__name, D64Bits, decimal64ToUnion(mcr__body), __VA_ARGS__)

#define OPN_BOOL(mcr__name, mcr__body, ...)         OPNR(mcr__name, intBool, mcr__body, __VA_ARGS__)

static const BID_UINT64 nanConst =  0x7C00000000000000ull;
static const BID_UINT64 zeroConst = 0x31C0000000000000ull;
static const BID_UINT64 twoConst =  0x31C0000000000002ull;

//region Conversion

#define OPN_FROM(mcr__type)                         OPN(PPCAT(from, mcr__type), (BID_UINT64)x, mcr__type x)
#define OPN_TO(mcr__type)                           OPNR(PPCAT(to, mcr__type), mcr__type, (mcr__type)(x.d64), D64Bits x)
#define OPN_FROM_TO(mcr__type)                      OPN_FROM(mcr__type)     OPN_TO(mcr__type)

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
OPNR(toFloat64, double, bid64_to_binary64(x.d64), D64Bits x)
OPNR(toFloat32, float, bid64_to_binary32(x.d64), D64Bits x)
OPNR(toInt64, int64, bid64_to_int64_xint(x.d64), D64Bits x)
OPNR(toUInt64, uint64, bid64_to_uint64_xint(x.d64), D64Bits x)
OPN(fromFixedPoint64, bid64_scalbn(bid64_from_int64(mantissa), -tenPowerFactor), int64 mantissa, int32 tenPowerFactor)
OPNR(toFixedPoint, int64, bid64_to_int64_xint(bid64_scalbn(value.d64, numberOfDigits)), D64Bits value, int32 numberOfDigits)

//endregion

//region Classification

OPN_BOOL(isNaN, bid64_isNaN(x.d64), D64Bits x)
OPN_BOOL(isInfinity, bid64_isInf(x.d64), D64Bits x)
OPN_BOOL(isPositiveInfinity, (intBool)(bid64_isInf(x.d64) && !bid64_isSigned(x.d64)), D64Bits x)
OPN_BOOL(isNegativeInfinity, (intBool)(bid64_isInf(x.d64) && bid64_isSigned(x.d64)), D64Bits x)
OPN_BOOL(isFinite, bid64_isFinite(x.d64), D64Bits x)
OPN_BOOL(isNormal, bid64_isNormal(x.d64), D64Bits x)
OPN_BOOL(signBit, bid64_isSigned(x.d64), D64Bits x)

//endregion

//region Comparison

DDFP_API(int32) PPCAT(API_PREFIX, compare) ( D64Bits a, D64Bits b) {
    if (bid64_quiet_less(a.d64, b.d64))
        return -1;
    if (bid64_quiet_greater(a.d64, b.d64))
        return 1;
    if (bid64_quiet_equal(a.d64, b.d64))
        return 0;
    return bid64_isNaN(b.d64) - bid64_isNaN(a.d64);
}
JNI_API(int32) PPCAT(PPCAT(Java_, JAVA_PREFIX), compare) (void *jEnv, void *jClass,  D64Bits a, D64Bits b) {
    if (bid64_quiet_less(a.d64, b.d64))
        return -1;
    if (bid64_quiet_greater(a.d64, b.d64))
        return 1;
    if (bid64_quiet_equal(a.d64, b.d64))
        return 0;
    return bid64_isNaN(a.d64) - bid64_isNaN(b.d64);
}
JNI_API(int32) PPCAT(PPCAT(JavaCritical_, JAVA_PREFIX), compare) ( D64Bits a, D64Bits b) {
    if (bid64_quiet_less(a.d64, b.d64))
        return -1;
    if (bid64_quiet_greater(a.d64, b.d64))
        return 1;
    if (bid64_quiet_equal(a.d64, b.d64))
        return 0;
    return bid64_isNaN(a.d64) - bid64_isNaN(b.d64);
}

OPN_BOOL(isEqual, bid64_quiet_equal(a.d64, b.d64), D64Bits a, D64Bits b)
OPN_BOOL(isNotEqual, bid64_quiet_not_equal(a.d64, b.d64), D64Bits a, D64Bits b)
OPN_BOOL(isLess, bid64_quiet_less(a.d64, b.d64), D64Bits a, D64Bits b)
OPN_BOOL(isLessOrEqual, bid64_quiet_less_equal(a.d64, b.d64), D64Bits a, D64Bits b)
OPN_BOOL(isGreater, bid64_quiet_greater(a.d64, b.d64), D64Bits a, D64Bits b)
OPN_BOOL(isGreaterOrEqual, bid64_quiet_greater_equal(a.d64, b.d64), D64Bits a, D64Bits b)
OPN_BOOL(isZero, bid64_isZero(a.d64), D64Bits a)
OPN_BOOL(isNonZero, bid64_quiet_not_equal(a.d64, zeroConst), D64Bits a)
OPN_BOOL(isPositive, bid64_quiet_greater(a.d64, zeroConst), D64Bits a)
OPN_BOOL(isNegative, bid64_quiet_less(a.d64, zeroConst), D64Bits a)
OPN_BOOL(isNonPositive, bid64_quiet_less_equal(a.d64, zeroConst), D64Bits a)
OPN_BOOL(isNonNegative, bid64_quiet_greater_equal(a.d64, zeroConst), D64Bits a)

//endregion

//region Rounding

OPN(roundTowardsPositiveInfinity, bid64_round_integral_positive(x.d64), D64Bits x)
OPN(roundTowardsNegativeInfinity, bid64_round_integral_negative(x.d64), D64Bits x)
OPN(roundTowardsZero, bid64_round_integral_zero(x.d64), D64Bits x)
OPN(roundToNearestTiesAwayFromZero, bid64_round_integral_nearest_away(x.d64), D64Bits x)

//endregion

//region Minimum & Maximum

OPN(max2, bid64_isNaN(a.d64 | b.d64) ? nanConst : bid64_maxnum(a.d64, b.d64), D64Bits a, D64Bits b)
OPN(max3, bid64_isNaN(a.d64 | b.d64 | c.d64) ? nanConst : bid64_maxnum(bid64_maxnum(a.d64, b.d64), c.d64), D64Bits a, D64Bits b, D64Bits c)
OPN(max4, bid64_isNaN(a.d64 | b.d64 | c.d64 | d.d64) ? nanConst : bid64_maxnum(bid64_maxnum(a.d64, b.d64), bid64_maxnum(c.d64, d.d64)), D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN(min2, bid64_isNaN(a.d64 | b.d64) ? nanConst : bid64_minnum(a.d64, b.d64), D64Bits a, D64Bits b)
OPN(min3, bid64_isNaN(a.d64 | b.d64 | c.d64) ? nanConst : bid64_minnum(bid64_minnum(a.d64, b.d64), c.d64), D64Bits a, D64Bits b, D64Bits c)
OPN(min4, bid64_isNaN(a.d64 | b.d64 | c.d64 | d.d64) ? nanConst : bid64_minnum(bid64_minnum(a.d64, b.d64), bid64_minnum(c.d64, d.d64)), D64Bits a, D64Bits b, D64Bits c, D64Bits d)

//endregion

//region Arithmetic

OPN(negate, bid64_negate(x.d64), D64Bits x)
OPN(abs, bid64_abs(x.d64), D64Bits x)
OPN(add2, bid64_add(a.d64, b.d64), D64Bits a, D64Bits b)
OPN(add3, bid64_add(bid64_add(a.d64, b.d64), c.d64), D64Bits a, D64Bits b, D64Bits c)
OPN(add4, bid64_add(bid64_add(a.d64, b.d64), bid64_add(c.d64, d.d64)), D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN(subtract, bid64_sub(a.d64, b.d64), D64Bits a, D64Bits b)
OPN(multiply2, bid64_mul(a.d64, b.d64), D64Bits a, D64Bits b)
OPN(multiply3, bid64_mul(bid64_mul(a.d64, b.d64), c.d64), D64Bits a, D64Bits b, D64Bits c)
OPN(multiply4, bid64_mul(bid64_mul(a.d64, b.d64), bid64_mul(c.d64, d.d64)), D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN(multiplyByInt32, bid64_mul(a.d64, bid64_from_int32(integer)), D64Bits a, int32 integer)
OPN(multiplyByInt64, bid64_mul(a.d64, bid64_from_int64(integer)), D64Bits a, int64 integer)
OPN(divide, bid64_div(a.d64, b.d64), D64Bits a, D64Bits b)
OPN(divideByInt32, bid64_div(x.d64, bid64_from_int32(integer)), D64Bits x, int32 integer)
OPN(divideByInt64, bid64_div(x.d64, bid64_from_int64(integer)), D64Bits x, int64 integer)
OPN(multiplyAndAdd, bid64_fma(a.d64, b.d64, c.d64), D64Bits a, D64Bits b, D64Bits c)
OPN(scaleByPowerOfTen, bid64_scalbn(a.d64, n) , D64Bits a, int32 n)
OPN(mean2, bid64_div(bid64_add(a.d64, b.d64), twoConst), D64Bits a, D64Bits b)

//endregion

//region Special

OPN(nextUp, bid64_nextup(x.d64), D64Bits x)
OPN(nextDown, bid64_nextdown(x.d64), D64Bits x)

//endregion


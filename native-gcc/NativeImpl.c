#if defined(M_PI)
#error The libC is under the LGPL license! Only the libgcc with GCC RUNTIME LIBRARY EXCEPTION can be used.
#endif

// See the "GNU Compiler Collection Internals" for the list of available DFP functions.

#ifndef API_PREFIX
#define API_PREFIX    ddfp_
#endif

#ifndef JAVA_PREFIX
#define JAVA_PREFIX    com_epam_deltix_dfp_NativeImpl_
#endif

#if defined(_WIN32) && !defined(_WIN64)
#define JNI_API(x) x __attribute__ ((stdcall, visibility("default")))
#else
#define JNI_API(x) x __attribute__ ((visibility("default")))
#endif
#define DDFP_API(x) x __attribute__ ((visibility("default")))


/*
 * Concatenate preprocessor tokens A and B without expanding macro definitions
 * (however, if invoked from a macro, macro arguments are expanded).
 */
#define PPCAT_NX(A, B) A ## B

/*
 * Concatenate preprocessor tokens A and B after macro-expanding them.
 */
#define PPCAT(A, B) PPCAT_NX(A, B)

typedef _Decimal64          decimal64;
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
    _Decimal64  d64;
    int64       i64;
} D64Bits;

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

#define OPNR(mcr__name, mcr__type, mcr__body, ...)  OPNRR(mcr__name, mcr__type, return mcr__body;, __VA_ARGS__)

#define OPN(mcr__name, mcr__body, ...)              OPNR(mcr__name, _Decimal64, mcr__body, __VA_ARGS__)

#define OPN_BOOL(mcr__name, mcr__body, ...)         OPNR(mcr__name, intBool, mcr__body, __VA_ARGS__)

DDFP_API(int32) __bid_unorddd2 ( _Decimal64 a , _Decimal64 b ); // IsAnyNan

static const _Decimal64 nanConst = 0.0DD / 0.0DD;

static const uint64 MASK_SIGN =              0x8000000000000000ll;
//static const uint64 MASK_SPECIAL =           0x6000000000000000ll;
static const uint64 MASK_INFINITY_NAN =      0x7C00000000000000ll;
static const uint64 MASK_SIGN_INFINITY_NAN = 0xFC00000000000000ll; // SIGN|INF|NAN
static const uint64 MASK_INFINITY_AND_NAN =  0x7800000000000000ll;

static const uint64 POSITIVE_INFINITY =      0x7800000000000000ll;
static const uint64 NEGATIVE_INFINITY =      0xF800000000000000ll;
static const uint64 ZERO =                   0x31C0000000000000ll; //e=0,m=0,sign=0
//static const uint64 MAX_VALUE =              0x77FB86F26FC0FFFFll;
//static const uint64 MIN_VALUE =              0xF7FB86F26FC0FFFFll;
//static const uint64 MIN_POSITIVE_VALUE =     0x0000000000000001ll;
//static const uint64 MAX_NEGATIVE_VALUE =     0x8000000000000001ll;

//region Conversion

#define OPN_FROM(mcr__type)                         OPN(PPCAT(from, mcr__type), (_Decimal64)x, mcr__type x)
#define OPN_TO(mcr__type)                           OPNR(PPCAT(to, mcr__type), mcr__type, (mcr__type)x, _Decimal64 x)
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

_Decimal64 scalbnd64(_Decimal64 x, int32 tenPowerFactor) {
    D64Bits un64;
    un64.d64 = x;
    if ((un64.i64 & MASK_INFINITY_AND_NAN) == MASK_INFINITY_AND_NAN)
        return nanConst;

    _Decimal64 factor = 1;
    _Decimal64 tenPower = 10;
    int absFactor = tenPowerFactor > 0 ? tenPowerFactor : -tenPowerFactor;
    while(absFactor) {
        if (absFactor & 1)
            factor *= tenPower;
        tenPower *= tenPower;
        absFactor >>= 1;
    }

    return tenPowerFactor >= 0 ? x * factor : x / factor;
}

int isnand64(_Decimal64 x) {
    D64Bits un64;
    un64.d64 = x;
    return (un64.i64 & MASK_INFINITY_NAN) == MASK_INFINITY_NAN;
}

OPN_FROM_TO(Float64)
OPN_FROM_TO(Float32)
OPN(fromFixedPoint64, scalbnd64((_Decimal64)mantissa, -tenPowerFactor), int64 mantissa, int32 tenPowerFactor)
OPN(fromFixedPoint32, scalbnd64((_Decimal64)mantissa, -tenPowerFactor), int32 mantissa, int32 tenPowerFactor)
OPN(fromFixedPointU32, scalbnd64((_Decimal64)mantissa, -tenPowerFactor), uint32 mantissa, int32 tenPowerFactor)
OPNR(toFixedPoint, int64, (int64)scalbnd64(value, numberOfDigits), _Decimal64 value, int32 numberOfDigits)
OPN_FROM_TO(Int64)
OPN_FROM_TO(UInt64)
OPN_FROM_TO(Int32)
OPN_FROM_TO(UInt32)
OPN_FROM_TO(Int16)
OPN_FROM_TO(UInt16)
OPN_FROM_TO(Int8)
OPN_FROM_TO(UInt8)

//endregion

//region Classification

OPN_BOOL(isNaN,  (value & MASK_INFINITY_NAN) == MASK_INFINITY_NAN, uint64 value)
OPN_BOOL(isInfinity, (value & MASK_INFINITY_NAN) == POSITIVE_INFINITY, uint64 value)
OPN_BOOL(isPositiveInfinity, (value & MASK_SIGN_INFINITY_NAN) == POSITIVE_INFINITY, uint64 value)
OPN_BOOL(isNegativeInfinity, (value & MASK_SIGN_INFINITY_NAN) == NEGATIVE_INFINITY, uint64 value)
OPN_BOOL(isFinite, (value & MASK_INFINITY_AND_NAN) != MASK_INFINITY_AND_NAN, uint64 value)
OPN_BOOL(isNormal, ((value & MASK_INFINITY_AND_NAN) != MASK_INFINITY_AND_NAN) && (value != ZERO), uint64 value)
OPN_BOOL(signBit, (value & MASK_SIGN) == MASK_SIGN, uint64 value)

//endregion

//region Comparison

DDFP_API(int32) PPCAT(API_PREFIX, compare) ( _Decimal64 a, _Decimal64 b) {
    if (a < b)
        return -1;
    if (a > b)
        return 1;
    if (a == b)
        return 0;
    return isnand64(b) - isnand64(a);
}
JNI_API(int32) PPCAT(PPCAT(Java_, JAVA_PREFIX), compare) (void *jEnv, void *jClass,  _Decimal64 a, _Decimal64 b) {
    if (a < b)
        return -1;
    if (a > b)
        return 1;
    if (a == b)
        return 0;
    return isnand64(a) - isnand64(b);
}
JNI_API(int32) PPCAT(PPCAT(JavaCritical_, JAVA_PREFIX), compare) ( _Decimal64 a, _Decimal64 b) {
    if (a < b)
        return -1;
    if (a > b)
        return 1;
    if (a == b)
        return 0;
    return isnand64(a) - isnand64(b);
}

OPN_BOOL(isEqual, a == b, _Decimal64 a, _Decimal64 b)
OPN_BOOL(isNotEqual, a != b, _Decimal64 a, _Decimal64 b)
OPN_BOOL(isLess, a < b, _Decimal64 a, _Decimal64 b)
OPN_BOOL(isLessOrEqual, a <= b, _Decimal64 a, _Decimal64 b)
OPN_BOOL(isGreater, a > b, _Decimal64 a, _Decimal64 b)
OPN_BOOL(isGreaterOrEqual, a >=b, _Decimal64 a, _Decimal64 b)
OPN_BOOL(isZero, a == 0, _Decimal64 a)
OPN_BOOL(isNonZero, a != 0, _Decimal64 a)
OPN_BOOL(isPositive, a > 0, _Decimal64 a)
OPN_BOOL(isNegative, a < 0, _Decimal64 a)
OPN_BOOL(isNonPositive, a <= 0, _Decimal64 a)
OPN_BOOL(isNonNegative, a >= 0, _Decimal64 a)

//endregion

//region Rounding

//@AD: Just compilation stub - segfault
OPNRR(roundTowardsPositiveInfinity, _Decimal64, *((int *)0) = 42; return nanConst;, _Decimal64 x)
OPNRR(roundTowardsNegativeInfinity, _Decimal64, *((int *)0) = 42; return nanConst;, _Decimal64 x)
OPNRR(roundTowardsZero, _Decimal64, *((int *)0) = 42; return nanConst;, _Decimal64 x)
OPNRR(roundToNearestTiesAwayFromZero, _Decimal64, *((int *)0) = 42; return nanConst;, _Decimal64 x)

//endregion

//region Minimum & Maximum

inline _Decimal64 noNanMax(_Decimal64 a, _Decimal64 b) {
    return a > b ? a : b;
}
inline _Decimal64 noNanMin(_Decimal64 a, _Decimal64 b) {
    return a < b ? a : b;
}

OPN(max2, __bid_unorddd2(a, b) ? nanConst : noNanMax(a, b), _Decimal64 a, _Decimal64 b)
OPN(max3, __bid_unorddd2(a, b) || __bid_unorddd2(c, c) ? nanConst : noNanMax(noNanMax(a, b), c), _Decimal64 a, _Decimal64 b, _Decimal64 c)
OPN(max4, __bid_unorddd2(a, b) || __bid_unorddd2(c, d) ? nanConst : noNanMax(noNanMax(a, b), noNanMax(c, d)), _Decimal64 a, _Decimal64 b, _Decimal64 c, _Decimal64 d)
OPN(min2, __bid_unorddd2(a, b) ? nanConst : noNanMin(a, b), _Decimal64 a, _Decimal64 b)
OPN(min3, __bid_unorddd2(a, b) || __bid_unorddd2(c, c) ? nanConst : noNanMin(noNanMin(a, b), c), _Decimal64 a, _Decimal64 b, _Decimal64 c)
OPN(min4, __bid_unorddd2(a, b) || __bid_unorddd2(c, d) ? nanConst : noNanMin(noNanMin(a, b), noNanMin(c, d)), _Decimal64 a, _Decimal64 b, _Decimal64 c, _Decimal64 d)

//endregion

//region Arithmetic

OPN(negate, -x, _Decimal64 x)
OPN(abs, x >= 0 ? x : -x, _Decimal64 x)
OPN(add2, a + b, _Decimal64 a, _Decimal64 b)
OPN(add3, a + b + c, _Decimal64 a, _Decimal64 b, _Decimal64 c)
OPN(add4, a + b + c + d, _Decimal64 a, _Decimal64 b, _Decimal64 c, _Decimal64 d)
OPN(subtract, a - b, _Decimal64 a, _Decimal64 b)
OPN(multiply2, a * b, _Decimal64 a, _Decimal64 b)
OPN(multiply3, a * b * c, _Decimal64 a, _Decimal64 b, _Decimal64 c)
OPN(multiply4, a * b * c * d, _Decimal64 a, _Decimal64 b, _Decimal64 c, _Decimal64 d)
OPN(multiplyByInt32, a * integer, _Decimal64 a, int32 integer)
OPN(multiplyByInt64, a * integer, _Decimal64 a, int64 integer)
OPN(divide, a / b, _Decimal64 a, _Decimal64 b)
OPN(divideByInt32, x / integer, _Decimal64 x, int32 integer)
OPN(divideByInt64, x / integer, _Decimal64 x, int64 integer)
OPN(multiplyAndAdd, a * b + c, _Decimal64 a, _Decimal64 b, _Decimal64 c)
OPN(scaleByPowerOfTen, scalbnd64(a, n) , _Decimal64 a, int32 n)
OPN(mean2, (a + b) / 2, _Decimal64 a, _Decimal64 b)

//endregion

//region Special

//@AD: Just compilation stub - segfault
OPNRR(nextUp, _Decimal64, *((int *)0) = 42; return nanConst;, _Decimal64 x)
OPNRR(nextDown, _Decimal64, *((int *)0) = 42; return nanConst;, _Decimal64 x)

//endregion

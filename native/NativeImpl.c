#include <bid_conf.h>
#include <bid_functions.h>

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

#if defined(_WIN32)
#define DDFP_API(x) __declspec(dllexport) x
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

#define OPN_UN64(mcr__name, mcr__body, ...)         OPNR(mcr__name, D64Bits, mcr__body, __VA_ARGS__)

#define OPN_BOOL(mcr__name, mcr__body, ...)         OPNR(mcr__name, intBool, mcr__body, __VA_ARGS__)

static const BID_UINT64 nanConst = 0x7C00000000000000ull;

static const uint64 MASK_SIGN =              0x8000000000000000ull;
//static const uint64 MASK_SPECIAL =           0x6000000000000000ull;
static const uint64 MASK_INFINITY_NAN =      0x7C00000000000000ull;
static const uint64 MASK_SIGN_INFINITY_NAN = 0xFC00000000000000ull; // SIGN|INF|NAN
static const uint64 MASK_INFINITY_AND_NAN =  0x7800000000000000ull;

static const uint64 POSITIVE_INFINITY =      0x7800000000000000ull;
static const uint64 NEGATIVE_INFINITY =      0xF800000000000000ull;
static const uint64 ZERO =                   0x31C0000000000000ull; //e=0,m=0,sign=0
//static const uint64 MAX_VALUE =              0x77FB86F26FC0FFFFull;
//static const uint64 MIN_VALUE =              0xF7FB86F26FC0FFFFull;
//static const uint64 MIN_POSITIVE_VALUE =     0x0000000000000001ull;
//static const uint64 MAX_NEGATIVE_VALUE =     0x8000000000000001ull;

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

BID_UINT64 scalbnd64(BID_UINT64 x, int32 tenPowerFactor) {
    D64Bits un64;
    un64.d64 = x;
    if ((un64.i64 & MASK_INFINITY_AND_NAN) == MASK_INFINITY_AND_NAN)
        return x;

    BID_UINT64 tenPower = 10;
    if (tenPowerFactor < 0)
        tenPower = 1 / tenPower;
    int absFactor = tenPowerFactor >= 0 ? tenPowerFactor : -tenPowerFactor;
    while(absFactor) {
        if (absFactor & 1)
            x *= tenPower;
        tenPower *= tenPower;
        absFactor >>= 1;
    }

    return x;
}

inline static int isnandun64(D64Bits un64) {
    return (un64.i64 & MASK_INFINITY_NAN) == MASK_INFINITY_NAN;
}
inline static int isnand64(BID_UINT64 x) {
    D64Bits un64;
    un64.d64 = x;
    return isnandun64(un64);
}

OPN_FROM_TO(Float64)
OPN_FROM_TO(Float32)
OPN(fromFixedPoint64, scalbnd64((BID_UINT64)mantissa, -tenPowerFactor), int64 mantissa, int32 tenPowerFactor)
OPNR(toFixedPoint, int64, (int64)scalbnd64(value.d64, numberOfDigits), D64Bits value, int32 numberOfDigits)
OPN_FROM_TO(Int64)
OPN_FROM_TO(UInt64)

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

DDFP_API(int32) PPCAT(API_PREFIX, compare) ( D64Bits a, D64Bits b) {
    if (a.d64 < b.d64)
        return -1;
    if (a.d64 > b.d64)
        return 1;
    if (a.d64 == b.d64)
        return 0;
    return isnandun64(b) - isnandun64(a);
}
JNI_API(int32) PPCAT(PPCAT(Java_, JAVA_PREFIX), compare) (void *jEnv, void *jClass,  D64Bits a, D64Bits b) {
    if (a.d64 < b.d64)
        return -1;
    if (a.d64 > b.d64)
        return 1;
    if (a.d64 == b.d64)
        return 0;
    return isnandun64(a) - isnandun64(b);
}
JNI_API(int32) PPCAT(PPCAT(JavaCritical_, JAVA_PREFIX), compare) ( D64Bits a, D64Bits b) {
    if (a.d64 < b.d64)
        return -1;
    if (a.d64 > b.d64)
        return 1;
    if (a.d64 == b.d64)
        return 0;
    return isnandun64(a) - isnandun64(b);
}

OPN_BOOL(isEqual, a.d64 == b.d64, D64Bits a, D64Bits b)
OPN_BOOL(isNotEqual, a.d64 != b.d64, D64Bits a, D64Bits b)
OPN_BOOL(isLess, a.d64 < b.d64, D64Bits a, D64Bits b)
OPN_BOOL(isLessOrEqual, a.d64 <= b.d64, D64Bits a, D64Bits b)
OPN_BOOL(isGreater, a.d64 > b.d64, D64Bits a, D64Bits b)
OPN_BOOL(isGreaterOrEqual, a.d64 >=b.d64, D64Bits a, D64Bits b)
OPN_BOOL(isZero, a.d64 == 0, D64Bits a)
OPN_BOOL(isNonZero, a.d64 != 0, D64Bits a)
OPN_BOOL(isPositive, a.d64 > 0, D64Bits a)
OPN_BOOL(isNegative, a.d64 < 0, D64Bits a)
OPN_BOOL(isNonPositive, a.d64 <= 0, D64Bits a)
OPN_BOOL(isNonNegative, a.d64 >= 0, D64Bits a)

//endregion

//region Rounding

//@AD: Just compilation stub - segfault
OPNRR(roundTowardsPositiveInfinity, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(roundTowardsNegativeInfinity, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(roundTowardsZero, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(roundToNearestTiesAwayFromZero, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)

//endregion

//region Minimum & Maximum

inline static D64Bits noNanMax(D64Bits a, D64Bits b) {
    return a.d64 > b.d64 ? a : b;
}
inline static D64Bits noNanMin(D64Bits a, D64Bits b) {
    return a.d64 < b.d64 ? a : b;
}
inline static D64Bits nanConstU() {
    D64Bits un64;
    un64.d64 = nanConst;
    return un64;
}

OPN_UN64(max2, bid64_quiet_unordered(a.d64, b.d64) ? nanConstU() : noNanMax(a, b), D64Bits a, D64Bits b)
OPN_UN64(max3, bid64_quiet_unordered(a.d64, b.d64) || bid64_quiet_unordered(c.d64, c.d64) ? nanConstU() : noNanMax(noNanMax(a, b), c), D64Bits a, D64Bits b, D64Bits c)
OPN_UN64(max4, bid64_quiet_unordered(a.d64, b.d64) || bid64_quiet_unordered(c.d64, d.d64) ? nanConstU() : noNanMax(noNanMax(a, b), noNanMax(c, d)), D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN_UN64(min2, bid64_quiet_unordered(a.d64, b.d64) ? nanConstU() : noNanMin(a, b), D64Bits a, D64Bits b)
OPN_UN64(min3, bid64_quiet_unordered(a.d64, b.d64) || bid64_quiet_unordered(c.d64, c.d64) ? nanConstU() : noNanMin(noNanMin(a, b), c), D64Bits a, D64Bits b, D64Bits c)
OPN_UN64(min4, bid64_quiet_unordered(a.d64, b.d64) || bid64_quiet_unordered(c.d64, d.d64) ? nanConstU() : noNanMin(noNanMin(a, b), noNanMin(c, d)), D64Bits a, D64Bits b, D64Bits c, D64Bits d)

//endregion

//region Arithmetic

OPN(negate, -x.d64, D64Bits x)
OPN(abs, x.d64 >= 0 ? x.d64 : -x.d64, D64Bits x)
OPN(add2, a.d64 + b.d64, D64Bits a, D64Bits b)
OPN(add3, a.d64 + b.d64 + c.d64, D64Bits a, D64Bits b, D64Bits c)
OPN(add4, a.d64 + b.d64 + c.d64 + d.d64, D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN(subtract, a.d64 - b.d64, D64Bits a, D64Bits b)
OPN(multiply2, a.d64 * b.d64, D64Bits a, D64Bits b)
OPN(multiply3, a.d64 * b.d64 * c.d64, D64Bits a, D64Bits b, D64Bits c)
OPN(multiply4, a.d64 * b.d64 * c.d64 * d.d64, D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN(multiplyByInt32, a.d64 * integer, D64Bits a, int32 integer)
OPN(multiplyByInt64, a.d64 * integer, D64Bits a, int64 integer)
OPN(divide, a.d64 / b.d64, D64Bits a, D64Bits b)
OPN(divideByInt32, x.d64 / integer, D64Bits x, int32 integer)
OPN(divideByInt64, x.d64 / integer, D64Bits x, int64 integer)
OPN(multiplyAndAdd, a.d64 * b.d64 + c.d64, D64Bits a, D64Bits b, D64Bits c)
OPN(scaleByPowerOfTen, scalbnd64(a.d64, n) , D64Bits a, int32 n)
OPN(mean2, (a.d64 + b.d64) / 2, D64Bits a, D64Bits b)

//endregion

//region Special

//@AD: Just compilation stub - segfault
OPNRR(nextUp, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(nextDown, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)

//endregion


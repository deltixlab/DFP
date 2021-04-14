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

#define OPN_UN64(mcr__name, mcr__body, ...)         OPNR(mcr__name, D64Bits, mcr__body, __VA_ARGS__)

#define OPN_BOOL(mcr__name, mcr__body, ...)         OPNR(mcr__name, intBool, mcr__body, __VA_ARGS__)

static const BID_UINT64 nanConst =  0x7C00000000000000ull;
static const BID_UINT64 zeroConst = 0x31C0000000000000ull;
static const BID_UINT64 oneConst =  0x31C0000000000001ull;
static const BID_UINT64 twoConst =  0x31C0000000000002ull;

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

inline static int isnandun64(D64Bits un64) {
    return (un64.i64 & MASK_INFINITY_NAN) == MASK_INFINITY_NAN;
}
inline static int isnand64(BID_UINT64 x) {
    D64Bits un64;
    un64.d64 = x;
    return isnandun64(un64);
}

//OPN_FROM_TO(Float64)
//OPN_FROM_TO(Float32)
//OPN(fromFixedPoint64, bid64_scalbn((BID_UINT64)mantissa, -tenPowerFactor), int64 mantissa, int32 tenPowerFactor)
//OPNR(toFixedPoint, int64, (int64)bid64_scalbn(value.d64, numberOfDigits), D64Bits value, int32 numberOfDigits)
//OPN_FROM_TO(Int64)
//OPN_FROM_TO(UInt64)

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

//@AD: Just compilation stub - segfault
OPNRR(roundTowardsPositiveInfinity, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(roundTowardsNegativeInfinity, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(roundTowardsZero, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)
OPNRR(roundToNearestTiesAwayFromZero, BID_UINT64, *((int *)0) = 42; return nanConst;, D64Bits x)

//endregion

//region Minimum & Maximum

inline static D64Bits bidToD64(BID_UINT64 x) {
    D64Bits un64;
    un64.d64 = x;
    return un64;
}

OPN_UN64(max2, bidToD64(bid64_isNaN(a.d64) || bid64_isNaN(b.d64) ? nanConst : bid64_maxnum(a.d64, b.d64)), D64Bits a, D64Bits b)
OPN_UN64(max3, bidToD64(bid64_isNaN(a.d64) || bid64_isNaN(b.d64) || bid64_isNaN(c.d64) ? nanConst : bid64_maxnum(bid64_maxnum(a.d64, b.d64), c.d64)), D64Bits a, D64Bits b, D64Bits c)
OPN_UN64(max4, bidToD64(bid64_isNaN(a.d64) || bid64_isNaN(b.d64) || bid64_isNaN(c.d64) || bid64_isNaN(d.d64) ? nanConst : bid64_maxnum(bid64_maxnum(a.d64, b.d64), bid64_maxnum(c.d64, d.d64))), D64Bits a, D64Bits b, D64Bits c, D64Bits d)
OPN_UN64(min2, bidToD64(bid64_isNaN(a.d64) || bid64_isNaN(b.d64) ? nanConst : bid64_minnum(a.d64, b.d64)), D64Bits a, D64Bits b)
OPN_UN64(min3, bidToD64(bid64_isNaN(a.d64) || bid64_isNaN(b.d64) || bid64_isNaN(c.d64) ? nanConst : bid64_minnum(bid64_minnum(a.d64, b.d64), c.d64)), D64Bits a, D64Bits b, D64Bits c)
OPN_UN64(min4, bidToD64(bid64_isNaN(a.d64) || bid64_isNaN(b.d64) || bid64_isNaN(c.d64) || bid64_isNaN(d.d64) ? nanConst : bid64_minnum(bid64_minnum(a.d64, b.d64), bid64_minnum(c.d64, d.d64))), D64Bits a, D64Bits b, D64Bits c, D64Bits d)

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
    OPN(multiplyByInt32, a.d64 * integer, D64Bits a, int32 integer)
    OPN(multiplyByInt64, a.d64 * integer, D64Bits a, int64 integer)
OPN(divide, bid64_div(a.d64, b.d64), D64Bits a, D64Bits b)
    OPN(divideByInt32, x.d64 / integer, D64Bits x, int32 integer)
    OPN(divideByInt64, x.d64 / integer, D64Bits x, int64 integer)
OPN(multiplyAndAdd, bid64_fma(a.d64, b.d64, c.d64), D64Bits a, D64Bits b, D64Bits c)
OPN(scaleByPowerOfTen, bid64_scalbn(a.d64, n) , D64Bits a, int32 n)
OPN(mean2, bid64_div(bid64_add(a.d64, b.d64), twoConst), D64Bits a, D64Bits b)

//endregion

//region Special

OPN(nextUp, bid64_nextup(x.d64), D64Bits x)
OPN(nextDown, bid64_nextdown(x.d64), D64Bits x)

//endregion


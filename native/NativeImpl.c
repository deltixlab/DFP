#define  __STDC_WANT_DEC_FP__
#include <dfp754.h>
#include <stdint.h>
#include <stdbool.h>

#define JAVA_NAMESPACE com_epam_deltix_dfp_NativeImpl
#define MULTIAPI_HEADER "NativeImpl.h"

/**
* Update native API version after doing any changes to exported method signatures or behavior
* This version number will be compared to the version number expected by the Java/.NET lib
*/
#define NATIVE_API_VERSION 2


typedef uint64_t dfp_long_t;
#define DFP_ZERO (0.DD)
#define FORCE_CAST(src_type, dst_type, value) union _dfp_ {src_type a; dst_type b;} rv = {.a = value}; return rv.b;

//#define FORCE_CAST(src_type, dst_type, value) dst_type b; memcpy(&b, &value, sizeof(dst_type)); return b;
//#define FORCE_CAST(src_type, dst_type, value) union _{src_type a; dst_type b;}; return ((union _*)&(value))->b;

inline _Decimal64 long_as_decimal(dfp_long_t value) {
    FORCE_CAST(dfp_long_t, _Decimal64, value);
}

inline dfp_long_t decimal_as_long(_Decimal64 value) {
    FORCE_CAST(_Decimal64, dfp_long_t, value);
}


static inline _Decimal64 dfp_min(_Decimal64 a, _Decimal64 b)
{
    if (isnand64(a))
        return a;
    if (isnand64(b))
        return b;
    return fmind64(a, b);
}

static inline _Decimal64 dfp_max(_Decimal64 a, _Decimal64 b)
{
    if (isnand64(a))
        return a;
    if (isnand64(b))
        return b;
    return fmaxd64(a, b);
}

#include "MultiApiLibrary.h"

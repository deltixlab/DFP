#define  __STDC_WANT_DEC_FP__

#include <stdint.h>
#include <limits.h>
#include <stdbool.h>
#include <stdio.h>
#include <mathimf.h>
#include <dfp754.h>
#include <stdlib.h>
#include <basetsd.h>
#include <mkl_vsl.h>
#include <inttypes.h>

#define MAX_INNER_ITERATIONS                1

int main() {
    char buffer[65536];
    VSLStreamStatePtr stream;
    uint64_t msb = 0xbaddbaddbaddbaddull;
    _Decimal64 decimal = *(_Decimal64 *)&msb;

    decimal64_to_string(buffer, decimal);

    printf("%016" PRIX64 ",%s\n", msb, buffer);
    
    /*

    vslNewStream(&stream, VSL_BRNG_MT19937, 777);

    // Iterate over 16 highest bits - it will cover all possible variations
    // of sign, exponent, combination bits (and a few of coefficient's).
    for (msb = 0x0000; msb <= 0xFFFF; msb += 1)
    {
        int i;
        uint64_t lsb, value;
        _Decimal64 decimal;

        // Generate random lowest 48 bits and construct the whole DFP64 value.
        viRngUniformBits64(VSL_RNG_METHOD_UNIFORM_STD, stream, 1, &lsb);

        for (i = 0; i < 48; i += 1)
        {
            value = (msb << 48) | ((lsb << (16 + i)) >> (16 + i));
            decimal = *(_Decimal64 *)&value;

            decimal64_to_string(buffer, decimal);

            printf("%016" PRIX64 ",%s\n", value, buffer);
        }
    }

    vslDeleteStream(&stream);
    */

    return 0;
}

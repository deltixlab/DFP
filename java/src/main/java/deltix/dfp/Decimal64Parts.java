package deltix.dfp;

/**
 * The class that represents the internals of 64-bit Decimal Floating Point value.
 * Supposed to be used with ver performance-sensitive code and therefore does not perform range checks
 * Also, not supposed to represent non-finite DFP64 values
 */
public class Decimal64Parts {
    long coefficient;
    int exponent;
    boolean sign;

    public Decimal64Parts() {
        this(0L, 0, false);
    }

    public Decimal64Parts(final long coefficient, final int biasedExponent, final boolean sign) {
        this.coefficient = coefficient;
        this.exponent = biasedExponent;
        this.sign = sign;
    }


    public long getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(final long coefficient) {
        this.coefficient = coefficient;
    }

    public int getBiasedExponent() {
        return exponent;
    }

    public void setBiasedExponent(final int exponent) {
        this.exponent = exponent;
    }


    public boolean isNegative() {
        return sign;
    }

    public void setNegative(final boolean negative) {
        sign = negative;
    }
}

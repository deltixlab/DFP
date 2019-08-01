package deltix.dfp;

class Decimal64Parts {
    long coefficient;
    int exponent;
    boolean sign;

    public Decimal64Parts() {
        this(0L, 0, false);
    }

    public Decimal64Parts(final long coefficient, final int exponent, final boolean sign) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.sign = sign;
    }

    public long getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(final long coefficient) {
        this.coefficient = coefficient;
    }

    public int getExponent() {
        return exponent;
    }

    public void setExponent(final int exponent) {
        this.exponent = exponent;
    }

    public boolean isNegative() {
        return sign;
    }

    public void setNegative(final boolean negative) {
        sign = negative;
    }
}

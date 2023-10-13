package week2.rmioracle.client;


import week2.rmioracle.compute.Task;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Pi implements Task<BigDecimal>, Serializable {

    private static final long serialVersionUID = 227L;

    /**
     * constants used in pi computation
     */
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    /**
     * rounding mode to use during pi computation
     */
    private static final RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    /**
     * digits of precision after the decimal point
     */
    private final int digits;

    /**
     * Construct a task to calculate pi to the specified
     * precision.
     */
    public Pi(int digits) {
        this.digits = digits;
    }

    /**
     * Compute the value of pi to the specified number of
     * digits after the decimal point.  The value is
     * computed using Machin's formula:
     * <p>
     * pi/4 = 4*arctan(1/5) - arctan(1/239)
     * <p>
     * and a power series expansion of arctan(x) to
     * sufficient precision.
     */
    public static BigDecimal computePi(int digits) {
        final var scale = digits + 5;
        final var arctan1_5 = arctan(5, scale);
        final var arctan1_239 = arctan(239, scale);

        final var pi = arctan1_5.multiply(FOUR)
                .subtract(arctan1_239)
                .multiply(FOUR);

        return pi.setScale(digits, RoundingMode.HALF_UP);
    }

    /**
     * Compute the value, in radians, of the arctangent of
     * the inverse of the supplied integer to the specified
     * number of digits after the decimal point.  The value
     * is computed using the power series expansion for the
     * arc tangent:
     * <p>
     * arctan(x) = x - (x^3)/3 + (x^5)/5 - (x^7)/7 +
     * (x^9)/9 ...
     */
    public static BigDecimal arctan(int inverseX,
                                    int scale) {
        BigDecimal result, numer, term;
        final var invX = BigDecimal.valueOf(inverseX);
        final var invX2 =
                BigDecimal.valueOf((long) inverseX * inverseX);

        numer = BigDecimal.ONE.divide(invX,
                scale, roundingMode);

        result = numer;
        var i = 1;
        do {
            numer =
                    numer.divide(invX2, scale, roundingMode);
            final var denom = 2 * i + 1;
            term =
                    numer.divide(BigDecimal.valueOf(denom),
                            scale, roundingMode);
            if ((i % 2) != 0) {
                result = result.subtract(term);
            } else {
                result = result.add(term);
            }
            i++;
        } while (term.compareTo(BigDecimal.ZERO) != 0);
        return result;
    }

    /**
     * Calculate pi.
     */
    @Override
    public BigDecimal execute() {
        return computePi(digits);
    }
}
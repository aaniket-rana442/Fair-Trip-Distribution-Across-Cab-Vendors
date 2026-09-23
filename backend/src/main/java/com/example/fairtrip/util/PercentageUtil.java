package com.example.fairtrip.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentageUtil {

    public static final int CALCULATION_SCALE = 4;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private PercentageUtil() {}

    /**
     * Calculates expected trips given total trips and target percentage.
     * expectedTrips = (totalTrips * targetPercentage) / 100
     */
    public static BigDecimal calculateExpectedTrips(long totalTrips, BigDecimal targetPercentage) {
        if (targetPercentage == null) {
            return BigDecimal.ZERO.setScale(CALCULATION_SCALE, ROUNDING_MODE);
        }
        return BigDecimal.valueOf(totalTrips)
                .multiply(targetPercentage)
                .divide(ONE_HUNDRED, CALCULATION_SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates shortfall:
     * shortfall = expectedTrips - actualTrips
     * A positive shortfall means the vendor is behind their promised share.
     * A negative shortfall means the vendor is ahead of their promised share.
     */
    public static BigDecimal calculateShortfall(BigDecimal expectedTrips, long actualTrips) {
        BigDecimal actual = BigDecimal.valueOf(actualTrips).setScale(CALCULATION_SCALE, ROUNDING_MODE);
        return expectedTrips.subtract(actual);
    }

    /**
     * Calculates actual percentage:
     * actualPercentage = (actualTrips / totalTrips) * 100
     */
    public static BigDecimal calculateActualPercentage(long actualTrips, long totalTrips) {
        if (totalTrips <= 0) {
            return BigDecimal.ZERO.setScale(2, ROUNDING_MODE);
        }
        return BigDecimal.valueOf(actualTrips)
                .multiply(ONE_HUNDRED)
                .divide(BigDecimal.valueOf(totalTrips), 2, ROUNDING_MODE);
    }

    /**
     * Validates that a collection of target percentages totals 100.00% (within tolerance of 0.01%).
     */
    public static boolean isSumCloseToOneHundred(BigDecimal total) {
        if (total == null) return false;
        BigDecimal diff = total.subtract(ONE_HUNDRED).abs();
        return diff.compareTo(new BigDecimal("0.01")) <= 0;
    }
}

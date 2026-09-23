package com.example.fairtrip.util;

import com.example.fairtrip.entity.enums.DistanceZone;
import java.math.BigDecimal;

public final class DistanceZoneUtil {

    private static final BigDecimal BOUNDARY_ZONE_15 = BigDecimal.valueOf(15.0);
    private static final BigDecimal BOUNDARY_ZONE_25 = BigDecimal.valueOf(25.0);

    private DistanceZoneUtil() {}

    /**
     * Calculates the DistanceZone from a given distance in km.
     * ZONE_0_15: [0, 15) km
     * ZONE_15_25: [15, 25) km
     * ZONE_25_PLUS: [25, infinity) km
     */
    public static DistanceZone getZone(BigDecimal distanceKm) {
        if (distanceKm == null || distanceKm.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Trip distance cannot be null or negative");
        }
        if (distanceKm.compareTo(BOUNDARY_ZONE_15) < 0) {
            return DistanceZone.ZONE_0_15;
        } else if (distanceKm.compareTo(BOUNDARY_ZONE_25) < 0) {
            return DistanceZone.ZONE_15_25;
        } else {
            return DistanceZone.ZONE_25_PLUS;
        }
    }
}

package com.example.fairtrip.entity.enums;

import java.math.BigDecimal;

public enum DistanceZone {
    ZONE_0_15("0 to < 15 km"),
    ZONE_15_25("15 to < 25 km"),
    ZONE_25_PLUS("25 km and above");

    private final String description;

    DistanceZone(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Determines the zone from distance in kilometers.
     * ZONE_0_15: 0 to less than 15 km
     * ZONE_15_25: 15 to less than 25 km
     * ZONE_25_PLUS: 25 km and above
     */
    public static DistanceZone fromDistance(BigDecimal distanceKm) {
        if (distanceKm == null || distanceKm.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Distance must be a non-negative value");
        }
        if (distanceKm.compareTo(BigDecimal.valueOf(15)) < 0) {
            return ZONE_0_15;
        } else if (distanceKm.compareTo(BigDecimal.valueOf(25)) < 0) {
            return ZONE_15_25;
        } else {
            return ZONE_25_PLUS;
        }
    }
}

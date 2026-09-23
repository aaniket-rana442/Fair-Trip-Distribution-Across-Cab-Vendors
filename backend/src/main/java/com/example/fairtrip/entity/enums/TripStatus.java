package com.example.fairtrip.entity.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum TripStatus {
    PENDING,
    ALLOCATED,
    OFFERED_TO_VENDOR,
    ACCEPTED,
    REJECTED,
    REALLOCATING,
    COMPLETED,
    CANCELLED,
    FAILED;

    private static final Map<TripStatus, Set<TripStatus>> VALID_TRANSITIONS = Map.of(
        PENDING, EnumSet.of(OFFERED_TO_VENDOR, ALLOCATED, CANCELLED, FAILED),
        OFFERED_TO_VENDOR, EnumSet.of(ACCEPTED, REJECTED, REALLOCATING, CANCELLED, FAILED),
        ALLOCATED, EnumSet.of(OFFERED_TO_VENDOR, ACCEPTED, REJECTED, CANCELLED),
        REJECTED, EnumSet.of(REALLOCATING, FAILED, CANCELLED),
        REALLOCATING, EnumSet.of(OFFERED_TO_VENDOR, FAILED, CANCELLED),
        ACCEPTED, EnumSet.of(COMPLETED, CANCELLED),
        COMPLETED, EnumSet.noneOf(TripStatus.class),
        CANCELLED, EnumSet.noneOf(TripStatus.class),
        FAILED, EnumSet.of(REALLOCATING, CANCELLED) // Admin manual retry can reallocate a failed trip
    );

    public boolean canTransitionTo(TripStatus target) {
        Set<TripStatus> allowed = VALID_TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }
}

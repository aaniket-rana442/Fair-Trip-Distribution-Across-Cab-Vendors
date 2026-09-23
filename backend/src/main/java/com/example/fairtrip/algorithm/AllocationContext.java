package com.example.fairtrip.algorithm;

import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.Vendor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Encapsulates all data required to execute an allocation decision deterministically.
 */
public class AllocationContext {

    private final Trip trip;
    private final List<Vendor> candidates;
    private final Map<Long, BigDecimal> targetPercentages;
    private final Map<Long, Long> actualTripsInStream;
    private final long totalTripsInStream;
    private final Set<Long> cooldownVendorIds;
    private final Set<Long> previouslyRejectedVendorIds;
    private final Map<Long, LocalDateTime> lastAllocationTimes;

    private AllocationContext(Builder builder) {
        this.trip = builder.trip;
        this.candidates = builder.candidates != null ? builder.candidates : Collections.emptyList();
        this.targetPercentages = builder.targetPercentages != null ? builder.targetPercentages : Collections.emptyMap();
        this.actualTripsInStream = builder.actualTripsInStream != null ? builder.actualTripsInStream : Collections.emptyMap();
        this.totalTripsInStream = builder.totalTripsInStream;
        this.cooldownVendorIds = builder.cooldownVendorIds != null ? builder.cooldownVendorIds : Collections.emptySet();
        this.previouslyRejectedVendorIds = builder.previouslyRejectedVendorIds != null ? builder.previouslyRejectedVendorIds : Collections.emptySet();
        this.lastAllocationTimes = builder.lastAllocationTimes != null ? builder.lastAllocationTimes : Collections.emptyMap();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Trip getTrip() {
        return trip;
    }

    public List<Vendor> getCandidates() {
        return candidates;
    }

    public Map<Long, BigDecimal> getTargetPercentages() {
        return targetPercentages;
    }

    public Map<Long, Long> getActualTripsInStream() {
        return actualTripsInStream;
    }

    public long getTotalTripsInStream() {
        return totalTripsInStream;
    }

    public Set<Long> getCooldownVendorIds() {
        return cooldownVendorIds;
    }

    public Set<Long> getPreviouslyRejectedVendorIds() {
        return previouslyRejectedVendorIds;
    }

    public Map<Long, LocalDateTime> getLastAllocationTimes() {
        return lastAllocationTimes;
    }

    public static class Builder {
        private Trip trip;
        private List<Vendor> candidates = new ArrayList<>();
        private Map<Long, BigDecimal> targetPercentages = new HashMap<>();
        private Map<Long, Long> actualTripsInStream = new HashMap<>();
        private long totalTripsInStream = 0;
        private Set<Long> cooldownVendorIds = new HashSet<>();
        private Set<Long> previouslyRejectedVendorIds = new HashSet<>();
        private Map<Long, LocalDateTime> lastAllocationTimes = new HashMap<>();

        public Builder trip(Trip trip) {
            this.trip = trip;
            return this;
        }

        public Builder candidates(List<Vendor> candidates) {
            this.candidates = candidates;
            return this;
        }

        public Builder targetPercentages(Map<Long, BigDecimal> targetPercentages) {
            this.targetPercentages = targetPercentages;
            return this;
        }

        public Builder actualTripsInStream(Map<Long, Long> actualTripsInStream) {
            this.actualTripsInStream = actualTripsInStream;
            return this;
        }

        public Builder totalTripsInStream(long totalTripsInStream) {
            this.totalTripsInStream = totalTripsInStream;
            return this;
        }

        public Builder cooldownVendorIds(Set<Long> cooldownVendorIds) {
            this.cooldownVendorIds = cooldownVendorIds;
            return this;
        }

        public Builder previouslyRejectedVendorIds(Set<Long> previouslyRejectedVendorIds) {
            this.previouslyRejectedVendorIds = previouslyRejectedVendorIds;
            return this;
        }

        public Builder lastAllocationTimes(Map<Long, LocalDateTime> lastAllocationTimes) {
            this.lastAllocationTimes = lastAllocationTimes;
            return this;
        }

        public AllocationContext build() {
            Objects.requireNonNull(trip, "Trip must not be null in AllocationContext");
            return new AllocationContext(this);
        }
    }
}

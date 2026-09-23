package com.example.fairtrip.algorithm;

import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.TripCategory;
import com.example.fairtrip.entity.enums.VendorStatus;
import com.example.fairtrip.util.PercentageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Deterministic Fair Allocation Strategy: Most-Owed-First (Largest Shortfall First).
 *
 * Shortfall Formula:
 *     expectedTrips = (totalTripsInStream * targetPercentage) / 100
 *     shortfall = expectedTrips - actualTrips
 *
 * A vendor with the highest positive shortfall is prioritized because they are most behind
 * their contractual commitment.
 *
 * Deterministic Tie-Breaking Order:
 * 1. Higher shortfall (descending)
 * 2. Earlier last-allocation timestamp (ascending - least recently allocated gets priority)
 * 3. Lower Vendor ID (ascending - absolute deterministic tie-breaker)
 */
@Component("shortfallAllocationStrategy")
public class ShortfallAllocationStrategy implements VendorAllocationStrategy {

    private static final Logger log = LoggerFactory.getLogger(ShortfallAllocationStrategy.class);

    private static final LocalDateTime EARLIEST_TIME = LocalDateTime.of(1970, 1, 1, 0, 0);

    @Override
    public AllocationResult selectVendor(AllocationContext context) {
        Trip trip = context.getTrip();
        List<Vendor> candidates = context.getCandidates();

        if (candidates == null || candidates.isEmpty()) {
            return AllocationResult.failure("No candidate vendors found in system").build();
        }

        List<AllocationResult.CandidateEvaluation> evaluations = new ArrayList<>();
        List<VendorScoredCandidate> eligibleScoredCandidates = new ArrayList<>();

        for (Vendor vendor : candidates) {
            Long vendorId = vendor.getId();
            BigDecimal targetPct = context.getTargetPercentages().getOrDefault(vendorId, BigDecimal.ZERO);
            long actualInStream = context.getActualTripsInStream().getOrDefault(vendorId, 0L);
            BigDecimal expectedInStream = PercentageUtil.calculateExpectedTrips(context.getTotalTripsInStream(), targetPct);
            BigDecimal shortfall = PercentageUtil.calculateShortfall(expectedInStream, actualInStream);

            // Eligibility verification
            String ineligibilityReason = checkEligibility(vendor, trip, context);
            boolean isEligible = (ineligibilityReason == null);

            evaluations.add(new AllocationResult.CandidateEvaluation(
                    vendorId,
                    vendor.getVendorCode(),
                    targetPct,
                    expectedInStream,
                    actualInStream,
                    shortfall,
                    isEligible,
                    ineligibilityReason
            ));

            if (isEligible) {
                LocalDateTime lastAlloc = context.getLastAllocationTimes().getOrDefault(vendorId, EARLIEST_TIME);
                eligibleScoredCandidates.add(new VendorScoredCandidate(
                        vendor,
                        targetPct,
                        expectedInStream,
                        actualInStream,
                        shortfall,
                        lastAlloc
                ));
            }
        }

        if (eligibleScoredCandidates.isEmpty()) {
            log.warn("No eligible vendor found for Trip #{} (Zone: {}, Category: {}). Total candidates: {}",
                    trip.getId(), trip.getDistanceZone(), trip.getTripCategory(), candidates.size());
            return AllocationResult.failure("No eligible vendor available: all candidates are either at capacity, in cooldown, or have rejected this trip")
                    .candidateEvaluations(evaluations)
                    .build();
        }

        // Deterministic Multi-Criteria Sorting
        eligibleScoredCandidates.sort(
                Comparator.comparing(VendorScoredCandidate::getShortfall, Comparator.reverseOrder()) // 1. Highest shortfall first
                        .thenComparing(VendorScoredCandidate::getLastAllocationTime)                 // 2. Least recently allocated first
                        .thenComparing(c -> c.getVendor().getId())                                   // 3. Lowest vendor ID first
        );

        VendorScoredCandidate winner = eligibleScoredCandidates.get(0);
        Vendor winningVendor = winner.getVendor();

        // Calculate state after allocation
        long newTotalTrips = context.getTotalTripsInStream() + 1;
        long newActual = winner.getActualTrips() + 1;
        BigDecimal newExpected = PercentageUtil.calculateExpectedTrips(newTotalTrips, winner.getTargetPercentage());
        BigDecimal shortfallAfter = PercentageUtil.calculateShortfall(newExpected, newActual);

        log.info("Selected Vendor {} (ID: {}) for Trip #{} - Shortfall before: {}, Shortfall after: {}",
                winningVendor.getVendorCode(), winningVendor.getId(), trip.getId(), winner.getShortfall(), shortfallAfter);

        return AllocationResult.success(winningVendor)
                .shortfallBefore(winner.getShortfall())
                .expectedBefore(winner.getExpectedTrips())
                .actualBefore(winner.getActualTrips())
                .shortfallAfter(shortfallAfter)
                .candidateEvaluations(evaluations)
                .build();
    }

    private String checkEligibility(Vendor vendor, Trip trip, AllocationContext context) {
        if (vendor.getStatus() != VendorStatus.ACTIVE) {
            return "Vendor is not ACTIVE (current status: " + vendor.getStatus() + ")";
        }
        if (!vendor.hasAvailableCapacity()) {
            return "Vendor has zero available capacity (" + vendor.getAvailableCapacity() + "/" + vendor.getTotalCapacity() + ")";
        }
        if (trip.getTripCategory() == TripCategory.ESCORT && Boolean.FALSE.equals(vendor.getSupportsEscort())) {
            return "Vendor does not support ESCORT category trips";
        }
        if (trip.getTripCategory() == TripCategory.NORMAL && Boolean.FALSE.equals(vendor.getSupportsNormal())) {
            return "Vendor does not support NORMAL category trips";
        }
        if (context.getCooldownVendorIds().contains(vendor.getId())) {
            return "Vendor is currently in cooldown for this trip";
        }
        if (context.getPreviouslyRejectedVendorIds().contains(vendor.getId())) {
            return "Vendor has already rejected this trip previously";
        }
        BigDecimal target = context.getTargetPercentages().get(vendor.getId());
        if (target == null || target.compareTo(BigDecimal.ZERO) <= 0) {
            return "Vendor has 0% target allocation configured for this stream";
        }
        return null; // Eligible!
    }

    private static class VendorScoredCandidate {
        private final Vendor vendor;
        private final BigDecimal targetPercentage;
        private final BigDecimal expectedTrips;
        private final long actualTrips;
        private final BigDecimal shortfall;
        private final LocalDateTime lastAllocationTime;

        public VendorScoredCandidate(Vendor vendor, BigDecimal targetPercentage, BigDecimal expectedTrips,
                                     long actualTrips, BigDecimal shortfall, LocalDateTime lastAllocationTime) {
            this.vendor = vendor;
            this.targetPercentage = targetPercentage;
            this.expectedTrips = expectedTrips;
            this.actualTrips = actualTrips;
            this.shortfall = shortfall;
            this.lastAllocationTime = lastAllocationTime != null ? lastAllocationTime : EARLIEST_TIME;
        }

        public Vendor getVendor() {
            return vendor;
        }

        public BigDecimal getTargetPercentage() {
            return targetPercentage;
        }

        public BigDecimal getExpectedTrips() {
            return expectedTrips;
        }

        public long getActualTrips() {
            return actualTrips;
        }

        public BigDecimal getShortfall() {
            return shortfall;
        }

        public LocalDateTime getLastAllocationTime() {
            return lastAllocationTime;
        }
    }
}

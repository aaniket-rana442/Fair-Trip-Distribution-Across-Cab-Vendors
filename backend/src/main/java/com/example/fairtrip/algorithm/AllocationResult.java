package com.example.fairtrip.algorithm;

import com.example.fairtrip.entity.Vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Output of an allocation algorithm execution.
 */
public class AllocationResult {

    private final boolean success;
    private final Vendor selectedVendor;
    private final BigDecimal shortfallBefore;
    private final BigDecimal expectedBefore;
    private final long actualBefore;
    private final BigDecimal shortfallAfter;
    private final String reason;
    private final List<CandidateEvaluation> candidateEvaluations;

    public static class CandidateEvaluation {
        private final Long vendorId;
        private final String vendorCode;
        private final BigDecimal targetPercentage;
        private final BigDecimal expectedTrips;
        private final long actualTrips;
        private final BigDecimal shortfall;
        private final boolean eligible;
        private final String ineligibilityReason;

        public CandidateEvaluation(Long vendorId, String vendorCode, BigDecimal targetPercentage,
                                   BigDecimal expectedTrips, long actualTrips, BigDecimal shortfall,
                                   boolean eligible, String ineligibilityReason) {
            this.vendorId = vendorId;
            this.vendorCode = vendorCode;
            this.targetPercentage = targetPercentage;
            this.expectedTrips = expectedTrips;
            this.actualTrips = actualTrips;
            this.shortfall = shortfall;
            this.eligible = eligible;
            this.ineligibilityReason = ineligibilityReason;
        }

        public Long getVendorId() {
            return vendorId;
        }

        public String getVendorCode() {
            return vendorCode;
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

        public boolean isEligible() {
            return eligible;
        }

        public String getIneligibilityReason() {
            return ineligibilityReason;
        }
    }

    private AllocationResult(Builder builder) {
        this.success = builder.success;
        this.selectedVendor = builder.selectedVendor;
        this.shortfallBefore = builder.shortfallBefore;
        this.expectedBefore = builder.expectedBefore;
        this.actualBefore = builder.actualBefore;
        this.shortfallAfter = builder.shortfallAfter;
        this.reason = builder.reason;
        this.candidateEvaluations = Collections.unmodifiableList(builder.candidateEvaluations);
    }

    public static Builder failure(String reason) {
        return new Builder().success(false).reason(reason);
    }

    public static Builder success(Vendor selectedVendor) {
        return new Builder().success(true).selectedVendor(selectedVendor);
    }

    public boolean isSuccess() {
        return success;
    }

    public Vendor getSelectedVendor() {
        return selectedVendor;
    }

    public BigDecimal getShortfallBefore() {
        return shortfallBefore;
    }

    public BigDecimal getExpectedBefore() {
        return expectedBefore;
    }

    public long getActualBefore() {
        return actualBefore;
    }

    public BigDecimal getShortfallAfter() {
        return shortfallAfter;
    }

    public String getReason() {
        return reason;
    }

    public List<CandidateEvaluation> getCandidateEvaluations() {
        return candidateEvaluations;
    }

    public static class Builder {
        private boolean success;
        private Vendor selectedVendor;
        private BigDecimal shortfallBefore = BigDecimal.ZERO;
        private BigDecimal expectedBefore = BigDecimal.ZERO;
        private long actualBefore = 0;
        private BigDecimal shortfallAfter = BigDecimal.ZERO;
        private String reason;
        private List<CandidateEvaluation> candidateEvaluations = new ArrayList<>();

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder selectedVendor(Vendor selectedVendor) {
            this.selectedVendor = selectedVendor;
            return this;
        }

        public Builder shortfallBefore(BigDecimal shortfallBefore) {
            this.shortfallBefore = shortfallBefore;
            return this;
        }

        public Builder expectedBefore(BigDecimal expectedBefore) {
            this.expectedBefore = expectedBefore;
            return this;
        }

        public Builder actualBefore(long actualBefore) {
            this.actualBefore = actualBefore;
            return this;
        }

        public Builder shortfallAfter(BigDecimal shortfallAfter) {
            this.shortfallAfter = shortfallAfter;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder candidateEvaluations(List<CandidateEvaluation> candidateEvaluations) {
            this.candidateEvaluations = candidateEvaluations;
            return this;
        }

        public Builder addCandidateEvaluation(CandidateEvaluation evaluation) {
            this.candidateEvaluations.add(evaluation);
            return this;
        }

        public AllocationResult build() {
            return new AllocationResult(this);
        }
    }
}

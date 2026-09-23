package com.example.fairtrip.dto;

import java.math.BigDecimal;
import java.util.List;

public class SimulationResponse {

    private int totalSimulated;
    private String zone;
    private String tripCategory;
    private List<VendorSimulationResult> vendorResults;
    private String summaryMessage;

    public static class VendorSimulationResult {
        private String vendorCode;
        private String vendorName;
        private BigDecimal targetPercentage;
        private BigDecimal expectedTrips;
        private long actualTripsAllocated;
        private BigDecimal actualPercentage;
        private BigDecimal shortfall;
        private BigDecimal deviationPercentage;

        public VendorSimulationResult() {}

        public String getVendorCode() {
            return vendorCode;
        }

        public void setVendorCode(String vendorCode) {
            this.vendorCode = vendorCode;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public BigDecimal getTargetPercentage() {
            return targetPercentage;
        }

        public void setTargetPercentage(BigDecimal targetPercentage) {
            this.targetPercentage = targetPercentage;
        }

        public BigDecimal getExpectedTrips() {
            return expectedTrips;
        }

        public void setExpectedTrips(BigDecimal expectedTrips) {
            this.expectedTrips = expectedTrips;
        }

        public long getActualTripsAllocated() {
            return actualTripsAllocated;
        }

        public void setActualTripsAllocated(long actualTripsAllocated) {
            this.actualTripsAllocated = actualTripsAllocated;
        }

        public BigDecimal getActualPercentage() {
            return actualPercentage;
        }

        public void setActualPercentage(BigDecimal actualPercentage) {
            this.actualPercentage = actualPercentage;
        }

        public BigDecimal getShortfall() {
            return shortfall;
        }

        public void setShortfall(BigDecimal shortfall) {
            this.shortfall = shortfall;
        }

        public BigDecimal getDeviationPercentage() {
            return deviationPercentage;
        }

        public void setDeviationPercentage(BigDecimal deviationPercentage) {
            this.deviationPercentage = deviationPercentage;
        }
    }

    public SimulationResponse() {}

    public int getTotalSimulated() {
        return totalSimulated;
    }

    public void setTotalSimulated(int totalSimulated) {
        this.totalSimulated = totalSimulated;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getTripCategory() {
        return tripCategory;
    }

    public void setTripCategory(String tripCategory) {
        this.tripCategory = tripCategory;
    }

    public List<VendorSimulationResult> getVendorResults() {
        return vendorResults;
    }

    public void setVendorResults(List<VendorSimulationResult> vendorResults) {
        this.vendorResults = vendorResults;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }
}

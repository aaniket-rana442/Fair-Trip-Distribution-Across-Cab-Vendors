package com.example.fairtrip.dto;

import java.math.BigDecimal;
import java.util.List;

public class VendorStatsResponse {

    private Long vendorId;
    private String vendorCode;
    private String vendorName;

    // Capacity & Status
    private Integer totalCapacity;
    private Integer availableCapacity;
    private boolean onCooldown;
    private String cooldownDetails;

    // Overall metrics
    private BigDecimal overallTargetPercentage;
    private long overallExpectedTrips;
    private long overallActualTrips;
    private BigDecimal currentShortfall;
    private BigDecimal currentSurplus;

    // Today's metrics
    private BigDecimal todayTargetPercentage;
    private BigDecimal todayExpectedTrips;
    private long todayActualTrips;
    private BigDecimal todayShortfall;

    // Counts
    private long totalAllocatedTrips;
    private long acceptedTrips;
    private long rejectedTrips;
    private long completedTrips;

    // Stream breakdowns
    private List<StreamMetric> zoneMetrics;
    private List<StreamMetric> categoryMetrics;

    public static class StreamMetric {
        private String name;
        private BigDecimal targetPercentage;
        private BigDecimal expectedTrips;
        private long actualTrips;
        private BigDecimal shortfall;

        public StreamMetric() {}

        public StreamMetric(String name, BigDecimal targetPercentage, BigDecimal expectedTrips, long actualTrips, BigDecimal shortfall) {
            this.name = name;
            this.targetPercentage = targetPercentage;
            this.expectedTrips = expectedTrips;
            this.actualTrips = actualTrips;
            this.shortfall = shortfall;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public long getActualTrips() {
            return actualTrips;
        }

        public void setActualTrips(long actualTrips) {
            this.actualTrips = actualTrips;
        }

        public BigDecimal getShortfall() {
            return shortfall;
        }

        public void setShortfall(BigDecimal shortfall) {
            this.shortfall = shortfall;
        }
    }

    public VendorStatsResponse() {}

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

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

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }

    public void setOnCooldown(boolean onCooldown) {
        this.onCooldown = onCooldown;
    }

    public String getCooldownDetails() {
        return cooldownDetails;
    }

    public void setCooldownDetails(String cooldownDetails) {
        this.cooldownDetails = cooldownDetails;
    }

    public BigDecimal getOverallTargetPercentage() {
        return overallTargetPercentage;
    }

    public void setOverallTargetPercentage(BigDecimal overallTargetPercentage) {
        this.overallTargetPercentage = overallTargetPercentage;
    }

    public long getOverallExpectedTrips() {
        return overallExpectedTrips;
    }

    public void setOverallExpectedTrips(long overallExpectedTrips) {
        this.overallExpectedTrips = overallExpectedTrips;
    }

    public long getOverallActualTrips() {
        return overallActualTrips;
    }

    public void setOverallActualTrips(long overallActualTrips) {
        this.overallActualTrips = overallActualTrips;
    }

    public BigDecimal getCurrentShortfall() {
        return currentShortfall;
    }

    public void setCurrentShortfall(BigDecimal currentShortfall) {
        this.currentShortfall = currentShortfall;
    }

    public BigDecimal getCurrentSurplus() {
        return currentSurplus;
    }

    public void setCurrentSurplus(BigDecimal currentSurplus) {
        this.currentSurplus = currentSurplus;
    }

    public BigDecimal getTodayTargetPercentage() {
        return todayTargetPercentage;
    }

    public void setTodayTargetPercentage(BigDecimal todayTargetPercentage) {
        this.todayTargetPercentage = todayTargetPercentage;
    }

    public BigDecimal getTodayExpectedTrips() {
        return todayExpectedTrips;
    }

    public void setTodayExpectedTrips(BigDecimal todayExpectedTrips) {
        this.todayExpectedTrips = todayExpectedTrips;
    }

    public long getTodayActualTrips() {
        return todayActualTrips;
    }

    public void setTodayActualTrips(long todayActualTrips) {
        this.todayActualTrips = todayActualTrips;
    }

    public BigDecimal getTodayShortfall() {
        return todayShortfall;
    }

    public void setTodayShortfall(BigDecimal todayShortfall) {
        this.todayShortfall = todayShortfall;
    }

    public long getTotalAllocatedTrips() {
        return totalAllocatedTrips;
    }

    public void setTotalAllocatedTrips(long totalAllocatedTrips) {
        this.totalAllocatedTrips = totalAllocatedTrips;
    }

    public long getAcceptedTrips() {
        return acceptedTrips;
    }

    public void setAcceptedTrips(long acceptedTrips) {
        this.acceptedTrips = acceptedTrips;
    }

    public long getRejectedTrips() {
        return rejectedTrips;
    }

    public void setRejectedTrips(long rejectedTrips) {
        this.rejectedTrips = rejectedTrips;
    }

    public long getCompletedTrips() {
        return completedTrips;
    }

    public void setCompletedTrips(long completedTrips) {
        this.completedTrips = completedTrips;
    }

    public List<StreamMetric> getZoneMetrics() {
        return zoneMetrics;
    }

    public void setZoneMetrics(List<StreamMetric> zoneMetrics) {
        this.zoneMetrics = zoneMetrics;
    }

    public List<StreamMetric> getCategoryMetrics() {
        return categoryMetrics;
    }

    public void setCategoryMetrics(List<StreamMetric> categoryMetrics) {
        this.categoryMetrics = categoryMetrics;
    }
}

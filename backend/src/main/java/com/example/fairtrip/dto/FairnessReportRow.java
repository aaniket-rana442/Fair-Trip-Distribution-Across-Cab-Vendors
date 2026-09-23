package com.example.fairtrip.dto;

import java.math.BigDecimal;

public class FairnessReportRow {

    private Long vendorId;
    private String vendorCode;
    private String vendorName;
    private String zone;
    private String tripCategory;
    private BigDecimal targetPercentage;
    private BigDecimal expectedTrips;
    private long actualTrips;
    private BigDecimal shortfall;
    private BigDecimal actualPercentage;

    public FairnessReportRow() {}

    public FairnessReportRow(Long vendorId, String vendorCode, String vendorName, String zone,
                             String tripCategory, BigDecimal targetPercentage, BigDecimal expectedTrips,
                             long actualTrips, BigDecimal shortfall, BigDecimal actualPercentage) {
        this.vendorId = vendorId;
        this.vendorCode = vendorCode;
        this.vendorName = vendorName;
        this.zone = zone;
        this.tripCategory = tripCategory;
        this.targetPercentage = targetPercentage;
        this.expectedTrips = expectedTrips;
        this.actualTrips = actualTrips;
        this.shortfall = shortfall;
        this.actualPercentage = actualPercentage;
    }

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

    public BigDecimal getActualPercentage() {
        return actualPercentage;
    }

    public void setActualPercentage(BigDecimal actualPercentage) {
        this.actualPercentage = actualPercentage;
    }
}

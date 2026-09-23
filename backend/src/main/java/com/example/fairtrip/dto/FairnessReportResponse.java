package com.example.fairtrip.dto;

import java.util.List;

public class FairnessReportResponse {

    private String filterPeriod;
    private String filterZone;
    private String filterCategory;
    private Long filterVendorId;
    private long totalTripsInScope;
    private List<FairnessReportRow> rows;

    public FairnessReportResponse() {}

    public FairnessReportResponse(String filterPeriod, String filterZone, String filterCategory,
                                  Long filterVendorId, long totalTripsInScope, List<FairnessReportRow> rows) {
        this.filterPeriod = filterPeriod;
        this.filterZone = filterZone;
        this.filterCategory = filterCategory;
        this.filterVendorId = filterVendorId;
        this.totalTripsInScope = totalTripsInScope;
        this.rows = rows;
    }

    public String getFilterPeriod() {
        return filterPeriod;
    }

    public void setFilterPeriod(String filterPeriod) {
        this.filterPeriod = filterPeriod;
    }

    public String getFilterZone() {
        return filterZone;
    }

    public void setFilterZone(String filterZone) {
        this.filterZone = filterZone;
    }

    public String getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }

    public Long getFilterVendorId() {
        return filterVendorId;
    }

    public void setFilterVendorId(Long filterVendorId) {
        this.filterVendorId = filterVendorId;
    }

    public long getTotalTripsInScope() {
        return totalTripsInScope;
    }

    public void setTotalTripsInScope(long totalTripsInScope) {
        this.totalTripsInScope = totalTripsInScope;
    }

    public List<FairnessReportRow> getRows() {
        return rows;
    }

    public void setRows(List<FairnessReportRow> rows) {
        this.rows = rows;
    }
}

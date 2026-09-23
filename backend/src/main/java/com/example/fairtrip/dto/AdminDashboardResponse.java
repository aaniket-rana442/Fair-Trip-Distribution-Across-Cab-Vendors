package com.example.fairtrip.dto;

import java.util.List;

public class AdminDashboardResponse {

    // Trip counters
    private long totalTripsToday;
    private long pendingTrips;
    private long allocatedTrips;
    private long acceptedTrips;
    private long rejectedTrips;
    private long completedTrips;
    private long failedTrips;

    // Vendor counters
    private long totalVendors;
    private long activeVendors;
    private long vendorsOnCooldown;

    // Vendor overviews
    private List<VendorResponse> vendors;

    // Recent pending trips
    private List<TripResponse> pendingTripsList;

    public AdminDashboardResponse() {}

    public long getTotalTripsToday() {
        return totalTripsToday;
    }

    public void setTotalTripsToday(long totalTripsToday) {
        this.totalTripsToday = totalTripsToday;
    }

    public long getPendingTrips() {
        return pendingTrips;
    }

    public void setPendingTrips(long pendingTrips) {
        this.pendingTrips = pendingTrips;
    }

    public long getAllocatedTrips() {
        return allocatedTrips;
    }

    public void setAllocatedTrips(long allocatedTrips) {
        this.allocatedTrips = allocatedTrips;
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

    public long getFailedTrips() {
        return failedTrips;
    }

    public void setFailedTrips(long failedTrips) {
        this.failedTrips = failedTrips;
    }

    public long getTotalVendors() {
        return totalVendors;
    }

    public void setTotalVendors(long totalVendors) {
        this.totalVendors = totalVendors;
    }

    public long getActiveVendors() {
        return activeVendors;
    }

    public void setActiveVendors(long activeVendors) {
        this.activeVendors = activeVendors;
    }

    public long getVendorsOnCooldown() {
        return vendorsOnCooldown;
    }

    public void setVendorsOnCooldown(long vendorsOnCooldown) {
        this.vendorsOnCooldown = vendorsOnCooldown;
    }

    public List<VendorResponse> getVendors() {
        return vendors;
    }

    public void setVendors(List<VendorResponse> vendors) {
        this.vendors = vendors;
    }

    public List<TripResponse> getPendingTripsList() {
        return pendingTripsList;
    }

    public void setPendingTripsList(List<TripResponse> pendingTripsList) {
        this.pendingTripsList = pendingTripsList;
    }
}

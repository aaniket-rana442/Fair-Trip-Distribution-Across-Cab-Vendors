package com.example.fairtrip.dto;

import com.example.fairtrip.entity.enums.AllocationMethod;
import com.example.fairtrip.entity.enums.AllocationStatus;
import com.example.fairtrip.entity.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AllocationResponse {

    private Long tripId;
    private Long vendorId;
    private String vendorCode;
    private String vendorName;
    private Integer allocationSequence;
    private AllocationStatus allocationStatus;
    private AllocationMethod allocationMethod;
    private TripStatus tripStatus;
    private LocalDateTime offeredAt;
    private LocalDateTime responseDeadline;
    private BigDecimal shortfallBefore;
    private BigDecimal shortfallAfter;
    private String message;

    public AllocationResponse() {}

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
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

    public Integer getAllocationSequence() {
        return allocationSequence;
    }

    public void setAllocationSequence(Integer allocationSequence) {
        this.allocationSequence = allocationSequence;
    }

    public AllocationStatus getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(AllocationStatus allocationStatus) {
        this.allocationStatus = allocationStatus;
    }

    public AllocationMethod getAllocationMethod() {
        return allocationMethod;
    }

    public void setAllocationMethod(AllocationMethod allocationMethod) {
        this.allocationMethod = allocationMethod;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public LocalDateTime getOfferedAt() {
        return offeredAt;
    }

    public void setOfferedAt(LocalDateTime offeredAt) {
        this.offeredAt = offeredAt;
    }

    public LocalDateTime getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(LocalDateTime responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public BigDecimal getShortfallBefore() {
        return shortfallBefore;
    }

    public void setShortfallBefore(BigDecimal shortfallBefore) {
        this.shortfallBefore = shortfallBefore;
    }

    public BigDecimal getShortfallAfter() {
        return shortfallAfter;
    }

    public void setShortfallAfter(BigDecimal shortfallAfter) {
        this.shortfallAfter = shortfallAfter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

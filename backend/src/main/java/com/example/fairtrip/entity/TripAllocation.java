package com.example.fairtrip.entity;

import com.example.fairtrip.entity.enums.AllocationMethod;
import com.example.fairtrip.entity.enums.AllocationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_allocations")
public class TripAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(name = "allocation_sequence", nullable = false)
    private Integer allocationSequence = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_status", nullable = false, length = 30)
    private AllocationStatus allocationStatus = AllocationStatus.OFFERED;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_method", nullable = false, length = 30)
    private AllocationMethod allocationMethod = AllocationMethod.AUTOMATIC;

    @Column(name = "offered_at", nullable = false)
    private LocalDateTime offeredAt = LocalDateTime.now();

    @Column(name = "response_deadline", nullable = false)
    private LocalDateTime responseDeadline;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TripAllocation() {}

    public TripAllocation(Trip trip, Vendor vendor, Integer allocationSequence,
                          AllocationMethod allocationMethod, LocalDateTime responseDeadline) {
        this.trip = trip;
        this.vendor = vendor;
        this.allocationSequence = allocationSequence;
        this.allocationMethod = allocationMethod != null ? allocationMethod : AllocationMethod.AUTOMATIC;
        this.allocationStatus = AllocationStatus.OFFERED;
        this.offeredAt = LocalDateTime.now();
        this.responseDeadline = responseDeadline;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
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

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

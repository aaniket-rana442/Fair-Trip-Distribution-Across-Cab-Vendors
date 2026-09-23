package com.example.fairtrip.entity;

import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "allocation_history")
public class AllocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DistanceZone zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_category", nullable = false, length = 30)
    private TripCategory tripCategory;

    @Column(name = "shortfall_before", nullable = false, precision = 10, scale = 4)
    private BigDecimal shortfallBefore;

    @Column(name = "expected_before", nullable = false, precision = 10, scale = 4)
    private BigDecimal expectedBefore;

    @Column(name = "actual_before", nullable = false)
    private Integer actualBefore;

    @Column(name = "shortfall_after", nullable = false, precision = 10, scale = 4)
    private BigDecimal shortfallAfter;

    @Column(name = "allocation_sequence", nullable = false)
    private Integer allocationSequence;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AllocationHistory() {}

    public AllocationHistory(Trip trip, Vendor vendor, DistanceZone zone, TripCategory tripCategory,
                             BigDecimal shortfallBefore, BigDecimal expectedBefore, Integer actualBefore,
                             BigDecimal shortfallAfter, Integer allocationSequence) {
        this.trip = trip;
        this.vendor = vendor;
        this.zone = zone;
        this.tripCategory = tripCategory;
        this.shortfallBefore = shortfallBefore;
        this.expectedBefore = expectedBefore;
        this.actualBefore = actualBefore;
        this.shortfallAfter = shortfallAfter;
        this.allocationSequence = allocationSequence;
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

    public DistanceZone getZone() {
        return zone;
    }

    public void setZone(DistanceZone zone) {
        this.zone = zone;
    }

    public TripCategory getTripCategory() {
        return tripCategory;
    }

    public void setTripCategory(TripCategory tripCategory) {
        this.tripCategory = tripCategory;
    }

    public BigDecimal getShortfallBefore() {
        return shortfallBefore;
    }

    public void setShortfallBefore(BigDecimal shortfallBefore) {
        this.shortfallBefore = shortfallBefore;
    }

    public BigDecimal getExpectedBefore() {
        return expectedBefore;
    }

    public void setExpectedBefore(BigDecimal expectedBefore) {
        this.expectedBefore = expectedBefore;
    }

    public Integer getActualBefore() {
        return actualBefore;
    }

    public void setActualBefore(Integer actualBefore) {
        this.actualBefore = actualBefore;
    }

    public BigDecimal getShortfallAfter() {
        return shortfallAfter;
    }

    public void setShortfallAfter(BigDecimal shortfallAfter) {
        this.shortfallAfter = shortfallAfter;
    }

    public Integer getAllocationSequence() {
        return allocationSequence;
    }

    public void setAllocationSequence(Integer allocationSequence) {
        this.allocationSequence = allocationSequence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

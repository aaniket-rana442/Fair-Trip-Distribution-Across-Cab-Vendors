package com.example.fairtrip.entity;

import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fairness_snapshots")
public class FairnessSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DistanceZone zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_category", nullable = false, length = 30)
    private TripCategory tripCategory;

    @Column(name = "period_date", nullable = false)
    private LocalDate periodDate;

    @Column(name = "target_percentage", nullable = false, precision = 7, scale = 4)
    private BigDecimal targetPercentage;

    @Column(name = "expected_trips", nullable = false, precision = 10, scale = 4)
    private BigDecimal expectedTrips;

    @Column(name = "actual_trips", nullable = false)
    private Integer actualTrips;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal shortfall;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FairnessSnapshot() {}

    public FairnessSnapshot(Vendor vendor, DistanceZone zone, TripCategory tripCategory, LocalDate periodDate,
                            BigDecimal targetPercentage, BigDecimal expectedTrips, Integer actualTrips, BigDecimal shortfall) {
        this.vendor = vendor;
        this.zone = zone;
        this.tripCategory = tripCategory;
        this.periodDate = periodDate;
        this.targetPercentage = targetPercentage;
        this.expectedTrips = expectedTrips;
        this.actualTrips = actualTrips;
        this.shortfall = shortfall;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(LocalDate periodDate) {
        this.periodDate = periodDate;
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

    public Integer getActualTrips() {
        return actualTrips;
    }

    public void setActualTrips(Integer actualTrips) {
        this.actualTrips = actualTrips;
    }

    public BigDecimal getShortfall() {
        return shortfall;
    }

    public void setShortfall(BigDecimal shortfall) {
        this.shortfall = shortfall;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

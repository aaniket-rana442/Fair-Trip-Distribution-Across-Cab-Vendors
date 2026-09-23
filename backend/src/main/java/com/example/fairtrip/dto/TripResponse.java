package com.example.fairtrip.dto;

import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import com.example.fairtrip.entity.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String passengerName;
    private String pickupLocation;
    private String destination;
    private Integer passengerCount;
    private BigDecimal distanceKm;
    private DistanceZone distanceZone;
    private String distanceZoneDescription;
    private TripCategory tripCategory;
    private String notes;
    private TripStatus status;
    private Long allocatedVendorId;
    private String allocatedVendorCode;
    private String allocatedVendorName;
    private LocalDateTime responseDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TripResponse() {}

    public static TripResponse fromEntity(Trip trip) {
        TripResponse dto = new TripResponse();
        dto.setId(trip.getId());
        if (trip.getUser() != null) {
            dto.setUserId(trip.getUser().getId());
            dto.setUserName(trip.getUser().getName());
        }
        dto.setPassengerName(trip.getPassengerName());
        dto.setPickupLocation(trip.getPickupLocation());
        dto.setDestination(trip.getDestination());
        dto.setPassengerCount(trip.getPassengerCount());
        dto.setDistanceKm(trip.getDistanceKm());
        dto.setDistanceZone(trip.getDistanceZone());
        dto.setDistanceZoneDescription(trip.getDistanceZone() != null ? trip.getDistanceZone().getDescription() : null);
        dto.setTripCategory(trip.getTripCategory());
        dto.setNotes(trip.getNotes());
        dto.setStatus(trip.getStatus());
        if (trip.getAllocatedVendor() != null) {
            dto.setAllocatedVendorId(trip.getAllocatedVendor().getId());
            dto.setAllocatedVendorCode(trip.getAllocatedVendor().getVendorCode());
            dto.setAllocatedVendorName(trip.getAllocatedVendor().getVendorName());
        }
        dto.setCreatedAt(trip.getCreatedAt());
        dto.setUpdatedAt(trip.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public DistanceZone getDistanceZone() {
        return distanceZone;
    }

    public void setDistanceZone(DistanceZone distanceZone) {
        this.distanceZone = distanceZone;
    }

    public String getDistanceZoneDescription() {
        return distanceZoneDescription;
    }

    public void setDistanceZoneDescription(String distanceZoneDescription) {
        this.distanceZoneDescription = distanceZoneDescription;
    }

    public TripCategory getTripCategory() {
        return tripCategory;
    }

    public void setTripCategory(TripCategory tripCategory) {
        this.tripCategory = tripCategory;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public Long getAllocatedVendorId() {
        return allocatedVendorId;
    }

    public void setAllocatedVendorId(Long allocatedVendorId) {
        this.allocatedVendorId = allocatedVendorId;
    }

    public String getAllocatedVendorCode() {
        return allocatedVendorCode;
    }

    public void setAllocatedVendorCode(String allocatedVendorCode) {
        this.allocatedVendorCode = allocatedVendorCode;
    }

    public String getAllocatedVendorName() {
        return allocatedVendorName;
    }

    public void setAllocatedVendorName(String allocatedVendorName) {
        this.allocatedVendorName = allocatedVendorName;
    }

    public LocalDateTime getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(LocalDateTime responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

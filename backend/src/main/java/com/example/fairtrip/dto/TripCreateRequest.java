package com.example.fairtrip.dto;

import com.example.fairtrip.entity.enums.TripCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TripCreateRequest {

    @NotBlank(message = "Passenger name is required")
    @Size(max = 100, message = "Passenger name cannot exceed 100 characters")
    private String passengerName;

    @NotBlank(message = "Pickup location is required")
    @Size(max = 255, message = "Pickup location cannot exceed 255 characters")
    private String pickupLocation;

    @NotBlank(message = "Destination is required")
    @Size(max = 255, message = "Destination cannot exceed 255 characters")
    private String destination;

    @NotNull(message = "Passenger count is required")
    @Min(value = 1, message = "Passenger count must be at least 1")
    @Max(value = 50, message = "Passenger count cannot exceed 50")
    private Integer passengerCount = 1;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Distance must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid distance format")
    private BigDecimal distanceKm;

    @NotNull(message = "Trip category is required")
    private TripCategory tripCategory = TripCategory.NORMAL;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public TripCreateRequest() {}

    public TripCreateRequest(String passengerName, String pickupLocation, String destination,
                             Integer passengerCount, BigDecimal distanceKm, TripCategory tripCategory, String notes) {
        this.passengerName = passengerName;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.distanceKm = distanceKm;
        this.tripCategory = tripCategory;
        this.notes = notes;
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
}

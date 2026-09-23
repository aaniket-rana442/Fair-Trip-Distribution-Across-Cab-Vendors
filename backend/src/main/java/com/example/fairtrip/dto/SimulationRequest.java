package com.example.fairtrip.dto;

import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class SimulationRequest {

    @Min(value = 1, message = "Must simulate at least 1 trip")
    @Max(value = 1000, message = "Cannot simulate more than 1000 trips at once")
    private int tripCount = 100;

    private DistanceZone zone = DistanceZone.ZONE_0_15;
    private TripCategory tripCategory = TripCategory.NORMAL;

    public SimulationRequest() {}

    public SimulationRequest(int tripCount, DistanceZone zone, TripCategory tripCategory) {
        this.tripCount = tripCount;
        this.zone = zone;
        this.tripCategory = tripCategory;
    }

    public int getTripCount() {
        return tripCount;
    }

    public void setTripCount(int tripCount) {
        this.tripCount = tripCount;
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
}

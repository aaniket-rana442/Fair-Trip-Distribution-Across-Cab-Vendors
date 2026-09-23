package com.example.fairtrip.dto;

import com.example.fairtrip.entity.VendorTarget;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class VendorTargetDto {

    private Long id;
    private Long vendorId;
    private String vendorCode;

    @NotNull(message = "Distance zone is required")
    private DistanceZone zone;

    @NotNull(message = "Trip category is required")
    private TripCategory tripCategory;

    @NotNull(message = "Target percentage is required")
    @DecimalMin(value = "0.0", message = "Target percentage must be >= 0")
    @DecimalMax(value = "100.0", message = "Target percentage must be <= 100")
    private BigDecimal targetPercentage;

    public VendorTargetDto() {}

    public VendorTargetDto(DistanceZone zone, TripCategory tripCategory, BigDecimal targetPercentage) {
        this.zone = zone;
        this.tripCategory = tripCategory;
        this.targetPercentage = targetPercentage;
    }

    public static VendorTargetDto fromEntity(VendorTarget entity) {
        VendorTargetDto dto = new VendorTargetDto();
        dto.setId(entity.getId());
        if (entity.getVendor() != null) {
            dto.setVendorId(entity.getVendor().getId());
            dto.setVendorCode(entity.getVendor().getVendorCode());
        }
        dto.setZone(entity.getZone());
        dto.setTripCategory(entity.getTripCategory());
        dto.setTargetPercentage(entity.getTargetPercentage());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getTargetPercentage() {
        return targetPercentage;
    }

    public void setTargetPercentage(BigDecimal targetPercentage) {
        this.targetPercentage = targetPercentage;
    }
}

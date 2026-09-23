package com.example.fairtrip.dto;

import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.VendorStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VendorResponse {

    private Long id;
    private Long userId;
    private String vendorCode;
    private String vendorName;
    private String contactName;
    private String email;
    private String phone;
    private VendorStatus status;
    private Integer totalCapacity;
    private Integer availableCapacity;
    private Boolean supportsNormal;
    private Boolean supportsEscort;
    private Long version;
    private boolean onCooldown;
    private BigDecimal averageTargetPercentage;
    private BigDecimal actualPercentage;
    private BigDecimal currentShortfall;
    private long totalAllocatedTrips;
    private long totalAcceptedTrips;
    private long totalRejectedTrips;
    private List<VendorTargetDto> targets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VendorResponse() {}

    public static VendorResponse fromEntity(Vendor vendor) {
        VendorResponse dto = new VendorResponse();
        dto.setId(vendor.getId());
        if (vendor.getUser() != null) {
            dto.setUserId(vendor.getUser().getId());
        }
        dto.setVendorCode(vendor.getVendorCode());
        dto.setVendorName(vendor.getVendorName());
        dto.setContactName(vendor.getContactName());
        dto.setEmail(vendor.getEmail());
        dto.setPhone(vendor.getPhone());
        dto.setStatus(vendor.getStatus());
        dto.setTotalCapacity(vendor.getTotalCapacity());
        dto.setAvailableCapacity(vendor.getAvailableCapacity());
        dto.setSupportsNormal(vendor.getSupportsNormal());
        dto.setSupportsEscort(vendor.getSupportsEscort());
        dto.setVersion(vendor.getVersion());
        dto.setCreatedAt(vendor.getCreatedAt());
        dto.setUpdatedAt(vendor.getUpdatedAt());
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public VendorStatus getStatus() {
        return status;
    }

    public void setStatus(VendorStatus status) {
        this.status = status;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public Boolean getSupportsNormal() {
        return supportsNormal;
    }

    public void setSupportsNormal(Boolean supportsNormal) {
        this.supportsNormal = supportsNormal;
    }

    public Boolean getSupportsEscort() {
        return supportsEscort;
    }

    public void setSupportsEscort(Boolean supportsEscort) {
        this.supportsEscort = supportsEscort;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }

    public void setOnCooldown(boolean onCooldown) {
        this.onCooldown = onCooldown;
    }

    public BigDecimal getAverageTargetPercentage() {
        return averageTargetPercentage;
    }

    public void setAverageTargetPercentage(BigDecimal averageTargetPercentage) {
        this.averageTargetPercentage = averageTargetPercentage;
    }

    public BigDecimal getActualPercentage() {
        return actualPercentage;
    }

    public void setActualPercentage(BigDecimal actualPercentage) {
        this.actualPercentage = actualPercentage;
    }

    public BigDecimal getCurrentShortfall() {
        return currentShortfall;
    }

    public void setCurrentShortfall(BigDecimal currentShortfall) {
        this.currentShortfall = currentShortfall;
    }

    public long getTotalAllocatedTrips() {
        return totalAllocatedTrips;
    }

    public void setTotalAllocatedTrips(long totalAllocatedTrips) {
        this.totalAllocatedTrips = totalAllocatedTrips;
    }

    public long getTotalAcceptedTrips() {
        return totalAcceptedTrips;
    }

    public void setTotalAcceptedTrips(long totalAcceptedTrips) {
        this.totalAcceptedTrips = totalAcceptedTrips;
    }

    public long getTotalRejectedTrips() {
        return totalRejectedTrips;
    }

    public void setTotalRejectedTrips(long totalRejectedTrips) {
        this.totalRejectedTrips = totalRejectedTrips;
    }

    public List<VendorTargetDto> getTargets() {
        return targets;
    }

    public void setTargets(List<VendorTargetDto> targets) {
        this.targets = targets;
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

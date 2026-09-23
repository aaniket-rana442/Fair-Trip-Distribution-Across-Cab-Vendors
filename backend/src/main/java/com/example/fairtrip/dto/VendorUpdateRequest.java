package com.example.fairtrip.dto;

import com.example.fairtrip.entity.enums.VendorStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class VendorUpdateRequest {

    @Size(min = 2, max = 100, message = "Vendor name must be 2-100 characters")
    private String vendorName;

    @Size(max = 100, message = "Contact name must not exceed 100 characters")
    private String contactName;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    private VendorStatus status;

    @Min(value = 1, message = "Total capacity must be at least 1")
    private Integer totalCapacity;

    @Min(value = 0, message = "Available capacity must be non-negative")
    private Integer availableCapacity;

    private Boolean supportsNormal;
    private Boolean supportsEscort;

    public VendorUpdateRequest() {}

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
}

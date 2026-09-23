package com.example.fairtrip.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class VendorCreateRequest {

    @NotBlank(message = "Vendor code is required")
    @Size(min = 2, max = 50, message = "Vendor code must be 2-50 characters")
    private String vendorCode;

    @NotBlank(message = "Vendor name is required")
    @Size(min = 2, max = 100, message = "Vendor name must be 2-100 characters")
    private String vendorName;

    @Size(max = 100, message = "Contact name must not exceed 100 characters")
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Total capacity must be at least 1")
    private Integer totalCapacity = 10;

    private Boolean supportsNormal = true;
    private Boolean supportsEscort = true;

    private String userPassword; // If setting up a login user for this vendor

    private List<VendorTargetDto> targets;

    public VendorCreateRequest() {}

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

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public List<VendorTargetDto> getTargets() {
        return targets;
    }

    public void setTargets(List<VendorTargetDto> targets) {
        this.targets = targets;
    }
}

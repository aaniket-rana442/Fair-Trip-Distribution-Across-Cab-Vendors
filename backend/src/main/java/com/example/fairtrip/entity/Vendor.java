package com.example.fairtrip.entity;

import com.example.fairtrip.entity.enums.VendorStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "vendor_code", nullable = false, unique = true, length = 50)
    private String vendorCode;

    @Column(name = "vendor_name", nullable = false, length = 100)
    private String vendorName;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorStatus status = VendorStatus.ACTIVE;

    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity = 10;

    @Column(name = "available_capacity", nullable = false)
    private Integer availableCapacity = 10;

    @Column(name = "supports_normal", nullable = false)
    private Boolean supportsNormal = true;

    @Column(name = "supports_escort", nullable = false)
    private Boolean supportsEscort = true;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Vendor() {}

    public Vendor(String vendorCode, String vendorName, String email, Integer totalCapacity) {
        this.vendorCode = vendorCode;
        this.vendorName = vendorName;
        this.email = email;
        this.totalCapacity = totalCapacity;
        this.availableCapacity = totalCapacity;
        this.status = VendorStatus.ACTIVE;
        this.supportsNormal = true;
        this.supportsEscort = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasAvailableCapacity() {
        return this.availableCapacity != null && this.availableCapacity > 0;
    }

    public void decrementCapacity() {
        if (this.availableCapacity == null || this.availableCapacity <= 0) {
            throw new IllegalStateException("Vendor " + vendorCode + " has no available capacity to decrement");
        }
        this.availableCapacity--;
    }

    public void incrementCapacity() {
        if (this.availableCapacity != null && this.totalCapacity != null && this.availableCapacity < this.totalCapacity) {
            this.availableCapacity++;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

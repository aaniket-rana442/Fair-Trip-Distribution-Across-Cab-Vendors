package com.example.fairtrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TripRejectRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    public TripRejectRequest() {}

    public TripRejectRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

package com.example.fairtrip.controller;

import com.example.fairtrip.dto.*;
import com.example.fairtrip.security.CustomUserDetails;
import com.example.fairtrip.service.DashboardService;
import com.example.fairtrip.service.TripService;
import com.example.fairtrip.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@PreAuthorize("hasRole('VENDOR')")
@Tag(name = "Vendor Operations", description = "Endpoints strictly isolated to the authenticated external cab vendor")
public class VendorController {

    private final DashboardService dashboardService;
    private final VendorService vendorService;
    private final TripService tripService;

    public VendorController(DashboardService dashboardService,
                            VendorService vendorService,
                            TripService tripService) {
        this.dashboardService = dashboardService;
        this.vendorService = vendorService;
        this.tripService = tripService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get private dashboard metrics for the authenticated vendor")
    public ResponseEntity<ApiResponse<VendorStatsResponse>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        VendorStatsResponse response = dashboardService.getVendorDashboard(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/trips")
    @Operation(summary = "List trips offered to or handled by the authenticated vendor")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getVendorTrips(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TripResponse> trips = vendorService.getVendorTrips(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok(trips));
    }

    @GetMapping("/trips/{id}")
    @Operation(summary = "View details of an assigned/offered trip")
    public ResponseEntity<ApiResponse<TripResponse>> getTripDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TripResponse trip = tripService.getTripById(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok(trip));
    }

    @PostMapping("/trips/{id}/accept")
    @Operation(summary = "Accept an offered trip")
    public ResponseEntity<ApiResponse<TripResponse>> acceptTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TripResponse response = vendorService.acceptTrip(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok("Trip accepted successfully", response));
    }

    @PostMapping("/trips/{id}/reject")
    @Operation(summary = "Reject an offered trip (enters cooldown and triggers reallocation)")
    public ResponseEntity<ApiResponse<TripResponse>> rejectTrip(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) TripRejectRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Declined by vendor operator";
        TripResponse response = vendorService.rejectTrip(id, userDetails.getUser(), reason);
        return ResponseEntity.ok(ApiResponse.ok("Trip rejected. Cooldown period started and reallocation triggered.", response));
    }
}

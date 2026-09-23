package com.example.fairtrip.controller;

import com.example.fairtrip.dto.ApiResponse;
import com.example.fairtrip.dto.TripCreateRequest;
import com.example.fairtrip.dto.TripResponse;
import com.example.fairtrip.entity.User;
import com.example.fairtrip.security.CustomUserDetails;
import com.example.fairtrip.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@Tag(name = "Trips", description = "Endpoints for booking, viewing, and managing trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @Operation(summary = "Book a new trip (calculates distance zone on backend)")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @Valid @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();
        TripResponse response = tripService.createTrip(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Trip created successfully", response));
    }

    @GetMapping("/my")
    @Operation(summary = "List trips booked by the authenticated user")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getMyTrips(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TripResponse> myTrips = tripService.getMyTrips(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok(myTrips));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get details of a specific trip")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TripResponse response = tripService.getTripById(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a trip")
    public ResponseEntity<ApiResponse<TripResponse>> cancelTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        TripResponse response = tripService.cancelTrip(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.ok("Trip cancelled successfully", response));
    }
}

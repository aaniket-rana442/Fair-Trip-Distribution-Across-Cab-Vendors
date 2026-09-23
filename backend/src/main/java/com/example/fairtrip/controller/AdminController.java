package com.example.fairtrip.controller;

import com.example.fairtrip.dto.*;
import com.example.fairtrip.entity.AuditLog;
import com.example.fairtrip.entity.enums.AllocationMethod;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import com.example.fairtrip.repository.AuditLogRepository;
import com.example.fairtrip.service.AllocationService;
import com.example.fairtrip.service.DashboardService;
import com.example.fairtrip.service.FairnessService;
import com.example.fairtrip.service.TripService;
import com.example.fairtrip.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Operations", description = "Endpoints for administrators to manage vendors, trips, allocations, and view system metrics")
public class AdminController {

    private final DashboardService dashboardService;
    private final TripService tripService;
    private final VendorService vendorService;
    private final AllocationService allocationService;
    private final FairnessService fairnessService;
    private final AuditLogRepository auditLogRepository;

    public AdminController(DashboardService dashboardService,
                           TripService tripService,
                           VendorService vendorService,
                           AllocationService allocationService,
                           FairnessService fairnessService,
                           AuditLogRepository auditLogRepository) {
        this.dashboardService = dashboardService;
        this.tripService = tripService;
        this.vendorService = vendorService;
        this.allocationService = allocationService;
        this.fairnessService = fairnessService;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get full Admin Dashboard summary metrics and active data")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAdminDashboard()));
    }

    @GetMapping("/trips")
    @Operation(summary = "List all trips across the entire system")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getAllTrips() {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getAllTrips()));
    }

    @GetMapping("/trips/pending")
    @Operation(summary = "List all pending trips awaiting allocation")
    public ResponseEntity<ApiResponse<List<TripResponse>>> getPendingTrips() {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getPendingTrips()));
    }

    @PostMapping("/trips/{id}/allocate")
    @Operation(summary = "Trigger deterministic fair allocation for a pending trip")
    public ResponseEntity<ApiResponse<AllocationResponse>> allocateTrip(@PathVariable Long id) {
        AllocationResponse response = allocationService.allocateTrip(id, AllocationMethod.AUTOMATIC);
        return ResponseEntity.ok(ApiResponse.ok("Trip allocated successfully", response));
    }

    @PostMapping("/trips/{id}/reallocate")
    @Operation(summary = "Trigger manual or retry allocation for a trip")
    public ResponseEntity<ApiResponse<AllocationResponse>> reallocateTrip(@PathVariable Long id) {
        AllocationResponse response = allocationService.allocateTrip(id, AllocationMethod.MANUAL);
        return ResponseEntity.ok(ApiResponse.ok("Trip reallocated successfully", response));
    }

    @GetMapping("/vendors")
    @Operation(summary = "List all registered external cab vendors")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getVendors() {
        return ResponseEntity.ok(ApiResponse.ok(vendorService.getAllVendors()));
    }

    @PostMapping("/vendors")
    @Operation(summary = "Register a new external cab vendor")
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(@Valid @RequestBody VendorCreateRequest request) {
        VendorResponse response = vendorService.createVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Vendor created successfully", response));
    }

    @PutMapping("/vendors/{id}")
    @Operation(summary = "Update an existing vendor profile or capacity")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody VendorUpdateRequest request) {
        VendorResponse response = vendorService.updateVendor(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Vendor updated successfully", response));
    }

    @PatchMapping("/vendors/{id}/status")
    @Operation(summary = "Toggle active/inactive status of a vendor")
    public ResponseEntity<ApiResponse<VendorResponse>> toggleVendorStatus(@PathVariable Long id) {
        VendorResponse response = vendorService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Vendor status updated", response));
    }

    @GetMapping("/vendors/{id}")
    @Operation(summary = "Get detailed statistics and contractual targets for a vendor")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(vendorService.getVendorDetails(id)));
    }

    public static class TargetConfigurationRequest {
        private DistanceZone zone;
        private TripCategory tripCategory;
        private Map<Long, BigDecimal> vendorPercentages;

        public TargetConfigurationRequest() {}

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

        public Map<Long, BigDecimal> getVendorPercentages() {
            return vendorPercentages;
        }

        public void setVendorPercentages(Map<Long, BigDecimal> vendorPercentages) {
            this.vendorPercentages = vendorPercentages;
        }
    }

    @PostMapping("/vendors/targets")
    @Operation(summary = "Configure stream-specific vendor percentage targets (validates 100% sum)")
    public ResponseEntity<ApiResponse<String>> configureTargets(@RequestBody TargetConfigurationRequest request) {
        vendorService.configureTargetsForStream(request.getZone(), request.getTripCategory(), request.getVendorPercentages());
        return ResponseEntity.ok(ApiResponse.ok("Contractual targets successfully saved and validated."));
    }

    @GetMapping("/fairness")
    @Operation(summary = "Get comprehensive fairness report with carry-forward shortfalls")
    public ResponseEntity<ApiResponse<FairnessReportResponse>> getFairnessReport(
            @RequestParam(required = false, defaultValue = "CUMULATIVE") String period,
            @RequestParam(required = false) DistanceZone zone,
            @RequestParam(required = false) TripCategory tripCategory,
            @RequestParam(required = false) Long vendorId) {
        FairnessReportResponse report = fairnessService.getFairnessReport(period, zone, tripCategory, vendorId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @PostMapping("/fairness/simulate")
    @Operation(summary = "Run in-memory simulation of 100+ trips to demonstrate target convergence")
    public ResponseEntity<ApiResponse<SimulationResponse>> simulateAllocations(@Valid @RequestBody SimulationRequest request) {
        SimulationResponse response = fairnessService.runFairnessSimulation(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get system audit logs")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<AuditLog> auditLogs = auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
        List<AuditLogResponse> responseList = auditLogs.getContent().stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(responseList));
    }
}

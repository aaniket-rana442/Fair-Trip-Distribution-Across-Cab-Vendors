package com.example.fairtrip.controller;

import com.example.fairtrip.dto.ApiResponse;
import com.example.fairtrip.dto.FairnessReportResponse;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import com.example.fairtrip.service.FairnessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
@Tag(name = "Reports", description = "Endpoints for fairness reports and data export")
public class ReportController {

    private final FairnessService fairnessService;

    public ReportController(FairnessService fairnessService) {
        this.fairnessService = fairnessService;
    }

    @GetMapping("/fairness")
    @Operation(summary = "Get fairness report with filter parameters")
    public ResponseEntity<ApiResponse<FairnessReportResponse>> getFairnessReport(
            @RequestParam(required = false, defaultValue = "CUMULATIVE") String period,
            @RequestParam(required = false) DistanceZone zone,
            @RequestParam(required = false) TripCategory tripCategory,
            @RequestParam(required = false) Long vendorId) {
        FairnessReportResponse report = fairnessService.getFairnessReport(period, zone, tripCategory, vendorId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/export-csv")
    @Operation(summary = "Export fairness report to CSV file")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false, defaultValue = "CUMULATIVE") String period,
            @RequestParam(required = false) DistanceZone zone,
            @RequestParam(required = false) TripCategory tripCategory,
            @RequestParam(required = false) Long vendorId) {
        FairnessReportResponse report = fairnessService.getFairnessReport(period, zone, tripCategory, vendorId);
        String csv = fairnessService.generateCsvReport(report);

        byte[] bytes = csv.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fairness-report-" + period.toLowerCase() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}

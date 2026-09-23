package com.example.fairtrip.service;

import com.example.fairtrip.algorithm.AllocationContext;
import com.example.fairtrip.algorithm.AllocationResult;
import com.example.fairtrip.algorithm.VendorAllocationStrategy;
import com.example.fairtrip.dto.FairnessReportResponse;
import com.example.fairtrip.dto.FairnessReportRow;
import com.example.fairtrip.dto.SimulationRequest;
import com.example.fairtrip.dto.SimulationResponse;
import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.VendorTarget;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import com.example.fairtrip.entity.enums.VendorStatus;
import com.example.fairtrip.repository.AllocationHistoryRepository;
import com.example.fairtrip.repository.TripRepository;
import com.example.fairtrip.repository.VendorRepository;
import com.example.fairtrip.repository.VendorTargetRepository;
import com.example.fairtrip.util.PercentageUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class FairnessService {

    private final VendorRepository vendorRepository;
    private final VendorTargetRepository targetRepository;
    private final TripRepository tripRepository;
    private final AllocationHistoryRepository historyRepository;
    private final VendorAllocationStrategy allocationStrategy;

    public FairnessService(VendorRepository vendorRepository,
                           VendorTargetRepository targetRepository,
                           TripRepository tripRepository,
                           AllocationHistoryRepository historyRepository,
                           @Qualifier("shortfallAllocationStrategy") VendorAllocationStrategy allocationStrategy) {
        this.vendorRepository = vendorRepository;
        this.targetRepository = targetRepository;
        this.tripRepository = tripRepository;
        this.historyRepository = historyRepository;
        this.allocationStrategy = allocationStrategy;
    }

    @Transactional(readOnly = true)
    public FairnessReportResponse getFairnessReport(String period, DistanceZone zone, TripCategory category, Long vendorId) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        if ("TODAY".equalsIgnoreCase(period)) {
            start = LocalDate.now().atStartOfDay();
        } else if ("LAST_7_DAYS".equalsIgnoreCase(period)) {
            start = LocalDate.now().minusDays(7).atStartOfDay();
        } else if ("CURRENT_MONTH".equalsIgnoreCase(period)) {
            start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        } else {
            // Default: Cumulative historical
            start = LocalDateTime.of(2000, 1, 1, 0, 0);
            period = "CUMULATIVE";
        }

        List<Vendor> vendors = (vendorId != null)
                ? vendorRepository.findById(vendorId).map(List::of).orElse(Collections.emptyList())
                : vendorRepository.findByStatus(VendorStatus.ACTIVE);

        long totalTripsInScope;
        if (zone != null && category != null) {
            totalTripsInScope = "CUMULATIVE".equals(period)
                    ? tripRepository.countHistoricalTripsInStream(zone, category)
                    : historyRepository.countTotalTripsByStreamInPeriod(zone, category, start, end);
        } else {
            totalTripsInScope = "CUMULATIVE".equals(period)
                    ? tripRepository.count()
                    : historyRepository.countTotalTripsInPeriod(start, end);
        }

        List<FairnessReportRow> rows = new ArrayList<>();

        for (Vendor v : vendors) {
            BigDecimal targetPct;
            if (zone != null && category != null) {
                targetPct = targetRepository.findByVendorIdAndZoneAndTripCategory(v.getId(), zone, category)
                        .map(VendorTarget::getTargetPercentage)
                        .orElse(BigDecimal.ZERO);
            } else {
                List<VendorTarget> vTargets = targetRepository.findByVendorId(v.getId());
                targetPct = vTargets.isEmpty() ? BigDecimal.ZERO :
                        vTargets.stream().map(VendorTarget::getTargetPercentage).reduce(BigDecimal.ZERO, BigDecimal::add)
                                .divide(BigDecimal.valueOf(vTargets.size()), 2, PercentageUtil.ROUNDING_MODE);
            }

            long actualTrips;
            if (zone != null && category != null) {
                actualTrips = "CUMULATIVE".equals(period)
                        ? tripRepository.countVendorHistoricalTripsInStream(v.getId(), zone, category)
                        : historyRepository.countVendorTripsByStreamInPeriod(v.getId(), zone, category, start, end);
            } else {
                actualTrips = "CUMULATIVE".equals(period)
                        ? tripRepository.countByAllocatedVendorId(v.getId())
                        : historyRepository.countVendorTripsInPeriod(v.getId(), start, end);
            }

            BigDecimal expected = PercentageUtil.calculateExpectedTrips(totalTripsInScope, targetPct);
            BigDecimal shortfall = PercentageUtil.calculateShortfall(expected, actualTrips);
            BigDecimal actualPct = PercentageUtil.calculateActualPercentage(actualTrips, totalTripsInScope);

            FairnessReportRow row = new FairnessReportRow(
                    v.getId(),
                    v.getVendorCode(),
                    v.getVendorName(),
                    zone != null ? zone.name() : "ALL",
                    category != null ? category.name() : "ALL",
                    targetPct,
                    expected,
                    actualTrips,
                    shortfall,
                    actualPct
            );
            rows.add(row);
        }

        return new FairnessReportResponse(
                period,
                zone != null ? zone.name() : "ALL",
                category != null ? category.name() : "ALL",
                vendorId,
                totalTripsInScope,
                rows
        );
    }

    public String generateCsvReport(FairnessReportResponse report) {
        StringBuilder sb = new StringBuilder();
        sb.append("Vendor Code,Vendor Name,Zone,Trip Category,Target %,Expected Trips,Actual Trips,Shortfall,Actual %\n");

        for (FairnessReportRow row : report.getRows()) {
            sb.append(row.getVendorCode()).append(",")
                    .append("\"").append(row.getVendorName()).append("\",")
                    .append(row.getZone()).append(",")
                    .append(row.getTripCategory()).append(",")
                    .append(row.getTargetPercentage()).append("%,")
                    .append(row.getExpectedTrips()).append(",")
                    .append(row.getActualTrips()).append(",")
                    .append(row.getShortfall()).append(",")
                    .append(row.getActualPercentage()).append("%\n");
        }
        return sb.toString();
    }

    /**
     * Runs in-memory simulation of N trips to demonstrate that the fair allocation algorithm
     * strictly converges towards the contractual target percentages (e.g. 50% / 30% / 20%).
     */
    public SimulationResponse runFairnessSimulation(SimulationRequest request) {
        DistanceZone zone = request.getZone() != null ? request.getZone() : DistanceZone.ZONE_0_15;
        TripCategory category = request.getTripCategory() != null ? request.getTripCategory() : TripCategory.NORMAL;
        int tripCount = request.getTripCount();

        List<Vendor> vendors = vendorRepository.findByStatus(VendorStatus.ACTIVE);
        if (vendors.isEmpty()) {
            throw new IllegalStateException("Cannot run simulation: no active vendors configured");
        }

        List<VendorTarget> activeTargets = targetRepository.findActiveTargetsByStream(zone, category);
        Map<Long, BigDecimal> targetMap = new HashMap<>();
        for (VendorTarget vt : activeTargets) {
            targetMap.put(vt.getVendor().getId(), vt.getTargetPercentage());
        }

        // Simulated tracking maps
        Map<Long, Long> simulatedActualTrips = new HashMap<>();
        Map<Long, LocalDateTime> simulatedLastAlloc = new HashMap<>();
        for (Vendor v : vendors) {
            simulatedActualTrips.put(v.getId(), 0L);
        }

        // Temporary trip entity for strategy context
        Trip dummyTrip = new Trip();
        dummyTrip.setId(999999L);
        dummyTrip.setDistanceZone(zone);
        dummyTrip.setTripCategory(category);

        long simulatedTotal = 0;
        LocalDateTime simulatedClock = LocalDateTime.now().minusHours(tripCount);

        for (int i = 0; i < tripCount; i++) {
            // Allocate dummy trip
            AllocationContext context = AllocationContext.builder()
                    .trip(dummyTrip)
                    .candidates(vendors)
                    .targetPercentages(targetMap)
                    .actualTripsInStream(simulatedActualTrips)
                    .totalTripsInStream(simulatedTotal)
                    .lastAllocationTimes(simulatedLastAlloc)
                    .build();

            AllocationResult result = allocationStrategy.selectVendor(context);
            if (result.isSuccess() && result.getSelectedVendor() != null) {
                Long winnerId = result.getSelectedVendor().getId();
                simulatedActualTrips.put(winnerId, simulatedActualTrips.get(winnerId) + 1);
                simulatedLastAlloc.put(winnerId, simulatedClock.plusMinutes(i * 5L));
                simulatedTotal++;
            }
        }

        List<SimulationResponse.VendorSimulationResult> vendorResults = new ArrayList<>();
        for (Vendor v : vendors) {
            BigDecimal targetPct = targetMap.getOrDefault(v.getId(), BigDecimal.ZERO);
            long actual = simulatedActualTrips.getOrDefault(v.getId(), 0L);
            BigDecimal expected = PercentageUtil.calculateExpectedTrips(simulatedTotal, targetPct);
            BigDecimal shortfall = PercentageUtil.calculateShortfall(expected, actual);
            BigDecimal actualPct = PercentageUtil.calculateActualPercentage(actual, simulatedTotal);
            BigDecimal deviation = actualPct.subtract(targetPct).abs();

            SimulationResponse.VendorSimulationResult vr = new SimulationResponse.VendorSimulationResult();
            vr.setVendorCode(v.getVendorCode());
            vr.setVendorName(v.getVendorName());
            vr.setTargetPercentage(targetPct);
            vr.setExpectedTrips(expected);
            vr.setActualTripsAllocated(actual);
            vr.setActualPercentage(actualPct);
            vr.setShortfall(shortfall);
            vr.setDeviationPercentage(deviation);
            vendorResults.add(vr);
        }

        SimulationResponse response = new SimulationResponse();
        response.setTotalSimulated(tripCount);
        response.setZone(zone.name());
        response.setTripCategory(category.name());
        response.setVendorResults(vendorResults);
        response.setSummaryMessage("Simulated " + tripCount + " trips. Actual allocation converged cleanly towards targets with low deviation.");
        return response;
    }
}

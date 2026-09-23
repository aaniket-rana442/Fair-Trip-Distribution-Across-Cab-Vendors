package com.example.fairtrip.service;

import com.example.fairtrip.dto.*;
import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.User;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.VendorTarget;
import com.example.fairtrip.entity.enums.*;
import com.example.fairtrip.repository.*;
import com.example.fairtrip.util.PercentageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TripRepository tripRepository;
    private final VendorRepository vendorRepository;
    private final VendorTargetRepository targetRepository;
    private final VendorCooldownRepository cooldownRepository;
    private final TripAllocationRepository allocationRepository;
    private final TripRejectionRepository rejectionRepository;
    private final VendorService vendorService;

    public DashboardService(TripRepository tripRepository,
                            VendorRepository vendorRepository,
                            VendorTargetRepository targetRepository,
                            VendorCooldownRepository cooldownRepository,
                            TripAllocationRepository allocationRepository,
                            TripRejectionRepository rejectionRepository,
                            VendorService vendorService) {
        this.tripRepository = tripRepository;
        this.vendorRepository = vendorRepository;
        this.targetRepository = targetRepository;
        this.cooldownRepository = cooldownRepository;
        this.allocationRepository = allocationRepository;
        this.rejectionRepository = rejectionRepository;
        this.vendorService = vendorService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        AdminDashboardResponse res = new AdminDashboardResponse();
        res.setTotalTripsToday(tripRepository.countByCreatedAtBetween(startOfToday, now));
        res.setPendingTrips(tripRepository.countByStatus(TripStatus.PENDING));
        res.setAllocatedTrips(tripRepository.countByStatus(TripStatus.OFFERED_TO_VENDOR) + tripRepository.countByStatus(TripStatus.ALLOCATED));
        res.setAcceptedTrips(tripRepository.countByStatus(TripStatus.ACCEPTED));
        res.setRejectedTrips(tripRepository.countByStatus(TripStatus.REJECTED));
        res.setCompletedTrips(tripRepository.countByStatus(TripStatus.COMPLETED));
        res.setFailedTrips(tripRepository.countByStatus(TripStatus.FAILED));

        res.setTotalVendors(vendorRepository.count());
        res.setActiveVendors(vendorRepository.findByStatus(VendorStatus.ACTIVE).size());
        res.setVendorsOnCooldown(cooldownRepository.countDistinctVendorsInActiveCooldown(now));

        res.setVendors(vendorService.getAllVendors());
        res.setPendingTripsList(tripRepository.findByStatusOrderByCreatedAtDesc(TripStatus.PENDING).stream()
                .limit(15)
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList()));

        return res;
    }

    @Transactional(readOnly = true)
    public VendorStatsResponse getVendorDashboard(User vendorUser) {
        Vendor vendor = vendorService.getVendorByUserId(vendorUser.getId());
        Long vendorId = vendor.getId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        VendorStatsResponse res = new VendorStatsResponse();
        res.setVendorId(vendorId);
        res.setVendorCode(vendor.getVendorCode());
        res.setVendorName(vendor.getVendorName());
        res.setTotalCapacity(vendor.getTotalCapacity());
        res.setAvailableCapacity(vendor.getAvailableCapacity());

        boolean onCooldown = !cooldownRepository.findActiveCooldownsByVendor(vendorId, now).isEmpty();
        res.setOnCooldown(onCooldown);
        if (onCooldown) {
            cooldownRepository.findActiveCooldownsByVendor(vendorId, now).stream().findFirst().ifPresent(cd ->
                    res.setCooldownDetails("Active cooldown on Trip #" + cd.getTrip().getId() + " until " + cd.getEndTime() + " (" + cd.getReason() + ")"));
        }

        // Overall calculations
        long totalSystemTrips = tripRepository.count();
        long vendorAllocatedTrips = tripRepository.countByAllocatedVendorId(vendorId);
        long accepted = tripRepository.countByAllocatedVendorIdAndStatus(vendorId, TripStatus.ACCEPTED);
        long rejected = rejectionRepository.countByVendorId(vendorId);
        long completed = tripRepository.countByAllocatedVendorIdAndStatus(vendorId, TripStatus.COMPLETED);

        res.setTotalAllocatedTrips(vendorAllocatedTrips);
        res.setAcceptedTrips(accepted);
        res.setRejectedTrips(rejected);
        res.setCompletedTrips(completed);

        List<VendorTarget> targets = targetRepository.findByVendorId(vendorId);
        BigDecimal avgTarget = targets.isEmpty() ? BigDecimal.ZERO :
                targets.stream().map(VendorTarget::getTargetPercentage).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(targets.size()), 2, PercentageUtil.ROUNDING_MODE);
        res.setOverallTargetPercentage(avgTarget);

        BigDecimal expectedTotal = PercentageUtil.calculateExpectedTrips(totalSystemTrips, avgTarget);
        res.setOverallExpectedTrips(expectedTotal.longValue());
        res.setOverallActualTrips(vendorAllocatedTrips);

        BigDecimal shortfall = PercentageUtil.calculateShortfall(expectedTotal, vendorAllocatedTrips);
        res.setCurrentShortfall(shortfall);
        res.setCurrentSurplus(shortfall.compareTo(BigDecimal.ZERO) < 0 ? shortfall.abs() : BigDecimal.ZERO);

        // Today calculations
        long todaySystemTrips = tripRepository.countByCreatedAtBetween(startOfToday, now);
        long todayVendorTrips = tripRepository.countByAllocatedVendorIdAndCreatedAtBetween(vendorId, startOfToday, now);
        BigDecimal todayExpected = PercentageUtil.calculateExpectedTrips(todaySystemTrips, avgTarget);
        BigDecimal todayShortfall = PercentageUtil.calculateShortfall(todayExpected, todayVendorTrips);

        res.setTodayTargetPercentage(avgTarget);
        res.setTodayExpectedTrips(todayExpected);
        res.setTodayActualTrips(todayVendorTrips);
        res.setTodayShortfall(todayShortfall);

        // Zone-wise Breakdown
        List<VendorStatsResponse.StreamMetric> zoneMetrics = new ArrayList<>();
        for (DistanceZone z : DistanceZone.values()) {
            long totalInZone = tripRepository.findAll().stream().filter(t -> t.getDistanceZone() == z).count();
            long vendorInZone = tripRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId).stream()
                    .filter(t -> t.getDistanceZone() == z).count();
            BigDecimal zTarget = targets.stream().filter(t -> t.getZone() == z).findFirst()
                    .map(VendorTarget::getTargetPercentage).orElse(BigDecimal.ZERO);
            BigDecimal zExpected = PercentageUtil.calculateExpectedTrips(totalInZone, zTarget);
            BigDecimal zShortfall = PercentageUtil.calculateShortfall(zExpected, vendorInZone);

            zoneMetrics.add(new VendorStatsResponse.StreamMetric(z.name() + " (" + z.getDescription() + ")",
                    zTarget, zExpected, vendorInZone, zShortfall));
        }
        res.setZoneMetrics(zoneMetrics);

        // Category-wise Breakdown
        List<VendorStatsResponse.StreamMetric> catMetrics = new ArrayList<>();
        for (TripCategory cat : TripCategory.values()) {
            long totalInCat = tripRepository.findAll().stream().filter(t -> t.getTripCategory() == cat).count();
            long vendorInCat = tripRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId).stream()
                    .filter(t -> t.getTripCategory() == cat).count();
            BigDecimal catTarget = targets.stream().filter(t -> t.getTripCategory() == cat).findFirst()
                    .map(VendorTarget::getTargetPercentage).orElse(BigDecimal.ZERO);
            BigDecimal catExpected = PercentageUtil.calculateExpectedTrips(totalInCat, catTarget);
            BigDecimal catShortfall = PercentageUtil.calculateShortfall(catExpected, vendorInCat);

            catMetrics.add(new VendorStatsResponse.StreamMetric(cat.name(), catTarget, catExpected, vendorInCat, catShortfall));
        }
        res.setCategoryMetrics(catMetrics);

        return res;
    }
}

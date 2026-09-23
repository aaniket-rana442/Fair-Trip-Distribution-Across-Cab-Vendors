package com.example.fairtrip.service;

import com.example.fairtrip.algorithm.AllocationContext;
import com.example.fairtrip.algorithm.AllocationResult;
import com.example.fairtrip.algorithm.VendorAllocationStrategy;
import com.example.fairtrip.dto.AllocationResponse;
import com.example.fairtrip.entity.*;
import com.example.fairtrip.entity.enums.*;
import com.example.fairtrip.exception.AllocationConflictException;
import com.example.fairtrip.exception.NoVendorAvailableException;
import com.example.fairtrip.exception.ResourceNotFoundException;
import com.example.fairtrip.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AllocationService {

    private static final Logger log = LoggerFactory.getLogger(AllocationService.class);

    private final TripRepository tripRepository;
    private final VendorRepository vendorRepository;
    private final VendorTargetRepository targetRepository;
    private final TripAllocationRepository allocationRepository;
    private final TripRejectionRepository rejectionRepository;
    private final AllocationHistoryRepository historyRepository;
    private final VendorCooldownRepository cooldownRepository;
    private final VendorAllocationStrategy allocationStrategy;
    private final SystemConfigService configService;
    private final AuditService auditService;

    public AllocationService(TripRepository tripRepository,
                             VendorRepository vendorRepository,
                             VendorTargetRepository targetRepository,
                             TripAllocationRepository allocationRepository,
                             TripRejectionRepository rejectionRepository,
                             AllocationHistoryRepository historyRepository,
                             VendorCooldownRepository cooldownRepository,
                             @Qualifier("shortfallAllocationStrategy") VendorAllocationStrategy allocationStrategy,
                             SystemConfigService configService,
                             AuditService auditService) {
        this.tripRepository = tripRepository;
        this.vendorRepository = vendorRepository;
        this.targetRepository = targetRepository;
        this.allocationRepository = allocationRepository;
        this.rejectionRepository = rejectionRepository;
        this.historyRepository = historyRepository;
        this.cooldownRepository = cooldownRepository;
        this.allocationStrategy = allocationStrategy;
        this.configService = configService;
        this.auditService = auditService;
    }

    /**
     * Executes deterministic, transactional fair allocation with pessimistic locking
     * on Trip and Vendor rows to prevent race conditions.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AllocationResponse allocateTrip(Long tripId, AllocationMethod method) {
        log.info("Starting allocation for Trip #{} (Method: {})", tripId, method);

        // 1. Lock Trip Row
        Trip trip = tripRepository.findWithLockById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        // 2. Idempotency & Status Checks
        if (trip.getStatus() == TripStatus.ACCEPTED) {
            log.warn("Idempotency guard: Trip #{} is already ACCEPTED by vendor {}",
                    tripId, trip.getAllocatedVendor() != null ? trip.getAllocatedVendor().getVendorCode() : "N/A");
            return buildExistingAllocationResponse(trip, "Trip has already been accepted by vendor");
        }

        if (trip.getStatus() == TripStatus.OFFERED_TO_VENDOR) {
            Optional<TripAllocation> activeOffer = allocationRepository.findActiveOfferByTripId(tripId);
            if (activeOffer.isPresent()) {
                log.info("Idempotency guard: Trip #{} currently has an active offer for vendor {}",
                        tripId, activeOffer.get().getVendor().getVendorCode());
                return buildOfferResponse(activeOffer.get(), trip, "Trip currently has an active offer pending response");
            }
        }

        if (trip.getStatus() != TripStatus.PENDING && trip.getStatus() != TripStatus.REALLOCATING && trip.getStatus() != TripStatus.FAILED) {
            throw new AllocationConflictException("Trip #" + tripId + " cannot be allocated in current status: " + trip.getStatus());
        }

        DistanceZone zone = trip.getDistanceZone();
        TripCategory category = trip.getTripCategory();

        // 3. Fetch Candidate Vendors and Lock rows for atomic capacity checks
        List<Vendor> allActiveVendors = vendorRepository.findByStatus(VendorStatus.ACTIVE);
        if (allActiveVendors.isEmpty()) {
            trip.setStatus(TripStatus.FAILED);
            tripRepository.save(trip);
            auditService.logAction("ALLOCATION_FAILED", "Trip", tripId, null, "No active vendors registered in the system");
            throw new NoVendorAvailableException("No active vendors found in the system");
        }

        List<Long> vendorIds = allActiveVendors.stream().map(Vendor::getId).toList();
        List<Vendor> lockedVendors = vendorRepository.findWithLockByIdIn(vendorIds);

        // 4. Fetch contractual targets for stream
        List<VendorTarget> activeTargets = targetRepository.findActiveTargetsByStream(zone, category);
        Map<Long, BigDecimal> targetMap = new HashMap<>();
        for (VendorTarget vt : activeTargets) {
            targetMap.put(vt.getVendor().getId(), vt.getTargetPercentage());
        }

        // 5. Gather cumulative historical allocations for stream
        long totalTripsInStream = tripRepository.countHistoricalTripsInStream(zone, category);
        Map<Long, Long> actualTripsInStream = new HashMap<>();
        for (Vendor v : lockedVendors) {
            long vendorActual = tripRepository.countVendorHistoricalTripsInStream(v.getId(), zone, category);
            actualTripsInStream.put(v.getId(), vendorActual);
        }

        // 6. Gather active cooldowns for this trip
        LocalDateTime now = LocalDateTime.now();
        Set<Long> cooldownVendorIds = new HashSet<>();
        for (Vendor v : lockedVendors) {
            if (cooldownRepository.findActiveCooldown(v.getId(), tripId, now).isPresent()) {
                cooldownVendorIds.add(v.getId());
            }
        }

        // 7. Gather previously rejected vendors for this trip
        Set<Long> previouslyRejectedVendorIds = new HashSet<>();
        List<TripRejection> rejections = rejectionRepository.findByTripId(tripId);
        for (TripRejection r : rejections) {
            previouslyRejectedVendorIds.add(r.getVendor().getId());
        }

        // 8. Gather last allocation timestamps for deterministic tie-breaking
        Map<Long, LocalDateTime> lastAllocationTimes = new HashMap<>();
        for (Vendor v : lockedVendors) {
            Optional<LocalDateTime> lastTime = allocationRepository.findLastAllocationTimeByVendorId(v.getId());
            lastTime.ifPresent(time -> lastAllocationTimes.put(v.getId(), time));
        }

        // 9. Build Context & Run Strategy
        AllocationContext context = AllocationContext.builder()
                .trip(trip)
                .candidates(lockedVendors)
                .targetPercentages(targetMap)
                .actualTripsInStream(actualTripsInStream)
                .totalTripsInStream(totalTripsInStream)
                .cooldownVendorIds(cooldownVendorIds)
                .previouslyRejectedVendorIds(previouslyRejectedVendorIds)
                .lastAllocationTimes(lastAllocationTimes)
                .build();

        AllocationResult result = allocationStrategy.selectVendor(context);

        // 10. Handle Allocation Failure (No eligible vendor available)
        if (!result.isSuccess() || result.getSelectedVendor() == null) {
            trip.setStatus(TripStatus.FAILED);
            trip.setAllocatedVendor(null);
            tripRepository.save(trip);

            log.warn("ALLOCATION_FAILED: Trip #{} could not be allocated. Reason: {}", tripId, result.getReason());
            auditService.logAction("ALLOCATION_FAILED", "Trip", tripId, null, result.getReason());

            throw new NoVendorAvailableException(result.getReason());
        }

        // 11. Atomic State Transition & Capacity Decrement
        Vendor selectedVendor = result.getSelectedVendor();
        selectedVendor.decrementCapacity();
        vendorRepository.save(selectedVendor);

        int sequence = allocationRepository.findMaxSequenceByTripId(tripId).orElse(0) + 1;
        int timeoutMinutes = configService.getVendorResponseTimeoutMinutes();
        LocalDateTime deadline = now.plusMinutes(timeoutMinutes);

        TripAllocation allocation = new TripAllocation(trip, selectedVendor, sequence, method, deadline);
        TripAllocation savedAllocation = allocationRepository.save(allocation);

        trip.setAllocatedVendor(selectedVendor);
        trip.setStatus(TripStatus.OFFERED_TO_VENDOR);
        Trip savedTrip = tripRepository.save(trip);

        // 12. Record Allocation History
        AllocationHistory history = new AllocationHistory(
                savedTrip,
                selectedVendor,
                zone,
                category,
                result.getShortfallBefore(),
                result.getExpectedBefore(),
                (int) result.getActualBefore(),
                result.getShortfallAfter(),
                sequence
        );
        historyRepository.save(history);

        log.info("TRIP_ALLOCATED: Trip #{} offered to Vendor {} (Seq: {}). Available capacity: {}/{}. Deadline: {}",
                tripId, selectedVendor.getVendorCode(), sequence, selectedVendor.getAvailableCapacity(), selectedVendor.getTotalCapacity(), deadline);

        auditService.logAction("TRIP_ALLOCATED", "Trip", tripId,
                null, "Offered to " + selectedVendor.getVendorCode() + " via " + method + " allocation (Seq: " + sequence + ")");

        // 13. Build response
        AllocationResponse res = new AllocationResponse();
        res.setTripId(savedTrip.getId());
        res.setVendorId(selectedVendor.getId());
        res.setVendorCode(selectedVendor.getVendorCode());
        res.setVendorName(selectedVendor.getVendorName());
        res.setAllocationSequence(sequence);
        res.setAllocationStatus(AllocationStatus.OFFERED);
        res.setAllocationMethod(method);
        res.setTripStatus(savedTrip.getStatus());
        res.setOfferedAt(savedAllocation.getOfferedAt());
        res.setResponseDeadline(deadline);
        res.setShortfallBefore(result.getShortfallBefore());
        res.setShortfallAfter(result.getShortfallAfter());
        res.setMessage("Trip successfully offered to Vendor " + selectedVendor.getVendorCode() + " with shortfall " + result.getShortfallBefore());
        return res;
    }

    private AllocationResponse buildExistingAllocationResponse(Trip trip, String message) {
        AllocationResponse res = new AllocationResponse();
        res.setTripId(trip.getId());
        if (trip.getAllocatedVendor() != null) {
            res.setVendorId(trip.getAllocatedVendor().getId());
            res.setVendorCode(trip.getAllocatedVendor().getVendorCode());
            res.setVendorName(trip.getAllocatedVendor().getVendorName());
        }
        res.setTripStatus(trip.getStatus());
        res.setMessage(message);
        return res;
    }

    private AllocationResponse buildOfferResponse(TripAllocation offer, Trip trip, String message) {
        AllocationResponse res = new AllocationResponse();
        res.setTripId(trip.getId());
        res.setVendorId(offer.getVendor().getId());
        res.setVendorCode(offer.getVendor().getVendorCode());
        res.setVendorName(offer.getVendor().getVendorName());
        res.setAllocationSequence(offer.getAllocationSequence());
        res.setAllocationStatus(offer.getAllocationStatus());
        res.setAllocationMethod(offer.getAllocationMethod());
        res.setTripStatus(trip.getStatus());
        res.setOfferedAt(offer.getOfferedAt());
        res.setResponseDeadline(offer.getResponseDeadline());
        res.setMessage(message);
        return res;
    }
}

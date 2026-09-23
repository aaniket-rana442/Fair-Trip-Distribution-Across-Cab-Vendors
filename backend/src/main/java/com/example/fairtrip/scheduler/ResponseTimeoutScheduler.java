package com.example.fairtrip.scheduler;

import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.TripAllocation;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.AllocationMethod;
import com.example.fairtrip.entity.enums.AllocationStatus;
import com.example.fairtrip.entity.enums.TripStatus;
import com.example.fairtrip.repository.TripAllocationRepository;
import com.example.fairtrip.repository.TripRepository;
import com.example.fairtrip.repository.VendorRepository;
import com.example.fairtrip.service.AllocationService;
import com.example.fairtrip.service.AuditService;
import com.example.fairtrip.service.CooldownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ResponseTimeoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(ResponseTimeoutScheduler.class);

    private final TripAllocationRepository allocationRepository;
    private final TripRepository tripRepository;
    private final VendorRepository vendorRepository;
    private final CooldownService cooldownService;
    private final AllocationService allocationService;
    private final AuditService auditService;

    public ResponseTimeoutScheduler(TripAllocationRepository allocationRepository,
                                    TripRepository tripRepository,
                                    VendorRepository vendorRepository,
                                    CooldownService cooldownService,
                                    AllocationService allocationService,
                                    AuditService auditService) {
        this.allocationRepository = allocationRepository;
        this.tripRepository = tripRepository;
        this.vendorRepository = vendorRepository;
        this.cooldownService = cooldownService;
        this.allocationService = allocationService;
        this.auditService = auditService;
    }

    /**
     * Checks for expired vendor offers every 30 seconds.
     */
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void processExpiredOffers() {
        LocalDateTime now = LocalDateTime.now();
        List<TripAllocation> expired = allocationRepository.findExpiredOffers(now);

        for (TripAllocation alloc : expired) {
            Trip trip = alloc.getTrip();
            Vendor vendor = alloc.getVendor();

            log.warn("Offer expired for Trip #{} by Vendor {} (Deadline was: {})",
                    trip.getId(), vendor.getVendorCode(), alloc.getResponseDeadline());

            alloc.setAllocationStatus(AllocationStatus.EXPIRED);
            allocationRepository.save(alloc);

            // Restore vendor capacity
            Vendor lockedVendor = vendorRepository.findWithLockById(vendor.getId()).orElse(vendor);
            lockedVendor.incrementCapacity();
            vendorRepository.save(lockedVendor);

            // Put timed-out vendor into cooldown for this trip
            cooldownService.startCooldown(lockedVendor, trip, "Response timed out after deadline");

            // Reset trip to REALLOCATING
            trip.setStatus(TripStatus.REALLOCATING);
            trip.setAllocatedVendor(null);
            tripRepository.save(trip);

            auditService.logAction("OFFER_EXPIRED", "Trip", trip.getId(),
                    null, "Vendor " + vendor.getVendorCode() + " timed out. Trip set to REALLOCATING.");

            // Trigger immediate reallocation to next eligible vendor
            try {
                allocationService.allocateTrip(trip.getId(), AllocationMethod.AUTOMATIC);
            } catch (Exception e) {
                log.warn("Auto-reallocation after timeout for Trip #{} could not complete: {}", trip.getId(), e.getMessage());
            }
        }
    }
}

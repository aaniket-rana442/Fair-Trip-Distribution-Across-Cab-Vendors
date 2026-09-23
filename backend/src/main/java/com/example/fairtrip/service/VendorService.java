package com.example.fairtrip.service;

import com.example.fairtrip.dto.*;
import com.example.fairtrip.entity.*;
import com.example.fairtrip.entity.enums.*;
import com.example.fairtrip.exception.InvalidTripException;
import com.example.fairtrip.exception.ResourceNotFoundException;
import com.example.fairtrip.exception.UnauthorizedActionException;
import com.example.fairtrip.repository.*;
import com.example.fairtrip.util.PercentageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VendorService {

    private static final Logger log = LoggerFactory.getLogger(VendorService.class);

    private final VendorRepository vendorRepository;
    private final VendorTargetRepository targetRepository;
    private final TripRepository tripRepository;
    private final TripAllocationRepository allocationRepository;
    private final TripRejectionRepository rejectionRepository;
    private final VendorCooldownRepository cooldownRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CooldownService cooldownService;
    private final AuditService auditService;
    private final AllocationService allocationService;

    public VendorService(VendorRepository vendorRepository,
                         VendorTargetRepository targetRepository,
                         TripRepository tripRepository,
                         TripAllocationRepository allocationRepository,
                         TripRejectionRepository rejectionRepository,
                         VendorCooldownRepository cooldownRepository,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         CooldownService cooldownService,
                         AuditService auditService,
                         @Lazy AllocationService allocationService) {
        this.vendorRepository = vendorRepository;
        this.targetRepository = targetRepository;
        this.tripRepository = tripRepository;
        this.allocationRepository = allocationRepository;
        this.rejectionRepository = rejectionRepository;
        this.cooldownRepository = cooldownRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.cooldownService = cooldownService;
        this.auditService = auditService;
        this.allocationService = allocationService;
    }

    @Transactional
    @CacheEvict(value = "activeVendors", allEntries = true)
    public VendorResponse createVendor(VendorCreateRequest request) {
        if (vendorRepository.findByVendorCode(request.getVendorCode()).isPresent()) {
            throw new IllegalArgumentException("Vendor code already exists: " + request.getVendorCode());
        }

        User vendorUser = null;
        if (request.getUserPassword() != null && !request.getUserPassword().isBlank()) {
            Role vendorRole = roleRepository.findByName(RoleName.VENDOR)
                    .orElseThrow(() -> new ResourceNotFoundException("Role VENDOR not found"));
            vendorUser = new User(request.getVendorName(), request.getEmail(),
                    passwordEncoder.encode(request.getUserPassword()), vendorRole);
            vendorUser = userRepository.save(vendorUser);
        }

        Vendor vendor = new Vendor(request.getVendorCode(), request.getVendorName(), request.getEmail(), request.getTotalCapacity());
        vendor.setContactName(request.getContactName());
        vendor.setPhone(request.getPhone());
        vendor.setSupportsNormal(request.getSupportsNormal() != null ? request.getSupportsNormal() : true);
        vendor.setSupportsEscort(request.getSupportsEscort() != null ? request.getSupportsEscort() : true);
        vendor.setUser(vendorUser);

        Vendor saved = vendorRepository.save(vendor);

        // Save initial targets if provided
        if (request.getTargets() != null && !request.getTargets().isEmpty()) {
            for (VendorTargetDto td : request.getTargets()) {
                VendorTarget target = new VendorTarget(saved, td.getZone(), td.getTripCategory(), td.getTargetPercentage());
                targetRepository.save(target);
            }
        }

        log.info("VENDOR_ADDED: {} ({}) with capacity {}", saved.getVendorCode(), saved.getVendorName(), saved.getTotalCapacity());
        auditService.logAction("VENDOR_ADDED", "Vendor", saved.getId(), null, "Created vendor " + saved.getVendorCode());

        return getVendorDetails(saved.getId());
    }

    @Transactional
    @CacheEvict(value = "activeVendors", allEntries = true)
    public VendorResponse updateVendor(Long vendorId, VendorUpdateRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        String oldVal = "Name=" + vendor.getVendorName() + ", Capacity=" + vendor.getTotalCapacity() + ", Status=" + vendor.getStatus();

        if (request.getVendorName() != null) vendor.setVendorName(request.getVendorName());
        if (request.getContactName() != null) vendor.setContactName(request.getContactName());
        if (request.getPhone() != null) vendor.setPhone(request.getPhone());
        if (request.getStatus() != null) vendor.setStatus(request.getStatus());
        if (request.getTotalCapacity() != null) {
            int diff = request.getTotalCapacity() - vendor.getTotalCapacity();
            vendor.setTotalCapacity(request.getTotalCapacity());
            vendor.setAvailableCapacity(Math.max(0, vendor.getAvailableCapacity() + diff));
        }
        if (request.getAvailableCapacity() != null) {
            vendor.setAvailableCapacity(Math.min(request.getAvailableCapacity(), vendor.getTotalCapacity()));
        }
        if (request.getSupportsNormal() != null) vendor.setSupportsNormal(request.getSupportsNormal());
        if (request.getSupportsEscort() != null) vendor.setSupportsEscort(request.getSupportsEscort());

        Vendor saved = vendorRepository.save(vendor);
        log.info("VENDOR_UPDATED: ID #{} ({})", saved.getId(), saved.getVendorCode());

        auditService.logAction("VENDOR_UPDATED", "Vendor", saved.getId(), oldVal,
                "Name=" + saved.getVendorName() + ", Capacity=" + saved.getTotalCapacity() + ", Status=" + saved.getStatus());

        return getVendorDetails(saved.getId());
    }

    @Transactional
    @CacheEvict(value = "activeVendors", allEntries = true)
    public VendorResponse toggleStatus(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        VendorStatus newStatus = (vendor.getStatus() == VendorStatus.ACTIVE) ? VendorStatus.INACTIVE : VendorStatus.ACTIVE;
        vendor.setStatus(newStatus);
        Vendor saved = vendorRepository.save(vendor);

        auditService.logAction("VENDOR_STATUS_CHANGED", "Vendor", vendorId, null, "Status changed to " + newStatus);
        return getVendorDetails(saved.getId());
    }

    @Transactional
    @CacheEvict(value = "vendorTargets", allEntries = true)
    public void configureTargetsForStream(DistanceZone zone, TripCategory category, Map<Long, BigDecimal> vendorPercentages) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal pct : vendorPercentages.values()) {
            if (pct != null) {
                sum = sum.add(pct);
            }
        }

        if (!PercentageUtil.isSumCloseToOneHundred(sum)) {
            throw new IllegalArgumentException("Total target percentages for stream (" + zone + ", " + category + ") must equal 100.00%. Current sum: " + sum + "%");
        }

        for (Map.Entry<Long, BigDecimal> entry : vendorPercentages.entrySet()) {
            Long vId = entry.getKey();
            BigDecimal pct = entry.getValue();

            Vendor vendor = vendorRepository.findById(vId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vId));

            Optional<VendorTarget> existing = targetRepository.findByVendorIdAndZoneAndTripCategory(vId, zone, category);
            if (existing.isPresent()) {
                VendorTarget target = existing.get();
                target.setTargetPercentage(pct);
                targetRepository.save(target);
            } else {
                VendorTarget newTarget = new VendorTarget(vendor, zone, category, pct);
                targetRepository.save(newTarget);
            }
        }

        auditService.logAction("CONFIGURATION_CHANGED", "VendorTarget", null, null,
                "Configured targets for stream (" + zone + ", " + category + "): sum = " + sum + "%");
    }

    @Transactional(readOnly = true)
    public Vendor getVendorEntity(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));
    }

    @Transactional(readOnly = true)
    public Vendor getVendorByUserId(Long userId) {
        return vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No vendor profile associated with user ID: " + userId));
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToVendorResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "activeVendors")
    public List<Vendor> getActiveVendors() {
        return vendorRepository.findByStatus(VendorStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public VendorResponse getVendorDetails(Long vendorId) {
        Vendor vendor = getVendorEntity(vendorId);
        return mapToVendorResponse(vendor);
    }

    private VendorResponse mapToVendorResponse(Vendor vendor) {
        VendorResponse res = VendorResponse.fromEntity(vendor);

        List<VendorTarget> targets = targetRepository.findByVendorId(vendor.getId());
        res.setTargets(targets.stream().map(VendorTargetDto::fromEntity).collect(Collectors.toList()));

        boolean onCooldown = !cooldownRepository.findActiveCooldownsByVendor(vendor.getId(), LocalDateTime.now()).isEmpty();
        res.setOnCooldown(onCooldown);

        long allocated = tripRepository.countByAllocatedVendorId(vendor.getId());
        long accepted = tripRepository.countByAllocatedVendorIdAndStatus(vendor.getId(), TripStatus.ACCEPTED);
        long rejected = rejectionRepository.countByVendorId(vendor.getId());

        res.setTotalAllocatedTrips(allocated);
        res.setTotalAcceptedTrips(accepted);
        res.setTotalRejectedTrips(rejected);

        if (!targets.isEmpty()) {
            BigDecimal avgTarget = targets.stream()
                    .map(VendorTarget::getTargetPercentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(targets.size()), 2, PercentageUtil.ROUNDING_MODE);
            res.setAverageTargetPercentage(avgTarget);
        }

        long totalSystemTrips = tripRepository.count();
        res.setActualPercentage(PercentageUtil.calculateActualPercentage(allocated, totalSystemTrips));

        return res;
    }

    @Transactional
    public TripResponse acceptTrip(Long tripId, User vendorUser) {
        Vendor vendor = getVendorByUserId(vendorUser.getId());
        Trip trip = tripRepository.findWithLockById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        if (trip.getStatus() != TripStatus.OFFERED_TO_VENDOR) {
            throw new InvalidTripException("Trip is not in OFFERED_TO_VENDOR status. Current status: " + trip.getStatus());
        }

        if (trip.getAllocatedVendor() == null || !trip.getAllocatedVendor().getId().equals(vendor.getId())) {
            throw new UnauthorizedActionException("This trip was not offered to your vendor organisation");
        }

        TripAllocation activeOffer = allocationRepository.findActiveOfferByTripId(tripId)
                .orElseThrow(() -> new InvalidTripException("No active offer found for Trip #" + tripId));

        if (!activeOffer.getVendor().getId().equals(vendor.getId())) {
            throw new UnauthorizedActionException("Active offer does not belong to vendor: " + vendor.getVendorCode());
        }

        LocalDateTime now = LocalDateTime.now();
        if (activeOffer.getResponseDeadline().isBefore(now)) {
            activeOffer.setAllocationStatus(AllocationStatus.EXPIRED);
            allocationRepository.save(activeOffer);
            throw new InvalidTripException("Response deadline has expired for this trip offer");
        }

        activeOffer.setAllocationStatus(AllocationStatus.ACCEPTED);
        activeOffer.setAcceptedAt(now);
        allocationRepository.save(activeOffer);

        trip.setStatus(TripStatus.ACCEPTED);
        Trip savedTrip = tripRepository.save(trip);

        log.info("VENDOR_ACCEPTED: Trip #{} accepted by Vendor {}", tripId, vendor.getVendorCode());
        auditService.logAction("VENDOR_ACCEPTED", "Trip", tripId, TripStatus.OFFERED_TO_VENDOR.name(),
                "Accepted by " + vendor.getVendorCode());

        return TripResponse.fromEntity(savedTrip);
    }

    @Transactional
    public TripResponse rejectTrip(Long tripId, User vendorUser, String reason) {
        Vendor vendor = getVendorByUserId(vendorUser.getId());
        Trip trip = tripRepository.findWithLockById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        if (trip.getStatus() != TripStatus.OFFERED_TO_VENDOR) {
            throw new InvalidTripException("Trip is not in OFFERED_TO_VENDOR status. Current status: " + trip.getStatus());
        }

        if (trip.getAllocatedVendor() == null || !trip.getAllocatedVendor().getId().equals(vendor.getId())) {
            throw new UnauthorizedActionException("This trip was not offered to your vendor organisation");
        }

        TripAllocation activeOffer = allocationRepository.findActiveOfferByTripId(tripId)
                .orElseThrow(() -> new InvalidTripException("No active offer found for Trip #" + tripId));

        LocalDateTime now = LocalDateTime.now();
        activeOffer.setAllocationStatus(AllocationStatus.REJECTED);
        activeOffer.setRejectedAt(now);
        allocationRepository.save(activeOffer);

        // Record rejection
        TripRejection rejection = new TripRejection(trip, vendor, reason != null ? reason : "Declined by vendor operator");
        rejectionRepository.save(rejection);

        // Release vendor capacity
        Vendor lockedVendor = vendorRepository.findWithLockById(vendor.getId()).orElse(vendor);
        lockedVendor.incrementCapacity();
        vendorRepository.save(lockedVendor);

        // Start cooldown for this vendor for this specific trip
        cooldownService.startCooldown(lockedVendor, trip, rejection.getReason());

        trip.setStatus(TripStatus.REALLOCATING);
        trip.setAllocatedVendor(null);
        Trip reallocatingTrip = tripRepository.save(trip);

        log.info("VENDOR_REJECTED: Trip #{} rejected by Vendor {}. Reason: {}. Trip set to REALLOCATING",
                tripId, vendor.getVendorCode(), rejection.getReason());

        auditService.logAction("VENDOR_REJECTED", "Trip", tripId, TripStatus.OFFERED_TO_VENDOR.name(),
                "Rejected by " + vendor.getVendorCode() + ". Reason: " + rejection.getReason());

        // Automatically trigger reallocation to next eligible vendor
        try {
            allocationService.allocateTrip(tripId, AllocationMethod.AUTOMATIC);
        } catch (Exception e) {
            log.warn("Auto-reallocation for Trip #{} could not complete immediately: {}", tripId, e.getMessage());
        }

        return TripResponse.fromEntity(tripRepository.findById(tripId).orElse(reallocatingTrip));
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getVendorTrips(User vendorUser) {
        Vendor vendor = getVendorByUserId(vendorUser.getId());
        List<TripAllocation> allocations = allocationRepository.findByVendorIdOrderByOfferedAtDesc(vendor.getId());

        List<TripResponse> results = new ArrayList<>();
        Set<Long> seenTripIds = new HashSet<>();

        for (TripAllocation alloc : allocations) {
            Trip t = alloc.getTrip();
            if (seenTripIds.add(t.getId())) {
                TripResponse res = TripResponse.fromEntity(t);
                res.setResponseDeadline(alloc.getResponseDeadline());
                results.add(res);
            }
        }
        return results;
    }
}

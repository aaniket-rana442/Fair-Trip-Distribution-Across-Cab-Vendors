package com.example.fairtrip.service;

import com.example.fairtrip.dto.TripCreateRequest;
import com.example.fairtrip.dto.TripResponse;
import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.User;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.RoleName;
import com.example.fairtrip.entity.enums.TripStatus;
import com.example.fairtrip.exception.InvalidTripException;
import com.example.fairtrip.exception.ResourceNotFoundException;
import com.example.fairtrip.exception.UnauthorizedActionException;
import com.example.fairtrip.repository.TripRepository;
import com.example.fairtrip.repository.VendorRepository;
import com.example.fairtrip.util.DistanceZoneUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private static final Logger log = LoggerFactory.getLogger(TripService.class);

    private final TripRepository tripRepository;
    private final VendorRepository vendorRepository;
    private final AuditService auditService;

    public TripService(TripRepository tripRepository,
                       VendorRepository vendorRepository,
                       AuditService auditService) {
        this.tripRepository = tripRepository;
        this.vendorRepository = vendorRepository;
        this.auditService = auditService;
    }

    @Transactional
    public TripResponse createTrip(TripCreateRequest request, User currentUser) {
        if (request.getDistanceKm() == null || request.getDistanceKm().doubleValue() < 0) {
            throw new InvalidTripException("Trip distance must be non-negative");
        }
        if (request.getPassengerCount() == null || request.getPassengerCount() < 1) {
            throw new InvalidTripException("Passenger count must be at least 1");
        }

        Trip trip = new Trip(
                currentUser,
                request.getPassengerName(),
                request.getPickupLocation(),
                request.getDestination(),
                request.getPassengerCount(),
                request.getDistanceKm(),
                request.getTripCategory(),
                request.getNotes()
        );

        // Explicitly compute distance zone on backend
        trip.setDistanceZone(DistanceZoneUtil.getZone(request.getDistanceKm()));
        trip.setStatus(TripStatus.PENDING);

        Trip saved = tripRepository.save(trip);
        log.info("TRIP_CREATED: ID #{} by user {} - Zone: {}, Category: {}, Dist: {} km",
                saved.getId(), currentUser.getEmail(), saved.getDistanceZone(), saved.getTripCategory(), saved.getDistanceKm());

        auditService.logAction("TRIP_CREATED", "Trip", saved.getId(),
                null, "Created trip from " + saved.getPickupLocation() + " to " + saved.getDestination() + " (Zone: " + saved.getDistanceZone() + ")");

        return TripResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Trip getTripEntity(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
    }

    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId, User currentUser) {
        Trip trip = getTripEntity(tripId);
        // Authorization guard: normal users can only view their own trips
        if (currentUser.getRole().getName() == RoleName.NORMAL_USER
                && !trip.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not authorized to view another user's trip details");
        }
        return TripResponse.fromEntity(trip);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getMyTrips(User currentUser) {
        return tripRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getPendingTrips() {
        return tripRepository.findByStatusOrderByCreatedAtDesc(TripStatus.PENDING).stream()
                .map(TripResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void transitionTripStatus(Trip trip, TripStatus targetStatus) {
        TripStatus current = trip.getStatus();
        if (current == targetStatus) {
            return;
        }
        if (!current.canTransitionTo(targetStatus)) {
            throw new InvalidTripException("Illegal state transition from " + current + " to " + targetStatus);
        }
        trip.setStatus(targetStatus);
        tripRepository.save(trip);
    }

    @Transactional
    public TripResponse cancelTrip(Long tripId, User currentUser) {
        Trip trip = tripRepository.findWithLockById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        // Authorization check
        if (currentUser.getRole().getName() == RoleName.NORMAL_USER
                && !trip.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not authorized to cancel this trip");
        }

        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new InvalidTripException("Trip cannot be cancelled in state: " + trip.getStatus());
        }

        TripStatus oldStatus = trip.getStatus();

        // Release vendor capacity if allocated or offered
        if (trip.getAllocatedVendor() != null &&
                (oldStatus == TripStatus.OFFERED_TO_VENDOR || oldStatus == TripStatus.ACCEPTED || oldStatus == TripStatus.ALLOCATED)) {
            Vendor vendor = vendorRepository.findWithLockById(trip.getAllocatedVendor().getId()).orElse(null);
            if (vendor != null) {
                vendor.incrementCapacity();
                vendorRepository.save(vendor);
            }
        }

        trip.setStatus(TripStatus.CANCELLED);
        Trip saved = tripRepository.save(trip);

        auditService.logAction("TRIP_CANCELLED", "Trip", tripId, oldStatus.name(), TripStatus.CANCELLED.name());
        return TripResponse.fromEntity(saved);
    }
}

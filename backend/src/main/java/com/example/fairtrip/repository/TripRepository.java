package com.example.fairtrip.repository;

import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import com.example.fairtrip.entity.enums.TripStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Trip> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Trip> findByStatusOrderByCreatedAtDesc(TripStatus status);

    List<Trip> findByAllocatedVendorIdOrderByCreatedAtDesc(Long vendorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Trip t WHERE t.id = :id")
    Optional<Trip> findWithLockById(@Param("id") Long id);

    long countByStatus(TripStatus status);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreatedAtBetween(TripStatus status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.distanceZone = :zone AND t.tripCategory = :category AND t.status IN ('OFFERED_TO_VENDOR', 'ACCEPTED', 'COMPLETED')")
    long countHistoricalTripsInStream(@Param("zone") DistanceZone zone, @Param("category") TripCategory category);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.allocatedVendor.id = :vendorId AND t.distanceZone = :zone AND t.tripCategory = :category AND t.status IN ('OFFERED_TO_VENDOR', 'ACCEPTED', 'COMPLETED')")
    long countVendorHistoricalTripsInStream(@Param("vendorId") Long vendorId, @Param("zone") DistanceZone zone, @Param("category") TripCategory category);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.allocatedVendor.id = :vendorId AND t.status = :status")
    long countByAllocatedVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") TripStatus status);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.allocatedVendor.id = :vendorId AND t.createdAt BETWEEN :start AND :end")
    long countByAllocatedVendorIdAndCreatedAtBetween(@Param("vendorId") Long vendorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.allocatedVendor.id = :vendorId")
    long countByAllocatedVendorId(@Param("vendorId") Long vendorId);
}

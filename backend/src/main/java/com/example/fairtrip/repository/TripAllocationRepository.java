package com.example.fairtrip.repository;

import com.example.fairtrip.entity.TripAllocation;
import com.example.fairtrip.entity.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripAllocationRepository extends JpaRepository<TripAllocation, Long> {

    List<TripAllocation> findByTripIdOrderByAllocationSequenceDesc(Long tripId);

    List<TripAllocation> findByVendorIdOrderByOfferedAtDesc(Long vendorId);

    List<TripAllocation> findByVendorIdAndAllocationStatusOrderByOfferedAtDesc(Long vendorId, AllocationStatus status);

    @Query("SELECT ta FROM TripAllocation ta WHERE ta.trip.id = :tripId AND ta.allocationStatus = 'OFFERED'")
    Optional<TripAllocation> findActiveOfferByTripId(@Param("tripId") Long tripId);

    @Query("SELECT ta FROM TripAllocation ta WHERE ta.allocationStatus = 'OFFERED' AND ta.responseDeadline < :now")
    List<TripAllocation> findExpiredOffers(@Param("now") LocalDateTime now);

    @Query("SELECT MAX(ta.allocationSequence) FROM TripAllocation ta WHERE ta.trip.id = :tripId")
    Optional<Integer> findMaxSequenceByTripId(@Param("tripId") Long tripId);

    @Query("SELECT MAX(ta.offeredAt) FROM TripAllocation ta WHERE ta.vendor.id = :vendorId")
    Optional<LocalDateTime> findLastAllocationTimeByVendorId(@Param("vendorId") Long vendorId);

    long countByVendorIdAndAllocationStatus(Long vendorId, AllocationStatus status);
}

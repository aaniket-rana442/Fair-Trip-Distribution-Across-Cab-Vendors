package com.example.fairtrip.repository;

import com.example.fairtrip.entity.AllocationHistory;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AllocationHistoryRepository extends JpaRepository<AllocationHistory, Long> {

    List<AllocationHistory> findByTripIdOrderByAllocationSequenceAsc(Long tripId);

    List<AllocationHistory> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.zone = :zone AND ah.tripCategory = :category")
    long countTotalTripsByStream(@Param("zone") DistanceZone zone, @Param("category") TripCategory category);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.vendor.id = :vendorId AND ah.zone = :zone AND ah.tripCategory = :category")
    long countVendorTripsByStream(@Param("vendorId") Long vendorId, @Param("zone") DistanceZone zone, @Param("category") TripCategory category);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.vendor.id = :vendorId AND ah.zone = :zone AND ah.tripCategory = :category AND ah.createdAt BETWEEN :start AND :end")
    long countVendorTripsByStreamInPeriod(@Param("vendorId") Long vendorId, @Param("zone") DistanceZone zone, @Param("category") TripCategory category, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.zone = :zone AND ah.tripCategory = :category AND ah.createdAt BETWEEN :start AND :end")
    long countTotalTripsByStreamInPeriod(@Param("zone") DistanceZone zone, @Param("category") TripCategory category, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.vendor.id = :vendorId AND ah.createdAt BETWEEN :start AND :end")
    long countVendorTripsInPeriod(@Param("vendorId") Long vendorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.vendor.id = :vendorId")
    long countVendorTotalTrips(@Param("vendorId") Long vendorId);

    @Query("SELECT COUNT(ah) FROM AllocationHistory ah WHERE ah.createdAt BETWEEN :start AND :end")
    long countTotalTripsInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

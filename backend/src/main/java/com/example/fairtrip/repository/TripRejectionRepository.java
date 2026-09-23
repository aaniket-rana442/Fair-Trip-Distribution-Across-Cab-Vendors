package com.example.fairtrip.repository;

import com.example.fairtrip.entity.TripRejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRejectionRepository extends JpaRepository<TripRejection, Long> {
    List<TripRejection> findByTripId(Long tripId);
    boolean existsByTripIdAndVendorId(Long tripId, Long vendorId);
    long countByVendorId(Long vendorId);
}

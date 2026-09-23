package com.example.fairtrip.repository;

import com.example.fairtrip.entity.VendorTarget;
import com.example.fairtrip.entity.enums.DistanceZone;
import com.example.fairtrip.entity.enums.TripCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorTargetRepository extends JpaRepository<VendorTarget, Long> {
    List<VendorTarget> findByVendorId(Long vendorId);

    List<VendorTarget> findByZoneAndTripCategory(DistanceZone zone, TripCategory tripCategory);

    Optional<VendorTarget> findByVendorIdAndZoneAndTripCategory(Long vendorId, DistanceZone zone, TripCategory tripCategory);

    @Query("SELECT vt FROM VendorTarget vt WHERE vt.zone = :zone AND vt.tripCategory = :tripCategory AND (vt.effectiveTo IS NULL OR vt.effectiveTo > CURRENT_TIMESTAMP)")
    List<VendorTarget> findActiveTargetsByStream(@Param("zone") DistanceZone zone, @Param("tripCategory") TripCategory tripCategory);

    void deleteByVendorId(Long vendorId);
}

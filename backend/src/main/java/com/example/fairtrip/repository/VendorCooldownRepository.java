package com.example.fairtrip.repository;

import com.example.fairtrip.entity.VendorCooldown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorCooldownRepository extends JpaRepository<VendorCooldown, Long> {

    @Query("SELECT vc FROM VendorCooldown vc WHERE vc.vendor.id = :vendorId AND vc.trip.id = :tripId AND vc.active = true AND vc.endTime > :now")
    Optional<VendorCooldown> findActiveCooldown(@Param("vendorId") Long vendorId, @Param("tripId") Long tripId, @Param("now") LocalDateTime now);

    @Query("SELECT vc FROM VendorCooldown vc WHERE vc.vendor.id = :vendorId AND vc.active = true AND vc.endTime > :now")
    List<VendorCooldown> findActiveCooldownsByVendor(@Param("vendorId") Long vendorId, @Param("now") LocalDateTime now);

    @Query("SELECT vc FROM VendorCooldown vc WHERE vc.active = true AND vc.endTime <= :now")
    List<VendorCooldown> findExpiredCooldowns(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(DISTINCT vc.vendor.id) FROM VendorCooldown vc WHERE vc.active = true AND vc.endTime > :now")
    long countDistinctVendorsInActiveCooldown(@Param("now") LocalDateTime now);
}

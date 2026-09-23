package com.example.fairtrip.repository;

import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.VendorStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByVendorCode(String vendorCode);
    Optional<Vendor> findByUserId(Long userId);
    List<Vendor> findByStatus(VendorStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vendor v WHERE v.id = :id")
    Optional<Vendor> findWithLockById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vendor v WHERE v.id IN :ids")
    List<Vendor> findWithLockByIdIn(@Param("ids") List<Long> ids);
}

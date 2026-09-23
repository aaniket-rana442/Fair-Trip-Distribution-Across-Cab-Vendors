package com.example.fairtrip.repository;

import com.example.fairtrip.entity.FairnessSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FairnessSnapshotRepository extends JpaRepository<FairnessSnapshot, Long> {
    List<FairnessSnapshot> findByPeriodDate(LocalDate periodDate);
    List<FairnessSnapshot> findByVendorId(Long vendorId);
}

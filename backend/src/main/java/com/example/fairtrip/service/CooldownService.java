package com.example.fairtrip.service;

import com.example.fairtrip.entity.Trip;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.VendorCooldown;
import com.example.fairtrip.repository.VendorCooldownRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CooldownService {

    private static final Logger log = LoggerFactory.getLogger(CooldownService.class);

    private final VendorCooldownRepository cooldownRepository;
    private final SystemConfigService configService;
    private final AuditService auditService;

    public CooldownService(VendorCooldownRepository cooldownRepository,
                           SystemConfigService configService,
                           AuditService auditService) {
        this.cooldownRepository = cooldownRepository;
        this.configService = configService;
        this.auditService = auditService;
    }

    @Transactional
    public VendorCooldown startCooldown(Vendor vendor, Trip trip, String reason) {
        int durationMinutes = configService.getVendorCooldownMinutes();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(durationMinutes);

        VendorCooldown cooldown = new VendorCooldown(vendor, trip, now, endTime, reason);
        VendorCooldown saved = cooldownRepository.save(cooldown);

        log.info("VENDOR_COOLDOWN_STARTED: Vendor {} for Trip #{} until {}",
                vendor.getVendorCode(), trip.getId(), endTime);

        auditService.logAction("VENDOR_COOLDOWN_STARTED", "Vendor", vendor.getId(),
                null, "Cooldown for Trip #" + trip.getId() + " until " + endTime + " Reason: " + reason);

        return saved;
    }

    @Transactional(readOnly = true)
    public boolean isVendorInCooldown(Long vendorId, Long tripId) {
        return cooldownRepository.findActiveCooldown(vendorId, tripId, LocalDateTime.now()).isPresent();
    }

    @Transactional(readOnly = true)
    public Set<Long> getActiveCooldownVendorIds(Long tripId) {
        LocalDateTime now = LocalDateTime.now();
        List<VendorCooldown> cooldowns = cooldownRepository.findAll();
        Set<Long> result = new HashSet<>();
        for (VendorCooldown c : cooldowns) {
            if (c.getActive() && c.getTrip().getId().equals(tripId) && c.getEndTime().isAfter(now)) {
                result.add(c.getVendor().getId());
            }
        }
        return result;
    }

    @Transactional
    public int cleanExpiredCooldowns() {
        LocalDateTime now = LocalDateTime.now();
        List<VendorCooldown> expired = cooldownRepository.findExpiredCooldowns(now);
        for (VendorCooldown vc : expired) {
            vc.setActive(false);
        }
        cooldownRepository.saveAll(expired);
        return expired.size();
    }
}

package com.example.fairtrip.service;

import com.example.fairtrip.entity.AuditLog;
import com.example.fairtrip.entity.User;
import com.example.fairtrip.repository.AuditLogRepository;
import com.example.fairtrip.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, String entityType, Long entityId, String oldValue, String newValue) {
        User currentUser = getCurrentUser();
        AuditLog auditLog = new AuditLog(currentUser, action, entityType, entityId, oldValue, newValue, null);
        auditLogRepository.save(auditLog);
        log.info("[AUDIT] User: {} | Action: {} | Entity: {} #{} | Details: {}",
                currentUser != null ? currentUser.getEmail() : "SYSTEM", action, entityType, entityId, newValue);
    }

    private User getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String email = auth.getName();
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            log.debug("No authenticated user found for audit log");
        }
        return null;
    }
}

package com.example.fairtrip.scheduler;

import com.example.fairtrip.service.CooldownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CooldownScheduler {

    private static final Logger log = LoggerFactory.getLogger(CooldownScheduler.class);

    private final CooldownService cooldownService;

    public CooldownScheduler(CooldownService cooldownService) {
        this.cooldownService = cooldownService;
    }

    /**
     * Cleans up expired cooldowns every minute.
     */
    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredCooldowns() {
        int cleaned = cooldownService.cleanExpiredCooldowns();
        if (cleaned > 0) {
            log.info("Cleaned up {} expired vendor cooldowns", cleaned);
        }
    }
}

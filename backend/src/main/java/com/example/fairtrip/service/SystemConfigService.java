package com.example.fairtrip.service;

import com.example.fairtrip.entity.SystemConfiguration;
import com.example.fairtrip.repository.SystemConfigurationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {

    private final SystemConfigurationRepository configRepository;

    public SystemConfigService(SystemConfigurationRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Cacheable(value = "systemConfigs", key = "#key")
    public String getConfigValue(String key, String defaultValue) {
        return configRepository.findByConfigKey(key)
                .map(SystemConfiguration::getConfigValue)
                .orElse(defaultValue);
    }

    public int getIntConfig(String key, int defaultValue) {
        String val = getConfigValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getVendorResponseTimeoutMinutes() {
        return getIntConfig("VENDOR_RESPONSE_TIMEOUT_MINUTES", 5);
    }

    public int getVendorCooldownMinutes() {
        return getIntConfig("VENDOR_COOLDOWN_MINUTES", 10);
    }

    public int getMaxRetryAttempts() {
        return getIntConfig("MAX_RETRY_ATTEMPTS", 5);
    }

    @CacheEvict(value = "systemConfigs", key = "#key")
    public void setConfigValue(String key, String value, String description) {
        SystemConfiguration config = configRepository.findByConfigKey(key)
                .orElse(new SystemConfiguration(key, value, description));
        config.setConfigValue(value);
        if (description != null) {
            config.setDescription(description);
        }
        configRepository.save(config);
    }
}

package com.taarifu_engine_api.modules.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for SMS service.
 * Loads SMS configuration from application.yml
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * SMS provider name (e.g., "beem")
     */
    private String provider = "beem";

    /**
     * SMS provider username/API key
     */
    private String username;

    /**
     * SMS provider password/API secret
     */
    private String password;

    /**
     * SMS sender ID
     */
    private String senderId = "OTAPP";

    /**
     * Whether SMS service is enabled
     */
    private Boolean enabled = false;
}


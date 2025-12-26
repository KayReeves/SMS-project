package com.kritim_mind.sms_project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "esewa")
@Data
public class EsewaConfig {
    private String productCode;
    private String secretKey;
    private String paymentUrl;
    private String statusUrl;
}

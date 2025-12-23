package com.kritim_mind.sms_project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "khalti")
public class KhaltiConfig {

    private String secretKey;
    private String publicKey;
    private String baseUrl;
}
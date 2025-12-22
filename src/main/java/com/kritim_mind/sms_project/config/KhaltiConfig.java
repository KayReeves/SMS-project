package com.kritim_mind.sms_project.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KhaltiConfig {

    @Value("${khalti.base-url}")
    private String baseUrl;

    @Value("${khalti.secret-key}")
    private String secretKey;
}

package com.kritim_mind.sms_project.config;

import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            String defaultUsername = "admin";
            String defaultPassword = "admin123";
            String defaultEmail = "admin@gmail.com";

            if (adminRepository.findByUsername(defaultUsername).isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername(defaultUsername);
                admin.setPasswordHash(passwordEncoder.encode(defaultPassword));
                admin.setEmail(defaultEmail);
                admin.setTotalSmsCredits(1000);
                admin.setUsedSmsCredits(0);

                adminRepository.save(admin);
                System.out.println("Default admin created: " + defaultUsername);
            }
        };
    }
}

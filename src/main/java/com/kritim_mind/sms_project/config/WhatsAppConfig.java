//package com.kritim_mind.sms_project.config;
//
//import lombok.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WhatsAppConfig {
//        @Value("${whatsapp.api.url}")
//        private String apiUrl;
//
//        @Value("${whatsapp.access.token}")
//        private String accessToken;
//
//        @Bean
//        public WebClient whatsAppWebClient() {
//            return WebClient.builder()
//                    .baseUrl(apiUrl)
//                    .defaultHeader("Authorization", "Bearer " + accessToken)
//                    .defaultHeader("Content-Type", "application/json")
//                    .build();
//        }
//    }

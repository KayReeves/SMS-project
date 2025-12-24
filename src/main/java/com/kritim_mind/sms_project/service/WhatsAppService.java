//package com.kritim_mind.sms_project.service;
//
//import com.kritim_mind.sms_project.dto.request.WhatsAppRequest;
//import com.kritim_mind.sms_project.dto.response.WhatsAppResponse;
//import lombok.Value;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class WhatsAppService {
//
//    @Autowired
//    private WebClient whatsAppWebClient;
//
//    @Value("${whatsapp.phone.number.id}")
//    private String phoneNumberId;
//
//    public Mono<WhatsAppResponse> sendTextMessage(String recipientPhone, String message) {
//        WhatsAppRequest request = new WhatsAppRequest();
//        request.setTo(recipientPhone);
//        request.setType("text");
//        request.setText(new WhatsAppRequest.TextMessage(message));
//
//        return whatsAppWebClient.post()
//                .uri("/" + phoneNumberId + "/messages")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(WhatsAppResponse.class)
//                .doOnSuccess(response -> log.info("Message sent successfully: {}", response))
//                .doOnError(error -> log.error("Error sending message: {}", error.getMessage()));
//    }
//
//    public Mono<WhatsAppResponse> sendTemplateMessage(String recipientPhone, String templateName, String languageCode) {
//        Map<String, Object> request = new HashMap<>();
//        request.put("messaging_product", "whatsapp");
//        request.put("to", recipientPhone);
//        request.put("type", "template");
//
//        Map<String, Object> template = new HashMap<>();
//        template.put("name", templateName);
//
//        Map<String, String> language = new HashMap<>();
//        language.put("code", languageCode);
//        template.put("language", language);
//
//        request.put("template", template);
//
//        return whatsAppWebClient.post()
//                .uri("/" + phoneNumberId + "/messages")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(WhatsAppResponse.class);
//    }
//}

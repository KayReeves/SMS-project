//package com.kritim_mind.sms_project.controller;
//
//import com.kritim_mind.sms_project.dto.response.WhatsAppResponse;
//import com.kritim_mind.sms_project.service.WhatsAppService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/api/whatsapp")
//public class WhatsAppController {
//
//    @Autowired
//    private WhatsAppService whatsAppService;
//
//    @PostMapping("/send")
//    public Mono<ResponseEntity<WhatsAppResponse>> sendMessage(
//            @RequestParam String phone,
//            @RequestParam String message) {
//
//        return whatsAppService.sendTextMessage(phone, message)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(
//                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
//                ));
//    }
//
//    @PostMapping("/send-template")
//    public Mono<ResponseEntity<WhatsAppResponse>> sendTemplate(
//            @RequestParam String phone,
//            @RequestParam String templateName,
//            @RequestParam(defaultValue = "en") String languageCode) {
//
//        return whatsAppService.sendTemplateMessage(phone, templateName, languageCode)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(
//                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
//                ));
//    }
//}

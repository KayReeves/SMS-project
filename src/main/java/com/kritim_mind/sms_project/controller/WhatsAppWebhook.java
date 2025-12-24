//package com.kritim_mind.sms_project.controller;
//
//import lombok.Value;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/webhook")
//@Slf4j
//public class WhatsAppWebhook {
//        @Value("${whatsapp.webhook.verify.token}")
//        private String verifyToken;
//
//        // Webhook verification (GET request)
//        @GetMapping
//        public ResponseEntity<String> verifyWebhook(
//                @RequestParam("hub.mode") String mode,
//                @RequestParam("hub.verify_token") String token,
//                @RequestParam("hub.challenge") String challenge) {
//
//            if ("subscribe".equals(mode) && verifyToken.equals(token)) {
//                log.info("Webhook verified successfully");
//                return ResponseEntity.ok(challenge);
//            }
//
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        // Receive incoming messages (POST request)
//        @PostMapping
//        public ResponseEntity<Void> receiveMessage(@RequestBody Map<String, Object> payload) {
//            log.info("Received webhook payload: {}", payload);
//
//            try {
//                // Extract message details
//                List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
//
//                for (Map<String, Object> entry : entries) {
//                    List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
//
//                    for (Map<String, Object> change : changes) {
//                        Map<String, Object> value = (Map<String, Object>) change.get("value");
//
//                        if (value.containsKey("messages")) {
//                            List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
//
//                            for (Map<String, Object> message : messages) {
//                                String from = (String) message.get("from");
//                                String messageId = (String) message.get("id");
//                                String messageType = (String) message.get("type");
//
//                                if ("text".equals(messageType)) {
//                                    Map<String, Object> text = (Map<String, Object>) message.get("text");
//                                    String body = (String) text.get("body");
//
//                                    log.info("Received message from {}: {}", from, body);
//
//                                    // Process the message here
//                                    processIncomingMessage(from, body);
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Error processing webhook: {}", e.getMessage());
//            }
//
//            return ResponseEntity.ok().build();
//        }
//
//        private void processIncomingMessage(String from, String message) {
//            // Implement your business logic here
//            log.info("Processing message from {}: {}", from, message);
//        }
//
//}

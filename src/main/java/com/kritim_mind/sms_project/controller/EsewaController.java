package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.response.EsewaXmlResponse;
import com.kritim_mind.sms_project.model.EsewaPayment;
import com.kritim_mind.sms_project.repository.EsewaPaymentRepository;
import com.kritim_mind.sms_project.service.EsewaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/esewa")
public class EsewaController {

    private final EsewaService service;
    private final EsewaPaymentRepository esewaPaymentRepository;

    public EsewaController(EsewaService service, EsewaPaymentRepository esewaPaymentRepository) {
        this.service = service;
        this.esewaPaymentRepository = esewaPaymentRepository;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiate(@RequestBody Map<String, Object> body) {
        double amount = ((Number) body.get("amount")).doubleValue();
        return ResponseEntity.ok(service.initiatePayment(amount));
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyFromFrontend(@RequestBody Map<String, String> body) throws Exception {
        String data = body.get("data");
        EsewaXmlResponse res = service.verifyPaymentFromData(data);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestParam String transaction_uuid,
            @RequestParam String total_amount) throws Exception {

        EsewaXmlResponse res =
                service.verifyPayment(transaction_uuid, total_amount);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<EsewaPayment>> getTransactions() {
        List<EsewaPayment> transactions = esewaPaymentRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(transactions);
    }
}
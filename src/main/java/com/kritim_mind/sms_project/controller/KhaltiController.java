package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.KhaltiTopupRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import com.kritim_mind.sms_project.model.KhaltiPayment;
import com.kritim_mind.sms_project.service.KhaltiService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/khalti")
@CrossOrigin(origins = {"http://localhost:3000"})
public class KhaltiController {

    private final KhaltiService service;

    public KhaltiController(KhaltiService service) {
        this.service = service;
    }


    @PostMapping("/initiate")
    public ResponseEntity<KhaltiInitiateResponse> initiate(
            @Valid @RequestBody KhaltiTopupRequest request) {

        return ResponseEntity.ok(service.initiatePayment(request));
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody KhaltiVerifyRequest request) {
        KhaltiPayment payment = service.verifyAndSavePayment(request);
        return ResponseEntity.ok(Map.of(
                "message", "Payment verified and saved",
                "transactionId", payment.getTransactionId(),
                "data", payment
        ));
    }


    @GetMapping("/payments")
    public ResponseEntity<List<KhaltiPayment>> payments() {
        return ResponseEntity.ok(service.getAllPayments());
    }


    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam String pidx,
            @RequestParam String status) {

        if ("Completed".equalsIgnoreCase(status)) {
            service.verifyAndSavePayment(new KhaltiVerifyRequest(pidx));
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location",
                        "http://localhost:3000/balance-report?status=" + status + "&pidx=" + pidx)
                .build();
    }
}

package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kritim_mind.sms_project.dto.request.KhaltiInitiateRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import com.kritim_mind.sms_project.service.KhaltiService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/khalti")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "https://yourfrontend.com"})
@Validated
public class KhaltiController {

    private final KhaltiService khaltiService;

    public KhaltiController(KhaltiService khaltiService) {
        this.khaltiService = khaltiService;
    }


    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@Valid @RequestBody KhaltiInitiateRequest request) {
        try {
            log.info("Received payment initiation request for amount: {}", request.getAmount());
            KhaltiInitiateResponse response = khaltiService.initiatePayment(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Validation Error",
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            log.error("Error initiating payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Payment Initiation Failed",
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody KhaltiVerifyRequest request) {
        try {
            log.info("Received payment verification request for PIDX: {}", request.getPidx());
            Map<String, Object> verificationResult = khaltiService.verifyPayment(request);
            return ResponseEntity.ok(verificationResult);
        } catch (RuntimeException e) {
            log.error("Error verifying payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Payment Verification Failed",
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/verify/batch")
    public ResponseEntity<?> verifyBatch(@Valid @RequestBody List<KhaltiVerifyRequest> requests) {
        try {
            log.info("Received batch verification request for {} payments", requests.size());
            List<Map<String, Object>> results = requests.stream()
                    .map(khaltiService::verifyPayment)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            log.error("Error in batch verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Batch Verification Failed",
                    "message", e.getMessage()
            ));
        }
    }


    @GetMapping("/callback")
    public ResponseEntity<String> paymentCallback(
            @RequestParam("pidx") String pidx,
            @RequestParam("status") String status,
            @RequestParam(value = "transaction_id", required = false) String transactionId,
            @RequestParam(value = "amount", required = false) String amount) {

        log.info("Payment callback received - PIDX: {}, Status: {}", pidx, status);

        if ("Completed".equalsIgnoreCase(status)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:3000/balance-report?status=Completed&pidx=" + pidx)
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:3000/balance-report?status=Failed&pidx=" + pidx)
                    .build();
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Validation Failed",
                "fields", errors
        ));
    }


    @ExceptionHandler({ JsonMappingException.class, InvalidFormatException.class })
    public ResponseEntity<Map<String, Object>> handleJsonParseErrors(Exception ex) {
        log.error("JSON parsing error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Invalid JSON Payload",
                "message", ex.getMessage(),
                "hint", "Check your request body structure matches the expected format"
        ));
    }

    /**
     * Global exception handler for general errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralErrors(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please try again later."
        ));
    }
}
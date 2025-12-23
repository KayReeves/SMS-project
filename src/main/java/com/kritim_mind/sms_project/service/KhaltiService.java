package com.kritim_mind.sms_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.config.KhaltiConfig;
import com.kritim_mind.sms_project.dto.request.KhaltiInitiateRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class KhaltiService {

    private final KhaltiConfig khaltiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public KhaltiService(KhaltiConfig khaltiConfig) {
        this.khaltiConfig = khaltiConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    public KhaltiInitiateResponse initiatePayment(KhaltiInitiateRequest request) {
        String url = khaltiConfig.getBaseUrl() + "epayment/initiate/";

        log.info("Initiating Khalti payment for amount: {}", request.getAmount());

        if (request.getReturn_url() == null || request.getWebsite_url() == null ||
                request.getAmount() == null || request.getPurchase_order_id() == null ||
                request.getPurchase_order_name() == null) {
            throw new IllegalArgumentException("Missing required fields for Khalti initiation");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());


        try {
            String requestJson = objectMapper.writeValueAsString(request);
            log.debug("Khalti request payload: {}", requestJson);
        } catch (JsonProcessingException e) {
            log.error("Error serializing request", e);
        }

        HttpEntity<KhaltiInitiateRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Khalti payment initiation failed with status: {}", response.getStatusCode());
                throw new RuntimeException("Khalti payment initiation failed with status: " +
                        response.getStatusCode() + " - " + response.getBody());
            }

            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new RuntimeException("Empty response from Khalti");
            }

            log.info("Khalti payment initiated successfully. PIDX: {}", body.get("pidx"));

            KhaltiInitiateResponse res = new KhaltiInitiateResponse();
            res.setPaymentUrl((String) body.get("payment_url"));
            res.setPidx((String) body.get("pidx"));
            res.setMessage("Payment initiated successfully");

            return res;

        } catch (HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("Khalti API error response: {}", errorBody);

            try {
                Map<String, Object> errorMap = objectMapper.readValue(errorBody, Map.class);
                throw new RuntimeException("Khalti API error: " + errorMap.toString());
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Khalti API error (raw): " + errorBody, e);
            }
        } catch (Exception e) {
            log.error("Unexpected error during Khalti initiation", e);
            throw new RuntimeException("Unexpected error during Khalti initiation: " + e.getMessage(), e);
        }
    }

    /**
     * Verify payment with Khalti
     */
    public Map<String, Object> verifyPayment(KhaltiVerifyRequest request) {
        String url = khaltiConfig.getBaseUrl() + "epayment/lookup/";

        log.info("Verifying Khalti payment for PIDX: {}", request.getPidx());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        HttpEntity<KhaltiVerifyRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Khalti verification failed with status: {}", response.getStatusCode());
                throw new RuntimeException("Khalti verification failed with status: " +
                        response.getStatusCode() + " - " + response.getBody());
            }

            Map<String, Object> body = response.getBody();
            log.info("Khalti payment verification successful: {}", body);

            return body;

        } catch (HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("Khalti lookup error: {}", errorBody);
            throw new RuntimeException("Khalti lookup error: " + errorBody, e);
        } catch (Exception e) {
            log.error("Unexpected error during Khalti verification", e);
            throw new RuntimeException("Unexpected error during Khalti verification: " + e.getMessage(), e);
        }
    }
}
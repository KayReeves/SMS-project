package com.kritim_mind.sms_project.service;

import com.kritim_mind.sms_project.config.KhaltiConfig;
import com.kritim_mind.sms_project.dto.request.KhaltiInitiateRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KhaltiService {

    private final KhaltiConfig khaltiConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public KhaltiService(KhaltiConfig khaltiConfig) {
        this.khaltiConfig = khaltiConfig;
    }

    /** Initiate payment with Khalti */
    public KhaltiInitiateResponse initiatePayment(KhaltiInitiateRequest request) {
        String url = khaltiConfig.getBaseUrl() + "epayment/initiate/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        HttpEntity<KhaltiInitiateRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Khalti payment initiation failed: " + response.getBody());
        }

        Map<String, Object> body = response.getBody();
        KhaltiInitiateResponse res = new KhaltiInitiateResponse();
        res.setPaymentUrl(body.get("payment_url").toString());
        res.setMessage("Payment initiated successfully");
        return res;
    }

    /** Verify payment with Khalti */
    public Map<String, Object> verifyPayment(KhaltiVerifyRequest request) {
        String url = khaltiConfig.getBaseUrl() + "epayment/lookup/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        HttpEntity<KhaltiVerifyRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Khalti verification failed: " + response.getBody());
        }

        return response.getBody();
    }
}

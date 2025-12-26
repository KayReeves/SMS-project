package com.kritim_mind.sms_project.service;

import com.kritim_mind.sms_project.config.KhaltiConfig;
import com.kritim_mind.sms_project.dto.request.KhaltiInitiateRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import com.kritim_mind.sms_project.model.KhaltiPayment;
import com.kritim_mind.sms_project.repository.KhaltiPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KhaltiService {

    private final KhaltiConfig khaltiConfig;
    private final KhaltiPaymentRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public KhaltiService(KhaltiConfig khaltiConfig,
                         KhaltiPaymentRepository repository) {
        this.khaltiConfig = khaltiConfig;
        this.repository = repository;
    }


    public KhaltiInitiateResponse initiatePayment(KhaltiInitiateRequest request) {

        String url = khaltiConfig.getBaseUrl() + "epayment/initiate/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        HttpEntity<KhaltiInitiateRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("pidx") == null) {
            throw new RuntimeException("Failed to initiate Khalti payment");
        }

        String pidx = body.get("pidx").toString();


        KhaltiPayment payment = new KhaltiPayment();
        payment.setPidx(pidx);
        payment.setAmount(request.getAmount());
        payment.setStatus("Pending");
        payment.setPurchaseOrderId(request.getPurchase_order_id());
        payment.setPurchaseOrderName(request.getPurchase_order_name());
        payment.setPaidAt(null);

        repository.save(payment);

        KhaltiInitiateResponse res = new KhaltiInitiateResponse();
        res.setPidx(pidx);
        res.setPaymentUrl(body.get("payment_url").toString());
        res.setMessage("Payment initiated successfully");

        return res;
    }


    public KhaltiPayment verifyAndSavePayment(KhaltiVerifyRequest request) {

        String url = khaltiConfig.getBaseUrl() + "epayment/lookup/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        HttpEntity<KhaltiVerifyRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || !"Completed".equals(body.get("status"))) {
            throw new RuntimeException("Payment not completed or invalid response");
        }

        String pidx = body.get("pidx").toString();


        KhaltiPayment payment = repository.findByPidx(pidx)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));


        if ("Completed".equals(payment.getStatus())) {
            return payment;
        }

        payment.setStatus("Completed");
        payment.setTransactionId(
                body.get("transaction_id") != null
                        ? body.get("transaction_id").toString()
                        : null
        );

        Object paidAtObj = body.get("paid_at");
        if (paidAtObj != null) {
            try {
                payment.setPaidAt(LocalDateTime.parse(paidAtObj.toString()));
            } catch (Exception e) {
                payment.setPaidAt(LocalDateTime.now());
            }
        } else {
            payment.setPaidAt(LocalDateTime.now());
        }

        return repository.save(payment);
    }

    public List<KhaltiPayment> getAllPayments() {
        return repository.findAll();
    }
}

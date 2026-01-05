package com.kritim_mind.sms_project.service;

import com.kritim_mind.sms_project.config.KhaltiConfig;
import com.kritim_mind.sms_project.dto.request.KhaltiTopupRequest;
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

    /**
     * Initiates a Khalti payment and saves a record with INITIATED status
     */
    public KhaltiInitiateResponse initiatePayment(KhaltiTopupRequest request) {

        String url = khaltiConfig.getBaseUrl() + "epayment/initiate/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        int amountInPaisa = request.getAmount() * 100;
        String purchaseOrderId = "TOPUP_" + System.currentTimeMillis();

        Map<String, Object> payload = Map.of(
                "return_url", "http://localhost:3000/balance-report",
                "website_url", "http://localhost:3000",
                "amount", amountInPaisa,
                "purchase_order_id", purchaseOrderId,
                "purchase_order_name", "SMS Balance Top-up",
                "amount_breakdown", List.of(
                        Map.of("label", "Top-up Amount", "amount", amountInPaisa)
                ),
                "product_details", List.of(
                        Map.of(
                                "identity", purchaseOrderId,
                                "name", "SMS Balance Top-up",
                                "total_price", amountInPaisa,
                                "quantity", 1,
                                "unit_price", amountInPaisa
                        )
                ),
                "customer_info", Map.of(
                        "name", "kritimmind Technology",
                        "email", "Kritimind@gmail.com",
                        "phone", "9800000000"
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null || body.get("pidx") == null) {
            log.error("Khalti initiate failed: {}", body);
            throw new RuntimeException("Failed to initiate Khalti payment");
        }

        String pidx = body.get("pidx").toString();

        // Save in database with INITIATED status
        KhaltiPayment payment = new KhaltiPayment();
        payment.setPidx(pidx);
        payment.setAmount(amountInPaisa);
        payment.setStatus("INITIATED");  // <-- changed from COMPLETE
        payment.setPurchaseOrderId(purchaseOrderId);
        payment.setPurchaseOrderName("SMS Balance Top-up");

        repository.save(payment);
        log.info("Khalti payment initiated: {}", pidx);

        return new KhaltiInitiateResponse(
                body.get("payment_url").toString(),
                pidx,
                "Payment initiated successfully"
        );
    }

    /**
     * Verifies Khalti payment and updates status to Completed
     */
    public KhaltiPayment verifyAndSavePayment(KhaltiVerifyRequest request) {

        String url = khaltiConfig.getBaseUrl() + "epayment/lookup/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Key " + khaltiConfig.getSecretKey());

        HttpEntity<KhaltiVerifyRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null || !"Completed".equals(body.get("status"))) {
            log.error("Khalti verification failed: {}", body);
            throw new RuntimeException("Payment not completed or invalid response");
        }

        String pidx = body.get("pidx").toString();

        KhaltiPayment payment = repository.findByPidx(pidx)
                .orElseThrow(() -> new RuntimeException("Payment record not found for pidx: " + pidx));

        if ("Completed".equals(payment.getStatus())) {
            log.info("Payment already verified: {}", pidx);
            return payment;
        }

        payment.setStatus("Completed");
        payment.setTransactionId(body.get("transaction_id") != null
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

        KhaltiPayment savedPayment = repository.save(payment);
        log.info("Khalti payment verified and updated: {}", pidx);
        return savedPayment;
    }

    /**
     * Retrieves all Khalti payments
     */
    public List<KhaltiPayment> getAllPayments() {
        return repository.findAll();
    }
}

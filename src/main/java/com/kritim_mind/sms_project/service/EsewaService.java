package com.kritim_mind.sms_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kritim_mind.sms_project.config.EsewaConfig;
import com.kritim_mind.sms_project.dto.response.EsewaXmlResponse;
import com.kritim_mind.sms_project.model.EsewaPayment;
import com.kritim_mind.sms_project.repository.EsewaPaymentRepository;
import com.kritim_mind.sms_project.utils.HmacUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class EsewaService {

    private final EsewaConfig config;
    private final EsewaPaymentRepository repo;

    public EsewaService(EsewaConfig config, EsewaPaymentRepository repo) {
        this.config = config;
        this.repo = repo;
    }

    public Map<String, Object> initiatePayment(double amount) {

        String txnUuid = "TXN_" + System.currentTimeMillis();

        // Frontend already sends paisa (100 Rs × 100 = 10000 paisa)
        // So use amount directly, don't multiply again

        double tax = 0, service = 0, delivery = 0;
        double total = amount + tax + service + delivery;

        String signedFields = "total_amount,transaction_uuid,product_code";
        String message =
                "total_amount=" + total +
                        ",transaction_uuid=" + txnUuid +
                        ",product_code=" + config.getProductCode();

        String signature = HmacUtil.generateSignature(message, config.getSecretKey());

        Map<String, String> fields = new HashMap<>();
        fields.put("amount", String.valueOf(amount));
        fields.put("tax_amount", "0");
        fields.put("total_amount", String.valueOf(total));
        fields.put("transaction_uuid", txnUuid);
        fields.put("product_code", config.getProductCode());
        fields.put("product_service_charge", "0");
        fields.put("product_delivery_charge", "0");
        fields.put("success_url", "http://localhost:3000/balance-report");
        fields.put("failure_url", "http://localhost:3000/balance-report");
        fields.put("signed_field_names", signedFields);
        fields.put("signature", signature);

        EsewaPayment p = new EsewaPayment();
        p.setTransactionUuid(txnUuid);
        p.setTotalAmount(amount / 100);  // Convert paisa to rupees for storage
        p.setProductCode(config.getProductCode());
        p.setStatus("PENDING");  // Initial status
        repo.save(p);

        return Map.of(
                "actionUrl", config.getPaymentUrl(),
                "fields", fields
        );
    }

    // New method to handle base64 encoded data from frontend
    public EsewaXmlResponse verifyPaymentFromData(String base64Data) throws Exception {
        try {
            // Decode base64
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            String decodedString = new String(decodedBytes);

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(decodedString, Map.class);

            String txnUuid = data.get("transaction_uuid");
            String totalAmount = data.get("total_amount");
            String status = data.get("status");
            String transactionCode = data.get("transaction_code");

            // Verify the status
            if (!"COMPLETE".equals(status)) {
                throw new Exception("Payment not completed. Status: " + status);
            }

            // Call eSewa API to verify
            EsewaXmlResponse response = verifyPayment(txnUuid, totalAmount);

            // Update payment status in database
            EsewaPayment payment = repo.findByTransactionUuid(txnUuid)
                    .orElseThrow(() -> new Exception("Payment record not found for: " + txnUuid));

            payment.setStatus("COMPLETE");
            payment.setTransactionCode(transactionCode);
            repo.save(payment);

            return response;

        } catch (Exception e) {
            throw new Exception("Failed to verify payment: " + e.getMessage());
        }
    }

    public EsewaXmlResponse verifyPayment(String txnUuid, String totalAmount) throws Exception {
        try {
            String url = config.getStatusUrl() +
                    "?product_code=" + config.getProductCode() +
                    "&transaction_uuid=" + txnUuid +
                    "&total_amount=" + totalAmount;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Log to see what eSewa returns
            System.out.println("eSewa Response: " + response);

            // Parse as JSON (eSewa returns JSON, not XML)
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, EsewaXmlResponse.class);

        } catch (Exception e) {
            throw new Exception("eSewa verification failed: " + e.getMessage());
        }
    }
}
package com.kritim_mind.sms_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.config.EsewaConfig;
import com.kritim_mind.sms_project.dto.response.EsewaXmlResponse;
import com.kritim_mind.sms_project.model.EsewaPayment;
import com.kritim_mind.sms_project.repository.EsewaPaymentRepository;
import com.kritim_mind.sms_project.utils.HmacUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class EsewaService {

    private static final Logger logger = LoggerFactory.getLogger(EsewaService.class);

    private final EsewaConfig config;
    private final EsewaPaymentRepository repo;

    public EsewaService(EsewaConfig config, EsewaPaymentRepository repo) {
        this.config = config;
        this.repo = repo;
    }

    /**
     * Initiates a payment request and returns frontend form data
     */
    public Map<String, Object> initiatePayment(double amount) {

        String txnUuid = "TXN_" + System.currentTimeMillis();

        double tax = 0;
        double service = 0;
        double delivery = 0;
        double total = amount + tax + service + delivery;

        String signedFields = "total_amount,transaction_uuid,product_code";
        String message =
                "total_amount=" + total +
                        ",transaction_uuid=" + txnUuid +
                        ",product_code=" + config.getProductCode();

        String signature = HmacUtil.generateSignature(message, config.getSecretKey());

        // Form data for frontend
        Map<String, String> formData = new HashMap<>();
        formData.put("amount", String.valueOf(amount));
        formData.put("tax_amount", "0");
        formData.put("total_amount", String.valueOf(total));
        formData.put("transaction_uuid", txnUuid);
        formData.put("product_code", config.getProductCode());
        formData.put("product_service_charge", "0");
        formData.put("product_delivery_charge", "0");
        formData.put("success_url", "http://localhost:3000/balance-report");
        formData.put("failure_url", "http://localhost:3000/balance-report");
        formData.put("signed_field_names", signedFields);
        formData.put("signature", signature);

        // Save payment with INITIATED status
        EsewaPayment payment = new EsewaPayment();
        payment.setTransactionUuid(txnUuid);
        payment.setTotalAmount(total);
        payment.setProductCode(config.getProductCode());
        payment.setStatus("INITIATED");
        repo.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("api_endpoint", config.getPaymentUrl());
        response.put("formData", formData);

        logger.info("Payment initiated: {}", txnUuid);
        return response;
    }

    /**
     * Verifies payment from frontend Base64 data
     */
    public EsewaXmlResponse verifyPaymentFromData(String base64Data) throws Exception {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            String decodedString = new String(decodedBytes);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(decodedString, Map.class);

            String txnUuid = data.get("transaction_uuid");
            String totalAmount = data.get("total_amount");
            String status = data.get("status");
            String transactionCode = data.get("transaction_code");

            // Verify signature
            String signedFieldsStr = data.get("signed_field_names");
            String[] signedFields = signedFieldsStr.split(",");
            StringBuilder messageBuilder = new StringBuilder();
            for (String field : signedFields) {
                messageBuilder.append(field).append("=").append(data.get(field)).append(",");
            }
            String message = messageBuilder.substring(0, messageBuilder.length() - 1);
            String expectedSignature = HmacUtil.generateSignature(message, config.getSecretKey());
            if (!expectedSignature.equals(data.get("signature"))) {
                throw new Exception("Invalid signature");
            }

            // Verify with eSewa server
            EsewaXmlResponse response = verifyPayment(txnUuid, totalAmount);

            // Update payment record
            updatePaymentStatus(txnUuid, "COMPLETE", transactionCode);

            logger.info("Payment verified successfully: {}", txnUuid);
            return response;

        } catch (Exception e) {
            logger.error("Failed to verify payment: {}", e.getMessage());
            throw new Exception("Failed to verify payment: " + e.getMessage());
        }
    }

    /**
     * Calls eSewa server to confirm payment status
     */
    public EsewaXmlResponse verifyPayment(String txnUuid, String totalAmount) throws Exception {
        try {
            String url = config.getStatusUrl() +
                    "?product_code=" + config.getProductCode() +
                    "&transaction_uuid=" + txnUuid +
                    "&total_amount=" + totalAmount;

            RestTemplate restTemplate = new RestTemplate();
            String responseStr = restTemplate.getForObject(url, String.class);

            logger.info("eSewa response for {}: {}", txnUuid, responseStr);

            // Use ObjectMapper for JSON response
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseStr, EsewaXmlResponse.class);

        } catch (Exception e) {
            logger.error("eSewa verification failed for {}: {}", txnUuid, e.getMessage());
            throw new Exception("eSewa verification failed: " + e.getMessage());
        }
    }

    /**
     * Updates payment status in the database
     */
    public void updatePaymentStatus(String txnUuid, String status, String transactionCode) throws Exception {
        EsewaPayment payment = repo.findByTransactionUuid(txnUuid)
                .orElseThrow(() -> new Exception("Payment record not found for: " + txnUuid));
        payment.setStatus(status);
        payment.setTransactionCode(transactionCode);
        repo.save(payment);
        logger.info("Payment {} updated to status {}", txnUuid, status);
    }
}


package com.kritim_mind.sms_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        // Create flat form data map for frontend to use directly
        Map<String, String> formData = new HashMap<>();
        formData.put("amount", String.valueOf(amount));
        formData.put("tax_amount", "0");
        formData.put("total_amount", String.valueOf(total));
        formData.put("transaction_uuid", txnUuid);
        formData.put("product_code", config.getProductCode());
        formData.put("product_service_charge", "0");
        formData.put("product_delivery_charge", "0");
        formData.put("success_url", "http://localhost:3000/balance-report");  // Adjust to your actual success page
        formData.put("failure_url", "http://localhost:3000/balance-report");  // Adjust if you have a dedicated failure page
        formData.put("signed_field_names", signedFields);
        formData.put("signature", signature);

      //save in database
        EsewaPayment p = new EsewaPayment();
        p.setTransactionUuid(txnUuid);
        p.setTotalAmount(total);
        p.setProductCode(config.getProductCode());
        p.setStatus("PENDING");
        repo.save(p);


        Map<String, Object> response = new HashMap<>();
        response.put("api_endpoint", config.getPaymentUrl());  // e.g. https://rc-epay.esewa.com.np/api/epay/main/v2/form
        response.put("formData", formData);

        return response;
    }

    // Your existing verifyPaymentFromData method (unchanged)
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

            if (!"COMPLETE".equals(status)) {
                throw new Exception("Payment not completed. Status: " + status);
            }

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

            EsewaXmlResponse response = verifyPayment(txnUuid, totalAmount);

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

    // Your existing verifyPayment method (unchanged)
    public EsewaXmlResponse verifyPayment(String txnUuid, String totalAmount) throws Exception {
        try {
            String url = config.getStatusUrl() +
                    "?product_code=" + config.getProductCode() +
                    "&transaction_uuid=" + txnUuid +
                    "&total_amount=" + totalAmount;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            System.out.println("eSewa Response: " + response);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, EsewaXmlResponse.class);

        } catch (Exception e) {
            throw new Exception("eSewa verification failed: " + e.getMessage());
        }
    }
}
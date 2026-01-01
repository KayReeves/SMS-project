package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.KhaltiInitiateRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import com.kritim_mind.sms_project.model.KhaltiPayment;
import com.kritim_mind.sms_project.service.KhaltiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KhaltiController.class)
class KhaltiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KhaltiService khaltiService;

    @Autowired
    private ObjectMapper objectMapper;

    /* --------------------------------------------------
       Helpers
     -------------------------------------------------- */
    private KhaltiInitiateRequest mockInitiateRequest() {
        return new KhaltiInitiateRequest(
                10000,
                "ORDER-123",
                "SMS Package",
                "http://localhost:3000/return",
                "http://localhost:3000",
                List.of(new KhaltiInitiateRequest.AmountBreakdownItem(
                        "Base Price", 10000
                )),
                List.of(new KhaltiInitiateRequest.ProductDetail(
                        "PROD-1", "SMS Credits", 10000, 1, 10000
                )),
                new KhaltiInitiateRequest.CustomerInfo(
                        "John Doe", "john@test.com", "9800000000"
                )
        );
    }

    private KhaltiPayment mockPayment() {
        KhaltiPayment payment = new KhaltiPayment();
        payment.setId(1L);
        payment.setPidx("Pidx123");
        payment.setStatus("Completed");
        payment.setAmount(10000);
        payment.setTransactionId("TXN123");
        payment.setPurchaseOrderId("ORDER-123");
        payment.setPurchaseOrderName("SMS Package");
        payment.setEmail("john@test.com");
        payment.setMobile("9800000000");
        payment.setPaidAt(LocalDateTime.now());
        return payment;
    }

    /* --------------------------------------------------
       INITIATE PAYMENT
     -------------------------------------------------- */
    @Test
    void initiatePayment_success() throws Exception {
        KhaltiInitiateResponse response =
                new KhaltiInitiateResponse(
                        "https://khalti.com/pay",
                        "Pidx123",
                        "Initiated"
                );

        Mockito.when(khaltiService.initiatePayment(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/khalti/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockInitiateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_url").value("https://khalti.com/pay"))
                .andExpect(jsonPath("$.pidx").value("Pidx123"));
    }

    /* --------------------------------------------------
       INITIATE PAYMENT - VALIDATION FAILURE
     -------------------------------------------------- */
    @Test
    void initiatePayment_validationError() throws Exception {
        KhaltiInitiateRequest invalidRequest = new KhaltiInitiateRequest();

        mockMvc.perform(post("/api/khalti/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /* --------------------------------------------------
       VERIFY PAYMENT
     -------------------------------------------------- */
    @Test
    void verifyPayment_success() throws Exception {
        KhaltiVerifyRequest request = new KhaltiVerifyRequest("Pidx123");

        Mockito.when(khaltiService.verifyAndSavePayment(Mockito.any()))
                .thenReturn(mockPayment());

        mockMvc.perform(post("/api/khalti/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Payment verified and saved"))
                .andExpect(jsonPath("$.data.pidx")
                        .value("Pidx123"));
    }

    /* --------------------------------------------------
       VERIFY PAYMENT - VALIDATION FAILURE
     -------------------------------------------------- */
    @Test
    void verifyPayment_validationError() throws Exception {
        KhaltiVerifyRequest invalidRequest = new KhaltiVerifyRequest("");

        mockMvc.perform(post("/api/khalti/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /* --------------------------------------------------
       GET ALL PAYMENTS
     -------------------------------------------------- */
    @Test
    void getPayments_success() throws Exception {
        Mockito.when(khaltiService.getAllPayments())
                .thenReturn(List.of(mockPayment()));

        mockMvc.perform(get("/api/khalti/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pidx").value("Pidx123"));
    }

    /* --------------------------------------------------
       CALLBACK - COMPLETED
     -------------------------------------------------- */
    @Test
    void callback_completed() throws Exception {
        Mockito.when(khaltiService.verifyAndSavePayment(Mockito.any()))
                .thenReturn(mockPayment());

        mockMvc.perform(get("/api/khalti/callback")
                        .param("pidx", "Pidx123")
                        .param("status", "Completed"))
                .andExpect(status().isFound())
                .andExpect(header().string(
                        "Location",
                        "http://localhost:3000/balance-report?status=Completed&pidx=Pidx123"
                ));
    }

    /* --------------------------------------------------
       CALLBACK - FAILED (NO VERIFY CALL)
     -------------------------------------------------- */
    @Test
    void callback_failed() throws Exception {
        mockMvc.perform(get("/api/khalti/callback")
                        .param("pidx", "Pidx123")
                        .param("status", "Failed"))
                .andExpect(status().isFound())
                .andExpect(header().string(
                        "Location",
                        "http://localhost:3000/balance-report?status=Failed&pidx=Pidx123"
                ));

        Mockito.verify(khaltiService, Mockito.never())
                .verifyAndSavePayment(Mockito.any());
    }
}

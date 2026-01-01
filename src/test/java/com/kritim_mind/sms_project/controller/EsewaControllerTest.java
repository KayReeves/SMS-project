package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.response.EsewaXmlResponse;
import com.kritim_mind.sms_project.model.EsewaPayment;
import com.kritim_mind.sms_project.repository.EsewaPaymentRepository;
import com.kritim_mind.sms_project.service.EsewaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EsewaController.class)
class EsewaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EsewaService service;

    @MockBean
    private EsewaPaymentRepository esewaPaymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ------------------ INITIATE PAYMENT ------------------

    @Test
    void initiatePayment_success() throws Exception {

        Map<String, Object> response = Map.of(
                "payment_url", "https://esewa.com.np/pay",
                "amount", 500.0
        );

        Mockito.when(service.initiatePayment(500.0))
                .thenReturn(response);

        mockMvc.perform(post("/api/esewa/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("amount", 500)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment_url")
                        .value("https://esewa.com.np/pay"))
                .andExpect(jsonPath("$.amount").value(500.0));
    }

    // ------------------ VERIFY PAYMENT (POST) ------------------

    @Test
    void verifyPaymentFromFrontend_success() throws Exception {

        EsewaXmlResponse xmlResponse = new EsewaXmlResponse();
        xmlResponse.setStatus("COMPLETE");
        xmlResponse.setTransactionUuid("tx-123");

        Mockito.when(service.verifyPaymentFromData("signed-data"))
                .thenReturn(xmlResponse);

        mockMvc.perform(post("/api/esewa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("data", "signed-data")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETE"))
                .andExpect(jsonPath("$.transactionUuid").value("tx-123"));
    }

    // ------------------ VERIFY PAYMENT (GET) ------------------

    @Test
    void verifyPayment_success() throws Exception {

        EsewaXmlResponse xmlResponse = new EsewaXmlResponse();
        xmlResponse.setStatus("COMPLETE");
        xmlResponse.setTransactionUuid("tx-456");

        Mockito.when(service.verifyPayment("tx-456", "1000"))
                .thenReturn(xmlResponse);

        mockMvc.perform(get("/api/esewa/verify")
                        .param("transaction_uuid", "tx-456")
                        .param("total_amount", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETE"))
                .andExpect(jsonPath("$.transactionUuid").value("tx-456"));
    }

    // ------------------ GET TRANSACTIONS ------------------

    @Test
    void getTransactions_success() throws Exception {

        EsewaPayment payment1 = new EsewaPayment();
        payment1.setId(1L);
        payment1.setTransactionUuid("uuid-1");
        payment1.setTotalAmount(500.0);
        payment1.setStatus("COMPLETE");
        payment1.setCreatedAt(LocalDateTime.now());

        EsewaPayment payment2 = new EsewaPayment();
        payment2.setId(2L);
        payment2.setTransactionUuid("uuid-2");
        payment2.setTotalAmount(1000.0);
        payment2.setStatus("PENDING");
        payment2.setCreatedAt(LocalDateTime.now());

        Mockito.when(esewaPaymentRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(payment1, payment2));

        mockMvc.perform(get("/api/esewa/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionUuid").value("uuid-1"))
                .andExpect(jsonPath("$[1].status").value("PENDING"));
    }
}

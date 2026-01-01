package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.DeliveryReportRequest;
import com.kritim_mind.sms_project.dto.response.DeliveryReportResponse;
import com.kritim_mind.sms_project.model.DeliveryStatus;
import com.kritim_mind.sms_project.service.Interface.DeliveryReportService;
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

@WebMvcTest(DeliveryReportController.class)
class DeliveryReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long RECIPIENT_ID = 10L;
    private static final Long REPORT_ID = 5L;

    // ------------------ GET REPORTS BY RECIPIENT ------------------

    @Test
    void getReportsByRecipientId_success() throws Exception {

        DeliveryReportResponse report1 = new DeliveryReportResponse();
        report1.setId(1L);
        report1.setMessageRecipientId(RECIPIENT_ID);
        report1.setStatus(DeliveryStatus.DELIVERED);
        report1.setDescription("Delivered successfully");
        report1.setCreatedAt(LocalDateTime.now());

        DeliveryReportResponse report2 = new DeliveryReportResponse();
        report2.setId(2L);
        report2.setMessageRecipientId(RECIPIENT_ID);
        report2.setStatus(DeliveryStatus.FAILED);
        report2.setDescription("Network issue");
        report2.setCreatedAt(LocalDateTime.now());

        Mockito.when(reportService.getReportsByRecipientId(RECIPIENT_ID))
                .thenReturn(List.of(report1, report2));

        mockMvc.perform(get("/api/message_recipients/{recipient_id}/delivery_reports", RECIPIENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].status").value("DELIVERED"))
                .andExpect(jsonPath("$.data[1].status").value("FAILED"));
    }

    // ------------------ GET REPORT BY ID ------------------

    @Test
    void getReportById_success() throws Exception {

        DeliveryReportResponse response = new DeliveryReportResponse();
        response.setId(REPORT_ID);
        response.setMessageRecipientId(RECIPIENT_ID);
        response.setStatus(DeliveryStatus.PENDING);
        response.setDescription("Awaiting delivery");

        Mockito.when(reportService.getReportById(REPORT_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/delivery_reports/{report_id}", REPORT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(REPORT_ID))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    // ------------------ CREATE DELIVERY REPORT ------------------

    @Test
    void createDeliveryReport_success() throws Exception {

        DeliveryReportRequest request = new DeliveryReportRequest();
        request.setStatus(DeliveryStatus.DELIVERED);
        request.setDescription("Delivered successfully");

        DeliveryReportResponse response = new DeliveryReportResponse();
        response.setId(3L);
        response.setMessageRecipientId(RECIPIENT_ID);
        response.setStatus(DeliveryStatus.DELIVERED);
        response.setDescription("Delivered successfully");

        Mockito.when(reportService.createDeliveryReport(
                        Mockito.eq(RECIPIENT_ID),
                        Mockito.any(DeliveryReportRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/message_recipients/{recipient_id}/delivery_reports", RECIPIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Delivery report created successfully"))
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    // ------------------ UPDATE DELIVERY REPORT ------------------

    @Test
    void updateDeliveryReport_success() throws Exception {

        DeliveryReportRequest request = new DeliveryReportRequest();
        request.setStatus(DeliveryStatus.FAILED);
        request.setDescription("Carrier rejected message");

        DeliveryReportResponse response = new DeliveryReportResponse();
        response.setId(REPORT_ID);
        response.setStatus(DeliveryStatus.FAILED);
        response.setDescription("Carrier rejected message");

        Mockito.when(reportService.updateDeliveryReport(
                        Mockito.eq(REPORT_ID),
                        Mockito.any(DeliveryReportRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/delivery_reports/{report_id}", REPORT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Delivery report updated successfully"))
                .andExpect(jsonPath("$.data.status").value("FAILED"));
    }

    // ------------------ DELETE DELIVERY REPORT ------------------

    @Test
    void deleteDeliveryReport_success() throws Exception {

        Mockito.doNothing()
                .when(reportService).deleteDeliveryReport(REPORT_ID);

        mockMvc.perform(delete("/api/delivery_reports/{report_id}", REPORT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Delivery report deleted successfully"));
    }
}

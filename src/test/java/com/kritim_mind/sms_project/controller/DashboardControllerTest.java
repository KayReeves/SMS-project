package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.response.DailyReportData;
import com.kritim_mind.sms_project.dto.response.DashboardResponse;
import com.kritim_mind.sms_project.service.Interface.DashboardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long ADMIN_ID = 1L;

    // ------------------ DASHBOARD SUMMARY ------------------

    @Test
    void getDashboardSummary_success() throws Exception {

        DashboardResponse response = DashboardResponse.builder()
                .totalSmsSentYesterday(120L)
                .totalTransactions(50L)
                .totalSmsLength(360L)
                .remainingBalance(800)
                .contactsCount(200L)
                .groupsCount(10L)
                .build();

        Mockito.when(dashboardService.getDashboardSummary(ADMIN_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/dashboard")
                        .param("admin_id", ADMIN_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSmsSentYesterday").value(120))
                .andExpect(jsonPath("$.data.remainingBalance").value(800))
                .andExpect(jsonPath("$.data.contactsCount").value(200));
    }

    // ------------------ DAILY REPORT ------------------

    @Test
    void getDailyReport_success() throws Exception {

        List<DailyReportData> reports = List.of(
                new DailyReportData(LocalDate.of(2025, 1, 1), 100L, 5L, 2L),
                new DailyReportData(LocalDate.of(2025, 1, 2), 120L, 3L, 1L)
        );

        Mockito.when(dashboardService.getDailyReport(
                        Mockito.eq(ADMIN_ID),
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class)))
                .thenReturn(reports);

        mockMvc.perform(get("/api/reports/daily")
                        .param("admin_id", ADMIN_ID.toString())
                        .param("start_date", "2025-01-01")
                        .param("end_date", "2025-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].smsCount").value(100))
                .andExpect(jsonPath("$.data[0].pendingSmsCount").value(5))
                .andExpect(jsonPath("$.data[0].failedSmsCount").value(2));
    }

    // ------------------ MONTHLY REPORT ------------------

    @Test
    void getMonthlyReport_success() throws Exception {

        List<DailyReportData> reports = List.of(
                new DailyReportData(LocalDate.of(2025, 1, 1), 3000L, 50L, 10L),
                new DailyReportData(LocalDate.of(2025, 2, 1), 2800L, 40L, 12L)
        );

        Mockito.when(dashboardService.getMonthlyReport(ADMIN_ID, 2025))
                .thenReturn(reports);

        mockMvc.perform(get("/api/reports/monthly")
                        .param("admin_id", ADMIN_ID.toString())
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[1].smsCount").value(2800))
                .andExpect(jsonPath("$.data[1].failedSmsCount").value(12));
    }
}

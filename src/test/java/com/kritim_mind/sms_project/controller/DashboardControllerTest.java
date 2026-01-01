package com.kritim_mind.sms_project.controller;

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

    /* ------------------------------------------
       Helper methods
    ------------------------------------------ */
    private DashboardResponse mockDashboardResponse() {
        return DashboardResponse.builder()
                .totalSmsSentYesterday(120L)
                .totalTransactions(45L)
                .totalSmsLength(560L)
                .remainingBalance(980)
                .contactsCount(150L)
                .groupsCount(12L)
                .build();
    }

    private DailyReportData mockDailyReport() {
        return new DailyReportData(
                LocalDate.now(),
                200L,
                180L,
                20L
        );
    }

    /* ------------------------------------------
       GET /api/dashboard
    ------------------------------------------ */
    @Test
    void getDashboardSummary_success() throws Exception {
        Mockito.when(dashboardService.getDashboardSummary(1L))
                .thenReturn(mockDashboardResponse());

        mockMvc.perform(get("/api/dashboard")
                        .param("admin_id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSmsSentYesterday").value(120))
                .andExpect(jsonPath("$.data.remainingBalance").value(980))
                .andExpect(jsonPath("$.data.groupsCount").value(12));
    }

    /* ------------------------------------------
       GET /api/reports/daily
    ------------------------------------------ */
    @Test
    void getDailyReport_success() throws Exception {
        Mockito.when(dashboardService.getDailyReport(
                        Mockito.eq(1L),
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class)))
                .thenReturn(List.of(mockDailyReport()));

        mockMvc.perform(get("/api/reports/daily")
                        .param("admin_id", "1")
                        .param("start_date", "2024-01-01")
                        .param("end_date", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].smsCount").value(200))
                .andExpect(jsonPath("$.data[0].totalDeliveredMessage").value(180))
                .andExpect(jsonPath("$.data[0].totalFailedMessage").value(20));
    }

    /* ------------------------------------------
       GET /api/reports/monthly
    ------------------------------------------ */
    @Test
    void getMonthlyReport_success() throws Exception {
        Mockito.when(dashboardService.getMonthlyReport(1L, 2024))
                .thenReturn(List.of(mockDailyReport()));

        mockMvc.perform(get("/api/reports/monthly")
                        .param("admin_id", "1")
                        .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].smsCount").value(200));
    }
}

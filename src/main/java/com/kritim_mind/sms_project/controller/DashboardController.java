package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.DailyReportData;
import com.kritim_mind.sms_project.dto.response.DashboardResponse;
import com.kritim_mind.sms_project.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardSummary(
            @RequestParam Long admin_id) {
        DashboardResponse response = dashboardService.getDashboardSummary(admin_id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reports/daily")
    public ResponseEntity<ApiResponse<List<DailyReportData>>> getDailyReport(
            @RequestParam Long admin_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date) {
        List<DailyReportData> data = dashboardService.getDailyReport(admin_id, start_date, end_date);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/reports/monthly")
    public ResponseEntity<ApiResponse<List<DailyReportData>>> getMonthlyReport(
            @RequestParam Long admin_id,
            @RequestParam int year) {
        List<DailyReportData> data = dashboardService.getMonthlyReport(admin_id, year);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

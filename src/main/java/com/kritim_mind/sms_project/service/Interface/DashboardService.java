package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.response.DailyReportData;
import com.kritim_mind.sms_project.dto.response.DashboardResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardResponse getDashboardSummary(Long adminId);

    List<DailyReportData> getDailyReport(Long adminId, LocalDate startDate, LocalDate endDate);

    List<DailyReportData> getMonthlyReport(Long adminId, int year);
}

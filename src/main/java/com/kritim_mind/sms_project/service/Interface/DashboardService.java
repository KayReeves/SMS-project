package com.kritim_mind.sms_project.service.Interface;


import com.kritim_mind.sms_project.dto.response.DashboardResponse;
import com.kritim_mind.sms_project.dto.response.DeliveryReportSummary;


import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardResponse getDashboardSummary(Long adminId);

    List<DeliveryReportSummary> getDailyReport(Long adminId, LocalDate startDate, LocalDate endDate);

    List<DeliveryReportSummary> getMonthlyReport(Long adminId, int year);
}

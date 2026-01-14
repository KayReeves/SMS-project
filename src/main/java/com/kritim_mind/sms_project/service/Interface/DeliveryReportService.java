package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.request.DeliveryReportRequest;
import com.kritim_mind.sms_project.dto.response.DeliveryReportResponse;
import com.kritim_mind.sms_project.dto.response.DeliveryReportSummary;

import java.util.List;

public interface DeliveryReportService {

    // NEW: Get all reports
    List<DeliveryReportResponse> getAllDeliveryReports();

    List<DeliveryReportResponse> getReportsByRecipientId(Long recipientId);

    DeliveryReportResponse getReportById(Long id);

    DeliveryReportResponse createDeliveryReport(Long recipientId, DeliveryReportRequest request);

    DeliveryReportResponse updateDeliveryReport(Long id, DeliveryReportRequest request);

    void deleteDeliveryReport(Long id);

    DeliveryReportSummary getAllTimeDeliverySummary(Long adminId);
}
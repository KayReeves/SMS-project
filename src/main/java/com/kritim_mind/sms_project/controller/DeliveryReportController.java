package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.DeliveryReportRequest;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.DeliveryReportResponse;
import com.kritim_mind.sms_project.service.DeliveryReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryReportController {

    private final DeliveryReportService reportService;

    @GetMapping("/message_recipients/{recipient_id}/delivery_reports")
    public ResponseEntity<ApiResponse<List<DeliveryReportResponse>>> getReportsByRecipientId(
            @PathVariable("recipient_id") Long recipientId) {
        List<DeliveryReportResponse> reports = reportService.getReportsByRecipientId(recipientId);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/delivery_reports/{report_id}")
    public ResponseEntity<ApiResponse<DeliveryReportResponse>> getReportById(
            @PathVariable("report_id") Long reportId) {
        DeliveryReportResponse report = reportService.getReportById(reportId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @PostMapping("/message_recipients/{recipient_id}/delivery_reports")
    public ResponseEntity<ApiResponse<DeliveryReportResponse>> createDeliveryReport(
            @PathVariable("recipient_id") Long recipientId,
            @RequestBody DeliveryReportRequest request) {
        DeliveryReportResponse report = reportService.createDeliveryReport(recipientId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery report created successfully", report));
    }

    @PutMapping("/delivery_reports/{report_id}")
    public ResponseEntity<ApiResponse<DeliveryReportResponse>> updateDeliveryReport(
            @PathVariable("report_id") Long reportId,
            @RequestBody DeliveryReportRequest request) {
        DeliveryReportResponse report = reportService.updateDeliveryReport(reportId, request);
        return ResponseEntity.ok(ApiResponse.success("Delivery report updated successfully", report));
    }

    @DeleteMapping("/delivery_reports/{report_id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeliveryReport(
            @PathVariable("report_id") Long reportId) {
        reportService.deleteDeliveryReport(reportId);
        return ResponseEntity.ok(ApiResponse.success("Delivery report deleted successfully", null));
    }
}

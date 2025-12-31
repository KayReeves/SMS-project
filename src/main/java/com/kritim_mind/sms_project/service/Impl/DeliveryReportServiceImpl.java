package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.DeliveryReportRequest;
import com.kritim_mind.sms_project.dto.response.DeliveryReportResponse;
import com.kritim_mind.sms_project.exception.ResourceNotFoundException;
import com.kritim_mind.sms_project.model.DeliveryReport;
import com.kritim_mind.sms_project.model.DeliveryStatus;
import com.kritim_mind.sms_project.model.MessageRecipient;
import com.kritim_mind.sms_project.model.MessageStatus;
import com.kritim_mind.sms_project.repository.DeliveryReportRepository;
import com.kritim_mind.sms_project.repository.MessageRecipientRepository;
import com.kritim_mind.sms_project.service.Interface.DeliveryReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryReportServiceImpl implements DeliveryReportService {

    private final DeliveryReportRepository reportRepository;
    private final MessageRecipientRepository recipientRepository;

    @Override
    @Transactional
    public List<DeliveryReportResponse> getReportsByRecipientId(Long recipientId) {
        List<DeliveryReport> reports = reportRepository.findByMessageRecipientId(recipientId);

        return reports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeliveryReportResponse getReportById(Long id) {
        DeliveryReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery report not found"));

        return mapToResponse(report);
    }

    @Override
    @Transactional
    public DeliveryReportResponse createDeliveryReport(Long recipientId,
                                                       DeliveryReportRequest request) {

        log.info("Creating delivery report for recipient ID: {}", recipientId);

        MessageRecipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        DeliveryReport report = DeliveryReport.builder()
                .messageRecipient(recipient)
                .status(request.getStatus())
                .description(request.getDescription())
                .build();

        report = reportRepository.save(report);

        // Update recipient message status
        if (request.getStatus() == DeliveryStatus.DELIVERED) {
            recipient.setStatus(MessageStatus.DELIVERED);
            recipient.setDeliveredAt(LocalDateTime.now());
        } else if (request.getStatus() == DeliveryStatus.FAILED) {
            recipient.setStatus(MessageStatus.FAILED);
            recipient.setFailedAt(LocalDateTime.now());
        }

        recipientRepository.save(recipient);

        log.info("Delivery report created with ID: {}", report.getId());

        return mapToResponse(report);
    }

    @Override
    @Transactional
    public DeliveryReportResponse updateDeliveryReport(Long id, DeliveryReportRequest request) {
        log.info("Updating delivery report ID: {}", id);

        DeliveryReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery report not found"));

        report.setStatus(request.getStatus());
        report.setDescription(request.getDescription());

        report = reportRepository.save(report);

        log.info("Delivery report updated successfully");

        return mapToResponse(report);
    }

    @Override
    @Transactional
    public void deleteDeliveryReport(Long id) {
        log.info("Deleting delivery report ID: {}", id);

        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery report not found");
        }

        reportRepository.deleteById(id);

        log.info("Delivery report deleted successfully");
    }

    // ------------------------
    // Mapping helper
    // ------------------------
    private DeliveryReportResponse mapToResponse(DeliveryReport report) {
        DeliveryReportResponse response = new DeliveryReportResponse();
        response.setId(report.getId());
        response.setMessageRecipientId(report.getMessageRecipient().getId());
        response.setStatus(report.getStatus());
        response.setDescription(report.getDescription());
        response.setCreatedAt(report.getCreatedAt());
        return response;
    }
}

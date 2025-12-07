package com.kritim_mind.sms_project.dto.response;

import com.kritim_mind.sms_project.model.DeliveryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryReportResponse {
    private Long id;
    private Long messageRecipientId;
    private DeliveryStatus status;
    private String description;
    private LocalDateTime createdAt;
}

package com.kritim_mind.sms_project.dto.request;

import com.kritim_mind.sms_project.model.DeliveryStatus;
import lombok.Data;

@Data
public class DeliveryReportRequest {
    private DeliveryStatus status;
    private String description;
}

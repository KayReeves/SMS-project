package com.kritim_mind.sms_project.dto.request;

import com.kritim_mind.sms_project.model.MessageStatus;
import lombok.Data;

@Data
public class RecipientStatusUpdateRequest {
    private MessageStatus status;
}

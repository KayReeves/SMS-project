package com.kritim_mind.sms_project.dto.response;

import com.kritim_mind.sms_project.model.MessageStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecipientResponse {
    private Long id;
    private Long messageId;
    private String phoneNo;
    private Long contactId;
    private String contactName;
    private Long groupId;
    private String groupName;
    private MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime failedAt;
    private LocalDateTime createdAt;
}

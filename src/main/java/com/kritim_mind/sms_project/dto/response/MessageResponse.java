package com.kritim_mind.sms_project.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private String content;
    private Integer totalSmsParts;
    private Integer recipientCount;
    private LocalDateTime createdAt;
}

package com.kritim_mind.sms_project.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class MessageRequest {
    private Long senderId;
    private String content;
    private List<Long> recipientContactIds;
    private List<Long> recipientGroupIds;
    private List<String> recipientNumbers;
}

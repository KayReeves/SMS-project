package com.kritim_mind.sms_project.dto.request;

import lombok.Data;

@Data
public class RecipientRequest {
    private String phoneNo;

    private Long contactId;
    private Long groupId;
}

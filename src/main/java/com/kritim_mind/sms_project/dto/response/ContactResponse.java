package com.kritim_mind.sms_project.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContactResponse {
    private Long id;
    private String name;
    private String phoneNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

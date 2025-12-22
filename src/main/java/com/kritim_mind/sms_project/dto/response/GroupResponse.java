package com.kritim_mind.sms_project.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private Integer contactCount;
    private List<ContactResponse> contacts;
    private String originalFileName;
    private String contentType;
    private Long fileSizeBytes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

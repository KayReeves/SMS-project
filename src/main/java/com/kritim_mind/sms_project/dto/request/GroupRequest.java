package com.kritim_mind.sms_project.dto.request;

import lombok.Data;

@Data
public class GroupRequest {
    private String name;
    private String description;
    private String originalFileName;
    private String contentType;
    private long fileSizeBytes;
}

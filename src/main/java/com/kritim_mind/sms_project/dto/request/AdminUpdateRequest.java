package com.kritim_mind.sms_project.dto.request;

import lombok.Data;

@Data
public class AdminUpdateRequest {
    private String username;
    private String email;
    private String currentPassword;
    private String newPassword;
}

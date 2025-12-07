package com.kritim_mind.sms_project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String type;
    private AdminResponse admin;
}

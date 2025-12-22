package com.kritim_mind.sms_project.dto.response;

import lombok.Data;

@Data
public class KhaltiInitiateResponse {
    private String paymentUrl;
    private String message;
}

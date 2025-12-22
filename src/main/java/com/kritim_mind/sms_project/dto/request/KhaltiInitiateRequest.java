package com.kritim_mind.sms_project.dto.request;

import lombok.Data;

@Data
public class KhaltiInitiateRequest {
    private long amount;
    private String purchaseOrderId;
    private String purchaseOrderName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String returnUrl; // frontend redirect URL
}

package com.kritim_mind.sms_project.dto.response;

import lombok.Data;

@Data
public class EsewaStatusResponse {
    private String status;
    private String referenceId;
    private String message;

    public static EsewaStatusResponse success(String refId) {
        EsewaStatusResponse r = new EsewaStatusResponse();
        r.status = "COMPLETE";
        r.referenceId = refId;
        r.message = "Payment verified successfully";
        return r;
    }

    public static EsewaStatusResponse failed(String msg) {
        EsewaStatusResponse r = new EsewaStatusResponse();
        r.status = "FAILED";
        r.message = msg;
        return r;
    }
}

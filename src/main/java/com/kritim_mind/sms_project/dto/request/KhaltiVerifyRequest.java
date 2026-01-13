package com.kritim_mind.sms_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
public class KhaltiVerifyRequest {
    private Long id;
    @NotBlank(message = "pidx is required")
    private String pidx;

    public KhaltiVerifyRequest() {
    }

    public KhaltiVerifyRequest(String pidx) {
        this.pidx = pidx;
    }

    public KhaltiVerifyRequest(Long id, String pidx) {
        this.id = id;
        this.pidx = pidx;
    }
}
package com.kritim_mind.sms_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhaltiVerifyRequest {

    @NotBlank(message = "pidx is required")
    private String pidx;
}
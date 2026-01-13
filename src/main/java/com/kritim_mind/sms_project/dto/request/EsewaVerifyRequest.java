package com.kritim_mind.sms_project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsewaVerifyRequest {

    @NotBlank(message = "transactionUuid is required")
    private String transactionUuid;

    @NotNull(message = "adminId is required")
    private Long adminId; // admin/user id
}

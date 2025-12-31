package com.kritim_mind.sms_project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KhaltiTopupRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 100, message = "Minimum amount is Rs. 100")
    private Integer amount;
}

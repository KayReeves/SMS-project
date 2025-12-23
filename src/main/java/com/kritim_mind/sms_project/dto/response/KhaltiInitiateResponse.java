package com.kritim_mind.sms_project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhaltiInitiateResponse {

    @JsonProperty("payment_url")
    private String paymentUrl;

    private String pidx;

    private String message;
}
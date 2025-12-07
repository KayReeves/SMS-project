package com.kritim_mind.sms_project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceResponse {
    private Integer totalCredits;
    private Integer usedCredits;
    private Integer remainingCredits;
}

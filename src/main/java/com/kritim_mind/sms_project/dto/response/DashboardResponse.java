package com.kritim_mind.sms_project.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private Long totalSmsSentYesterday;
    private Long totalTransactions;
    private Long totalSmsLength;
    private Integer remainingBalance;
    private Long contactsCount;
    private Long groupsCount;
}

package com.kritim_mind.sms_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyReportData {
    private LocalDate date;
    private Long smsCount;
    private long totalDeliveredMessage;
    private long  totalFailedMessage;

    public DailyReportData(LocalDate localDate, Long total) {
    }
}

package com.kritim_mind.sms_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyReportData {

    private Long smsCount;
    private  int totalSmsCount;
    private int pendingCount;
    private int  failedCount;

    private LocalDate date;

    public DailyReportData(LocalDate of, long l) {
    }
}

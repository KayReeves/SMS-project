package com.kritim_mind.sms_project.dto.response;


import java.time.LocalDate;

public record DeliveryReportSummary(
        Long totalSmsSent,
        Long delivered,
        Long failed,
        Long pending
) {}


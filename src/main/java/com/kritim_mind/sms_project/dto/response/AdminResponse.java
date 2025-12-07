package com.kritim_mind.sms_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class AdminResponse {
    private Long id;
    private String username;
    private Integer totalSmsCredits;
    private Integer usedSmsCredits;
    private Integer remainingCredits;
    private LocalDateTime createdAt;
}

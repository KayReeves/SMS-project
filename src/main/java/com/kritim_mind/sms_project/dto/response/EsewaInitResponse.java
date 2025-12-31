package com.kritim_mind.sms_project.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class EsewaInitResponse {
    private String actionUrl;
    private Map<String, String> fields;
}

package com.kritim_mind.sms_project.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddContactToGroupRequest {
    private List<Long> contactIds;
}

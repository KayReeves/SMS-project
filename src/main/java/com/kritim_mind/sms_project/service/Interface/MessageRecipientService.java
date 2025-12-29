package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.response.RecipientResponse;
import com.kritim_mind.sms_project.model.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRecipientService {
    List<RecipientResponse> getRecipientsByMessageId(Long messageId);

    Page<RecipientResponse> getRecipientsByMessageId(Long messageId, Pageable pageable);

    RecipientResponse getRecipientById(Long id);

}

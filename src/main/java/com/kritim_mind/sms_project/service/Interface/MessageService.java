package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.request.MessageRequest;
import com.kritim_mind.sms_project.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface MessageService {
    Page<MessageResponse> getAllMessages(Long senderId, LocalDateTime from,
                                         LocalDateTime to, Pageable pageable);

    MessageResponse getMessageById(Long id);

    MessageResponse createMessage(MessageRequest request);

    MessageResponse updateMessage(Long id, String content);

    void deleteMessage(Long id);
}

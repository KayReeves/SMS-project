package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Message;
import com.kritim_mind.sms_project.model.MessageRecipient;
import com.kritim_mind.sms_project.model.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Long> {
    List<MessageRecipient> findByMessageId(Long messageId);

    Page<MessageRecipient> findByMessageId(Long messageId, Pageable pageable);

    List<MessageRecipient> findByStatus(MessageStatus status);

    @Query("SELECT mr FROM MessageRecipient mr WHERE mr.message.id = :messageId AND mr.status = :status")
    List<MessageRecipient> findByMessageIdAndStatus(Long messageId, MessageStatus status);
}

package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Message;
import com.kritim_mind.sms_project.model.MessageRecipient;
import com.kritim_mind.sms_project.model.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRecipientRepository extends JpaRepository<MessageRecipient, Long> {
    List<MessageRecipient> findByMessageId(Long messageId);

    Page<MessageRecipient> findByMessageId(Long messageId, Pageable pageable);

    List<MessageRecipient> findByStatus(MessageStatus status);

    @Query("SELECT mr FROM MessageRecipient mr WHERE mr.message.id = :messageId AND mr.status = :status")
    List<MessageRecipient> findByMessageIdAndStatus(Long messageId, MessageStatus status);


    @Modifying
    @Query("DELETE FROM MessageRecipient mr WHERE mr.contact.id = :contactId")
    void deleteByContactId(@Param("contactId") Long contactId);


}

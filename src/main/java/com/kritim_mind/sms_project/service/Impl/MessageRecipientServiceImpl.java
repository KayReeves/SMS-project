package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.response.RecipientResponse;
import com.kritim_mind.sms_project.model.MessageRecipient;
import com.kritim_mind.sms_project.model.MessageStatus;
import com.kritim_mind.sms_project.repository.MessageRecipientRepository;
import com.kritim_mind.sms_project.service.Interface.MessageRecipientService;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageRecipientServiceImpl implements MessageRecipientService {

    private final MessageRecipientRepository recipientRepository;

    @Override
    @Transactional
    public List<RecipientResponse> getRecipientsByMessageId(Long messageId) {
        List<MessageRecipient> recipients = recipientRepository.findByMessageId(messageId);
        return recipients.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<RecipientResponse> getRecipientsByMessageId(Long messageId, Pageable pageable) {
        Page<MessageRecipient> recipients = recipientRepository.findByMessageId(messageId, pageable);
        return recipients.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public RecipientResponse getRecipientById(Long id) {
        MessageRecipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));
        return mapToResponse(recipient);
    }

    @Override
    @Transactional
    public RecipientResponse updateRecipientStatus(Long id, MessageStatus status) {
        log.info("Updating recipient ID: {} to status: {}", id, status);

        MessageRecipient recipient = recipientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        recipient.setStatus(status);

        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case SENT -> recipient.setSentAt(now);
            case DELIVERED -> {
                recipient.setDeliveredAt(now);
                if (recipient.getSentAt() == null) recipient.setSentAt(now);
            }
            case FAILED -> recipient.setFailedAt(now);
        }

        recipient = recipientRepository.save(recipient);
        log.info("Recipient status updated successfully");

        return mapToResponse(recipient);
    }

    @Override
    @Transactional
    public void deleteRecipient(Long id) {
        log.info("Deleting recipient ID: {}", id);

        if (!recipientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipient not found");
        }

        recipientRepository.deleteById(id);
        log.info("Recipient deleted successfully");
    }

    // ------------------------
    // Mapping helper
    // ------------------------
    private RecipientResponse mapToResponse(MessageRecipient recipient) {
        RecipientResponse response = new RecipientResponse();
        response.setId(recipient.getId());
        response.setMessageId(recipient.getMessage().getId());
        response.setPhoneNo(recipient.getPhoneNo());

        if (recipient.getContact() != null) {
            response.setContactId(recipient.getContact().getId());
            response.setContactName(recipient.getContact().getName());
        }

        if (recipient.getGroup() != null) {
            response.setGroupId(recipient.getGroup().getId());
            response.setGroupName(recipient.getGroup().getName());
        }

        response.setStatus(recipient.getStatus());
        response.setSentAt(recipient.getSentAt());
        response.setDeliveredAt(recipient.getDeliveredAt());
        response.setFailedAt(recipient.getFailedAt());
        response.setCreatedAt(recipient.getCreatedAt());

        return response;
    }
}

package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.MessageRequest;
import com.kritim_mind.sms_project.dto.response.MessageResponse;
import com.kritim_mind.sms_project.exception.InsufficientBalanceException;
import com.kritim_mind.sms_project.model.*;
import com.kritim_mind.sms_project.repository.*;
import com.kritim_mind.sms_project.service.Interface.MessageService;
import com.kritim_mind.sms_project.service.Interface.SMSProviderService;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import com.kritim_mind.sms_project.utils.SMSCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final AdminRepository adminRepository;
    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;
    private final MessageRecipientRepository recipientRepository;
    private final SMSProviderService smsProviderService;

    @Override
    @Transactional
    public Page<MessageResponse> getAllMessages(Long senderId, LocalDateTime from,
                                                LocalDateTime to, Pageable pageable) {
        Page<Message> messages = messageRepository.findByFilters(senderId, from, to, pageable);
        return messages.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public MessageResponse getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        return mapToResponse(message);
    }

    @Override
    @Transactional
    public MessageResponse createMessage(MessageRequest request) {
        log.info("Creating message from sender ID: {}", request.getSenderId());

        Admin sender = adminRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));


        Set<String> phoneNumbers = new HashSet<>();
        Map<String, RecipientInfo> recipientInfoMap = new HashMap<>();

        if (request.getRecipientNumbers() != null) {
            request.getRecipientNumbers().forEach(phone -> {
                phoneNumbers.add(phone);
                recipientInfoMap.put(phone, new RecipientInfo(phone, null, null));
            });
        }

        if (request.getRecipientContactIds() != null) {
            for (Long contactId : request.getRecipientContactIds()) {
                Contact contact = contactRepository.findByIdAndIsDeleted(contactId, false)
                        .orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + contactId));
                phoneNumbers.add(contact.getPhoneNo());
                recipientInfoMap.put(contact.getPhoneNo(),
                        new RecipientInfo(contact.getPhoneNo(), contactId, null));
            }
        }

        if (request.getRecipientGroupIds() != null) {
            for (Long groupId : request.getRecipientGroupIds()) {
                Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                        .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupId));

                for (Contact contact : group.getContacts()) {
                    if (!contact.getIsDeleted()) {
                        phoneNumbers.add(contact.getPhoneNo());
                        recipientInfoMap.put(contact.getPhoneNo(),
                                new RecipientInfo(contact.getPhoneNo(), contact.getId(), groupId));
                    }
                }
            }
        }



        Message message = Message.builder()
                .sender(sender)
                .content(request.getContent())
                .build();

        message = messageRepository.save(message);

        for (String phone : phoneNumbers) {
            RecipientInfo info = recipientInfoMap.get(phone);
            MessageRecipient recipient = MessageRecipient.builder()
                    .message(message)
                    .phoneNo(phone)
                    .contact(info.contactId != null ?
                            contactRepository.findById(info.contactId).orElse(null) : null)
                    .group(info.groupId != null ?
                            groupRepository.findById(info.groupId).orElse(null) : null)
                    .status(MessageStatus.PENDING)
                    .build();

            message.getRecipients().add(recipient);
        }

        message = messageRepository.save(message);


        adminRepository.save(sender);

        smsProviderService.sendBulkSms(message);


        return mapToResponse(message);
    }

    @Override
    @Transactional
    public MessageResponse updateMessage(Long id, String content) {
        log.info("Updating message ID: {}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        message.setContent(content);
        message.setTotalSmsParts(SMSCalculator.calculateSmsParts(content) *
                message.getRecipients().size());

        message = messageRepository.save(message);
        log.info("Message updated successfully");

        return mapToResponse(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        log.info("Deleting message ID: {}", id);

        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message not found");
        }

        messageRepository.deleteById(id);
        log.info("Message deleted successfully");
    }

    // ------------------------
    // Mapping helper
    // ------------------------
    private MessageResponse mapToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderUsername(message.getSender().getUsername());
        response.setContent(message.getContent());
        response.setTotalSmsParts(message.getTotalSmsParts());
        response.setRecipientCount(message.getRecipients().size());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class RecipientInfo {
        private String phoneNo;
        private Long contactId;
        private Long groupId;
    }
}

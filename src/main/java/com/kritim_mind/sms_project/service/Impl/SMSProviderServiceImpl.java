package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.model.Message;
import com.kritim_mind.sms_project.model.MessageRecipient;
import com.kritim_mind.sms_project.model.MessageStatus;
import com.kritim_mind.sms_project.repository.MessageRecipientRepository;
import com.kritim_mind.sms_project.service.Interface.SMSProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSProviderServiceImpl implements SMSProviderService {

    private final MessageRecipientRepository recipientRepository;

    @Override
    @Async
    @Transactional
    public void sendBulkSms(Message message) {
        log.info("Sending SMS to {} recipients for message ID: {}",
                message.getRecipients().size(), message.getId());

        for (MessageRecipient recipient : message.getRecipients()) {
            try {
                boolean sent = sendSms(recipient.getPhoneNo(), message.getContent());

                if (sent) {
                    recipient.setStatus(MessageStatus.SENT);
                    recipient.setSentAt(LocalDateTime.now());
                    log.debug("SMS sent successfully to {}", recipient.getPhoneNo());
                } else {
                    recipient.setStatus(MessageStatus.FAILED);
                    recipient.setFailedAt(LocalDateTime.now());
                    log.error("Failed to send SMS to {}", recipient.getPhoneNo());
                }

                recipientRepository.save(recipient);

            } catch (Exception e) {
                log.error("Error sending SMS to {}: {}", recipient.getPhoneNo(), e.getMessage());
                recipient.setStatus(MessageStatus.FAILED);
                recipient.setFailedAt(LocalDateTime.now());
                recipientRepository.save(recipient);
            }
        }

        log.info("Bulk SMS sending completed for message ID: {}", message.getId());
    }

    // Simulated SMS sending - replace with actual provider integration
    private boolean sendSms(String phoneNo, String content) {
        log.info("Sending SMS to {}: {}", phoneNo, content);
        return true; // Simulate success
    }
}

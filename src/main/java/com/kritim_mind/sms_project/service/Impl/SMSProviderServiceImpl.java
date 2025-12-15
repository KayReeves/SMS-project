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
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSProviderServiceImpl implements SMSProviderService {

    private final MessageRecipientRepository recipientRepository;


    private static final String ACCOUNT_SID = "AC77b80b6a61ef58639521d4a4c8fa491d";
    private static final String AUTH_TOKEN = "14a5ca4430c70fbf8c65c9a9a75c65b2";
    private static final String FROM_NUMBER = "+18554199262";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
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


    private boolean sendSms(String toNumber, String content) {
        try {
            com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(FROM_NUMBER),
                    content
            ).create();

            log.info("Twilio SID {} - SMS sent to {}", message.getSid(), toNumber);
            return true;

        } catch (Exception e) {
            log.error("Twilio SMS error to {}: {}", toNumber, e.getMessage());
            return false;
        }
    }
}

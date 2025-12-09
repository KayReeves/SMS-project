package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.model.Message;

public interface SMSProviderService {
    void sendBulkSms(Message message);
}

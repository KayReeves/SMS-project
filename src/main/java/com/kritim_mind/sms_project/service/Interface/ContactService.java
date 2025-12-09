package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.request.ContactRequest;
import com.kritim_mind.sms_project.dto.response.ContactResponse;

import java.util.List;

public interface ContactService {
    List<ContactResponse> getAllContacts(Boolean isDeleted);
    ContactResponse getContactById(Long id);
    ContactResponse createContact(ContactRequest request);
    ContactResponse updateContact(Long id, ContactRequest request);
    void deleteContact(Long id);
}

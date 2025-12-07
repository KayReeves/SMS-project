package com.kritim_mind.sms_project.service;

import com.kritim_mind.sms_project.dto.request.ContactRequest;
import com.kritim_mind.sms_project.dto.response.ContactResponse;
import com.kritim_mind.sms_project.model.Contact;
import com.kritim_mind.sms_project.repository.ContactRepository;
import com.kritim_mind.sms_project.utils.DuplicateResourceException;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {
    private final ContactRepository contactRepository;

    @Transactional
    public List<ContactResponse> getAllContacts(Boolean isDeleted) {
        List<Contact> contacts = isDeleted != null
                ? contactRepository.findByIsDeleted(isDeleted)
                : contactRepository.findAll();

        return contacts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactResponse getContactById(Long id) {
        Contact contact = contactRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        return mapToResponse(contact);
    }

    @Transactional
    public ContactResponse createContact(ContactRequest request) {
        log.info("Creating contact: {}", request.getName());

        if (contactRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        Contact contact = Contact.builder()
                .name(request.getName())
                .phoneNo(request.getPhoneNo())
                .isDeleted(false)
                .build();

        contact = contactRepository.save(contact);
        log.info("Contact created with ID: {}", contact.getId());

        return mapToResponse(contact);
    }

    @Transactional
    public ContactResponse updateContact(Long id, ContactRequest request) {
        log.info("Updating contact ID: {}", id);

        Contact contact = contactRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        if (!contact.getPhoneNo().equals(request.getPhoneNo()) &&
                contactRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        contact.setName(request.getName());
        contact.setPhoneNo(request.getPhoneNo());

        contact = contactRepository.save(contact);
        log.info("Contact updated successfully");

        return mapToResponse(contact);
    }

    @Transactional
    public void deleteContact(Long id) {
        log.info("Soft deleting contact ID: {}", id);

        Contact contact = contactRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setIsDeleted(true);
        contactRepository.save(contact);

        log.info("Contact deleted successfully");
    }

    private ContactResponse mapToResponse(Contact contact) {
        ContactResponse response = new ContactResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setPhoneNo(contact.getPhoneNo());
        response.setCreatedAt(contact.getCreatedAt());
        response.setUpdatedAt(contact.getUpdatedAt());
        return response;
    }

}

package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.ContactRequest;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.ContactResponse;
import com.kritim_mind.sms_project.service.Interface.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ContactResponse>>> getAllContacts(
            @RequestParam(required = false) Boolean is_deleted) {
        List<ContactResponse> contacts = contactService.getAllContacts(is_deleted);
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }

    @GetMapping("/{contact_id}")
    public ResponseEntity<ApiResponse<ContactResponse>> getContactById(
            @PathVariable("contact_id") Long contactId) {
        ContactResponse contact = contactService.getContactById(contactId);
        return ResponseEntity.ok(ApiResponse.success(contact));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ContactResponse>> createContact(
            @RequestBody ContactRequest request) {
        ContactResponse contact = contactService.createContact(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact created successfully", contact));
    }

    @PutMapping("/update/{contact_id}")
    public ResponseEntity<ApiResponse<ContactResponse>> updateContact(
            @PathVariable("contact_id") Long contactId,
            @RequestBody ContactRequest request) {
        ContactResponse contact = contactService.updateContact(contactId, request);
        return ResponseEntity.ok(ApiResponse.success("Contact updated successfully", contact));
    }

    @DeleteMapping("/delete/{contact_id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(
            @PathVariable("contact_id") Long contactId) {
        contactService.deleteContact(contactId);
        return ResponseEntity.ok(ApiResponse.success("Contact deleted successfully", null));
    }
}

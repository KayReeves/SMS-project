package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.ContactRequest;
import com.kritim_mind.sms_project.dto.response.ContactResponse;
import com.kritim_mind.sms_project.service.Interface.ContactService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long CONTACT_ID = 1L;

    // ------------------ GET ALL CONTACTS ------------------

    @Test
    void getAllContacts_success() throws Exception {

        ContactResponse contact1 = new ContactResponse();
        contact1.setId(1L);
        contact1.setName("John Doe");
        contact1.setPhoneNo("9800000001");
        contact1.setCreatedAt(LocalDateTime.now());

        ContactResponse contact2 = new ContactResponse();
        contact2.setId(2L);
        contact2.setName("Jane Doe");
        contact2.setPhoneNo("9800000002");
        contact2.setCreatedAt(LocalDateTime.now());

        Mockito.when(contactService.getAllContacts(null))
                .thenReturn(List.of(contact1, contact2));

        mockMvc.perform(get("/api/contacts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"));
    }

    // ------------------ GET CONTACT BY ID ------------------

    @Test
    void getContactById_success() throws Exception {

        ContactResponse response = new ContactResponse();
        response.setId(CONTACT_ID);
        response.setName("John Doe");
        response.setPhoneNo("9800000001");

        Mockito.when(contactService.getContactById(CONTACT_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/contacts/{contact_id}", CONTACT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(CONTACT_ID))
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }

    // ------------------ CREATE CONTACT ------------------

    @Test
    void createContact_success() throws Exception {

        ContactRequest request = new ContactRequest();
        request.setName("New Contact");
        request.setPhoneNo("9800000009");

        ContactResponse response = new ContactResponse();
        response.setId(3L);
        response.setName("New Contact");
        response.setPhoneNo("9800000009");
        response.setCreatedAt(LocalDateTime.now());

        Mockito.when(contactService.createContact(Mockito.any(ContactRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/contacts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Contact created successfully"))
                .andExpect(jsonPath("$.data.name").value("New Contact"));
    }

    // ------------------ UPDATE CONTACT ------------------

    @Test
    void updateContact_success() throws Exception {

        ContactRequest request = new ContactRequest();
        request.setName("Updated Name");
        request.setPhoneNo("9800000010");

        ContactResponse response = new ContactResponse();
        response.setId(CONTACT_ID);
        response.setName("Updated Name");
        response.setPhoneNo("9800000010");

        Mockito.when(contactService.updateContact(
                        Mockito.eq(CONTACT_ID), Mockito.any(ContactRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/contacts/update/{contact_id}", CONTACT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contact updated successfully"))
                .andExpect(jsonPath("$.data.phoneNo").value("9800000010"));
    }

    // ------------------ SUSPEND CONTACT ------------------

    @Test
    void suspendContact_success() throws Exception {

        Mockito.doNothing()
                .when(contactService).suspendContact(CONTACT_ID);

        mockMvc.perform(patch("/api/contacts/suspend/{contact_id}", CONTACT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Contact suspended successfully"));
    }

    // ------------------ DELETE CONTACT ------------------

    @Test
    void deleteContact_success() throws Exception {

        Mockito.doNothing()
                .when(contactService).deleteContact(CONTACT_ID);

        mockMvc.perform(delete("/api/contacts/delete/{contact_id}", CONTACT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Contact deleted successfully"));
    }
}

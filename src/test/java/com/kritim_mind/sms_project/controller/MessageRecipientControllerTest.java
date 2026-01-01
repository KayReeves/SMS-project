package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.RecipientStatusUpdateRequest;
import com.kritim_mind.sms_project.dto.response.RecipientResponse;
import com.kritim_mind.sms_project.service.Interface.MessageRecipientService;
import com.kritim_mind.sms_project.model.MessageStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageRecipientController.class)
class MessageRecipientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageRecipientService recipientService;

    @Autowired
    private ObjectMapper objectMapper;

    /* ------------------------------------------
       Helper methods
    ------------------------------------------ */
    private RecipientResponse mockRecipient() {
        RecipientResponse r = new RecipientResponse();
        r.setId(1L);
        r.setMessageId(10L);
        r.setPhoneNo("9800000000");
        r.setContactId(5L);
        r.setContactName("John Doe");
        r.setGroupId(2L);
        r.setGroupName("Test Group");
        r.setStatus(MessageStatus.PENDING);
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }

    private RecipientStatusUpdateRequest mockUpdateRequest() {
        RecipientStatusUpdateRequest req = new RecipientStatusUpdateRequest();
        req.setStatus(MessageStatus.DELIVERED);
        return req;
    }

    /* ------------------------------------------
       GET recipients by message ID
    ------------------------------------------ */
    @Test
    void getRecipientsByMessageId_success() throws Exception {
        Mockito.when(recipientService.getRecipientsByMessageId(10L))
                .thenReturn(List.of(mockRecipient()));

        mockMvc.perform(get("/api/messagerecipient/messages/10/recipients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].phoneNo").value("9800000000"));
    }

    /* ------------------------------------------
       GET recipients by message ID (paged)
    ------------------------------------------ */
    @Test
    void getRecipientsByMessageIdPaged_success() throws Exception {
        Page<RecipientResponse> page =
                new PageImpl<>(List.of(mockRecipient()), PageRequest.of(0, 20), 1);

        Mockito.when(recipientService.getRecipientsByMessageId(Mockito.eq(10L), Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/messagerecipient/messages/10/recipients/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].contactName").value("John Doe"));
    }

    /* ------------------------------------------
       GET recipient by ID
    ------------------------------------------ */
    @Test
    void getRecipientById_success() throws Exception {
        Mockito.when(recipientService.getRecipientById(1L))
                .thenReturn(mockRecipient());

        mockMvc.perform(get("/api/messagerecipient/message_recipients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    /* ------------------------------------------
       UPDATE recipient status
    ------------------------------------------ */
    @Test
    void updateRecipientStatus_success() throws Exception {
        Mockito.when(recipientService.updateRecipientStatus(1L, MessageStatus.DELIVERED))
                .thenReturn(mockRecipient());

        mockMvc.perform(put("/api/messagerecipient/update/message_recipients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recipient status updated successfully"));
    }

    /* ------------------------------------------
       DELETE recipient
    ------------------------------------------ */
    @Test
    void deleteRecipient_success() throws Exception {
        Mockito.doNothing().when(recipientService).deleteRecipient(1L);

        mockMvc.perform(delete("/api/messagerecipient/delete/message_recipients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recipient deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}

package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.MessageRequest;
import com.kritim_mind.sms_project.dto.response.MessageResponse;
import com.kritim_mind.sms_project.service.Interface.MessageService;
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

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    /* --------------------------------------------------
       Helpers
     -------------------------------------------------- */
    private MessageResponse mockMessageResponse() {
        MessageResponse response = new MessageResponse();
        response.setId(1L);
        response.setSenderId(10L);
        response.setSenderUsername("admin");
        response.setContent("Hello World");
        response.setTotalSmsParts(1);
        response.setRecipientCount(3);
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    private MessageRequest mockMessageRequest() {
        MessageRequest request = new MessageRequest();
        request.setSenderId(10L);
        request.setContent("Hello World");
        request.setRecipientContactIds(List.of(1L, 2L));
        request.setRecipientGroupIds(List.of(3L));
        request.setRecipientNumbers(List.of("9800000000"));
        return request;
    }

    /* --------------------------------------------------
       GET ALL MESSAGES
     -------------------------------------------------- */
    @Test
    void getAllMessages_success() throws Exception {
        Page<MessageResponse> page =
                new PageImpl<>(List.of(mockMessageResponse()));

        Mockito.when(messageService.getAllMessages(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/messages/all")
                        .param("sender_id", "10")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].content")
                        .value("Hello World"));
    }

    /* --------------------------------------------------
       GET MESSAGE BY ID
     -------------------------------------------------- */
    @Test
    void getMessageById_success() throws Exception {
        Mockito.when(messageService.getMessageById(1L))
                .thenReturn(mockMessageResponse());

        mockMvc.perform(get("/api/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.content")
                        .value("Hello World"));
    }

    /* --------------------------------------------------
       CREATE MESSAGE
     -------------------------------------------------- */
    @Test
    void createMessage_success() throws Exception {
        Mockito.when(messageService.createMessage(Mockito.any()))
                .thenReturn(mockMessageResponse());

        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockMessageRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Message sent successfully"))
                .andExpect(jsonPath("$.data.content")
                        .value("Hello World"));
    }

    /* --------------------------------------------------
       UPDATE MESSAGE
     -------------------------------------------------- */
    @Test
    void updateMessage_success() throws Exception {
        Mockito.when(messageService.updateMessage(Mockito.eq(1L), Mockito.anyString()))
                .thenReturn(mockMessageResponse());

        mockMvc.perform(put("/api/messages/1")
                        .param("content", "Updated message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Message updated successfully"));
    }

    /* --------------------------------------------------
       DELETE MESSAGE
     -------------------------------------------------- */
    @Test
    void deleteMessage_success() throws Exception {
        Mockito.doNothing().when(messageService).deleteMessage(1L);

        mockMvc.perform(delete("/api/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Message deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}

package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.AddContactToGroupRequest;
import com.kritim_mind.sms_project.dto.request.GroupRequest;
import com.kritim_mind.sms_project.dto.response.GroupResponse;
import com.kritim_mind.sms_project.service.Interface.GroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @Autowired
    private ObjectMapper objectMapper;

    private GroupResponse mockGroupResponse() {
        GroupResponse response = new GroupResponse();
        response.setId(1L);
        response.setName("Test Group");
        response.setDescription("Test Description");
        response.setContactCount(2);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }

    /* --------------------------------------------------
       GET ALL GROUPS
     -------------------------------------------------- */
    @Test
    void getAllGroups_success() throws Exception {
        Mockito.when(groupService.getAllGroups(null))
                .thenReturn(List.of(mockGroupResponse()));

        mockMvc.perform(get("/api/groups/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Test Group"));
    }

    /* --------------------------------------------------
       GET GROUP BY ID
     -------------------------------------------------- */
    @Test
    void getGroupById_success() throws Exception {
        Mockito.when(groupService.getGroupById(1L))
                .thenReturn(mockGroupResponse());

        mockMvc.perform(get("/api/groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    /* --------------------------------------------------
       CREATE GROUP
     -------------------------------------------------- */
    @Test
    void createGroup_success() throws Exception {
        GroupRequest request = new GroupRequest();
        request.setName("New Group");
        request.setDescription("New Description");

        Mockito.when(groupService.createGroup(Mockito.any()))
                .thenReturn(mockGroupResponse());

        mockMvc.perform(post("/api/groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Group created successfully"));
    }

    /* --------------------------------------------------
       UPDATE GROUP
     -------------------------------------------------- */
    @Test
    void updateGroup_success() throws Exception {
        GroupRequest request = new GroupRequest();
        request.setName("Updated Group");

        Mockito.when(groupService.updateGroup(Mockito.eq(1L), Mockito.any()))
                .thenReturn(mockGroupResponse());

        mockMvc.perform(put("/api/groups/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Group updated successfully"));
    }

    /* --------------------------------------------------
       SUSPEND GROUP
     -------------------------------------------------- */
    @Test
    void suspendGroup_success() throws Exception {
        Mockito.doNothing().when(groupService).suspendGroup(1L);

        mockMvc.perform(delete("/api/groups/suspend/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Group deleted successfully"));
    }

    /* --------------------------------------------------
       ADD CONTACT TO GROUP
     -------------------------------------------------- */
    @Test
    void addContactToGroup_success() throws Exception {
        AddContactToGroupRequest request = new AddContactToGroupRequest();
        request.setContactIds(List.of(1L, 2L));

        Mockito.when(groupService.addContactToGroup(Mockito.eq(1L), Mockito.any()))
                .thenReturn(mockGroupResponse());

        mockMvc.perform(post("/api/groups/1/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Contact added to group successfully"));
    }

    /* --------------------------------------------------
       REMOVE CONTACT FROM GROUP
     -------------------------------------------------- */
    @Test
    void removeContactFromGroup_success() throws Exception {
        Mockito.doNothing()
                .when(groupService)
                .removeContactFromGroup(1L, 2L);

        mockMvc.perform(delete("/api/groups/delete/1/contacts/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Contact removed from group successfully"));
    }

    /* --------------------------------------------------
       DELETE GROUP
     -------------------------------------------------- */
    @Test
    void deleteGroup_success() throws Exception {
        Mockito.doNothing().when(groupService).deleteGroup(1L);

        mockMvc.perform(delete("/api/groups/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Group deleted successfully"));
    }

    /* --------------------------------------------------
       BULK CONTACT UPLOAD
     -------------------------------------------------- */
    @Test
    void addContactsToGroupBulk_success() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "contacts.csv",
                        "text/csv",
                        "name,phone\nTest,9800000000".getBytes()
                );

        MockMultipartFile groupRequest =
                new MockMultipartFile(
                        "groupRequest",
                        "",
                        "application/json",
                        objectMapper.writeValueAsBytes(new GroupRequest())
                );

        Mockito.when(groupService.addContactsToGroupFromFile(
                        Mockito.any(), Mockito.any()))
                .thenReturn(mockGroupResponse());

        mockMvc.perform(multipart("/api/groups/contacts/bulk")
                        .file(file)
                        .file(groupRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Contacts added in bulk successfully"));
    }
}

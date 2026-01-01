package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.AdminUpdateRequest;
import com.kritim_mind.sms_project.dto.request.BalanceTopUpRequest;
import com.kritim_mind.sms_project.dto.request.BalanceUpdateRequest;
import com.kritim_mind.sms_project.dto.response.AdminResponse;
import com.kritim_mind.sms_project.dto.response.BalanceResponse;
import com.kritim_mind.sms_project.service.Interface.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long ADMIN_ID = 1L;

    @Test
    void getAdmin_success() throws Exception {
        AdminResponse response = new AdminResponse();
        response.setId(ADMIN_ID);
        response.setUsername("admin");
        response.setEmail("admin@test.com");
        response.setTotalSmsCredits(1000);
        response.setUsedSmsCredits(200);
        response.setRemainingCredits(800);
        response.setCreatedAt(LocalDateTime.now());

        Mockito.when(adminService.getAdmin(ADMIN_ID)).thenReturn(response);

        mockMvc.perform(get("/api/admins/{admin_id}", ADMIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.remainingCredits").value(800));
    }

    @Test
    void updateAdmin_success() throws Exception {
        AdminUpdateRequest request = new AdminUpdateRequest();
        request.setUsername("newAdmin");
        request.setEmail("new@test.com");
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");

        AdminResponse response = new AdminResponse();
        response.setId(ADMIN_ID);
        response.setUsername("newAdmin");
        response.setEmail("new@test.com");

        Mockito.when(adminService.updateAdmin(Mockito.eq(ADMIN_ID), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/admins/update/{admin_id}", ADMIN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Admin updated successfully"))
                .andExpect(jsonPath("$.data.username").value("newAdmin"));
    }

    @Test
    void getBalance_success() throws Exception {
        BalanceResponse response = BalanceResponse.builder()
                .totalCredits(1000)
                .usedCredits(400)
                .remainingCredits(600)
                .build();

        Mockito.when(adminService.getBalance(ADMIN_ID)).thenReturn(response);

        mockMvc.perform(get("/api/admins/{admin_id}/balance", ADMIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCredits").value(1000))
                .andExpect(jsonPath("$.data.remainingCredits").value(600));
    }

    @Test
    void updateBalance_success() throws Exception {
        BalanceUpdateRequest request = new BalanceUpdateRequest();
        request.setSentSmsParts(50);

        BalanceResponse response = BalanceResponse.builder()
                .totalCredits(1000)
                .usedCredits(450)
                .remainingCredits(550)
                .build();

        Mockito.when(adminService.updateBalance(Mockito.eq(ADMIN_ID), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/admins/{admin_id}/balance/update", ADMIN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Balance updated successfully"))
                .andExpect(jsonPath("$.data.usedCredits").value(450));
    }

    @Test
    void topupBalance_success() throws Exception {
        BalanceTopUpRequest request = new BalanceTopUpRequest();
        request.setAddCredits(500);

        BalanceResponse response = BalanceResponse.builder()
                .totalCredits(1500)
                .usedCredits(300)
                .remainingCredits(1200)
                .build();

        Mockito.when(adminService.topupBalance(Mockito.eq(ADMIN_ID), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/admins/{admin_id}/balance/topup", ADMIN_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Balance topped up successfully"))
                .andExpect(jsonPath("$.data.totalCredits").value(1500));
    }
}

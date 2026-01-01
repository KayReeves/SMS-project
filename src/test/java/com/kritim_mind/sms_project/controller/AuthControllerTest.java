package com.kritim_mind.sms_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritim_mind.sms_project.dto.request.LoginRequest;
import com.kritim_mind.sms_project.dto.response.LoginResponse;
import com.kritim_mind.sms_project.service.Interface.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_success_shouldReturnTokenAndSetCookie() throws Exception {

        // GIVEN
        LoginRequest request = new LoginRequest();
        request.setLogin("admin@test.com");
        request.setPassword("password123");

        LoginResponse loginResponse = new LoginResponse(
                "admin",
                1L,
                "admin@test.com",
                "jwt-token-123",
                "ADMIN"
        );

        Mockito.when(authService.login(Mockito.any(LoginRequest.class)))
                .thenReturn(loginResponse);

        // WHEN & THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.email").value("admin@test.com"))
                .andExpect(jsonPath("$.data.token").value("jwt-token-123"))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        org.hamcrest.Matchers.containsString("access_token=jwt-token-123")));
    }
}

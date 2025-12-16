package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.LoginRequest;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.LoginResponse;
import com.kritim_mind.sms_project.service.Interface.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {

        LoginResponse loginResponse = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("access_token", loginResponse.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(60 * 60 * 60)
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", loginResponse)
        );
    }
}

package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.LoginRequest;
import com.kritim_mind.sms_project.dto.response.AdminResponse;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.LoginResponse;
import com.kritim_mind.sms_project.exception.UnauthorizedException;
import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.service.AdminService;
import com.kritim_mind.sms_project.utils.PasswordUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Admin admin = adminService.findByUsername(request.getUsername());

        if (!PasswordUtil.verifyPassword(request.getPassword(), admin.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Create JWT token (simplified - in production use proper JWT library)
        String token = "jwt-token-" + admin.getId(); // Replace with actual JWT generation

        AdminResponse adminResponse = adminService.getAdmin(admin.getId());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .admin(adminResponse)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}

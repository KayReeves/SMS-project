package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.AdminCreateRequest;
import com.kritim_mind.sms_project.dto.request.AdminUpdateRequest;
import com.kritim_mind.sms_project.dto.request.BalanceTopUpRequest;
import com.kritim_mind.sms_project.dto.request.BalanceUpdateRequest;
import com.kritim_mind.sms_project.dto.response.AdminResponse;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.BalanceResponse;
import com.kritim_mind.sms_project.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
class AdminController {

    private final AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(
             @RequestBody AdminCreateRequest request) {
        AdminResponse response = adminService.createAdmin(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin created successfully", response));
    }

    @GetMapping("/{admin_id}")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdmin(
            @PathVariable("admin_id") Long adminId) {
        AdminResponse response = adminService.getAdmin(adminId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/update/{admin_id}")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdmin(
            @PathVariable("admin_id") Long adminId,
            @RequestBody AdminUpdateRequest request) {
        AdminResponse response = adminService.updateAdmin(adminId, request);
        return ResponseEntity.ok(ApiResponse.success("Admin updated successfully", response));
    }

    @GetMapping("/{admin_id}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @PathVariable("admin_id") Long adminId) {
        BalanceResponse response = adminService.getBalance(adminId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{admin_id}/balance/update")
    public ResponseEntity<ApiResponse<BalanceResponse>> updateBalance(
            @PathVariable("admin_id") Long adminId,
            @RequestBody BalanceUpdateRequest request) {
        BalanceResponse response = adminService.updateBalance(adminId, request);
        return ResponseEntity.ok(ApiResponse.success("Balance updated successfully", response));
    }

    @PostMapping("/{admin_id}/balance/topup")
    public ResponseEntity<ApiResponse<BalanceResponse>> topupBalance(
            @PathVariable("admin_id") Long adminId,
            @RequestBody BalanceTopUpRequest request) {
        BalanceResponse response = adminService.topupBalance(adminId, request);
        return ResponseEntity.ok(ApiResponse.success("Balance topped up successfully", response));
    }

}

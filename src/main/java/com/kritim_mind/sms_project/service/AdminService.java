package com.kritim_mind.sms_project.service;

import com.kritim_mind.sms_project.dto.request.AdminCreateRequest;
import com.kritim_mind.sms_project.dto.request.AdminUpdateRequest;
import com.kritim_mind.sms_project.dto.request.BalanceTopUpRequest;
import com.kritim_mind.sms_project.dto.request.BalanceUpdateRequest;
import com.kritim_mind.sms_project.dto.response.AdminResponse;
import com.kritim_mind.sms_project.dto.response.BalanceResponse;
import com.kritim_mind.sms_project.exception.InsufficientBalanceException;
import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.repository.AdminRepository;
import com.kritim_mind.sms_project.utils.DuplicateResourceException;
import com.kritim_mind.sms_project.utils.PasswordUtil;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;

    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest request) {
        log.info("Creating admin with username: {}", request.getUsername());

        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        Admin admin = Admin.builder()
                .username(request.getUsername())
                .passwordHash(PasswordUtil.hashPassword(request.getPassword()))
                .totalSmsCredits(0)
                .usedSmsCredits(0)
                .build();

        admin = adminRepository.save(admin);
        log.info("Admin created successfully with ID: {}", admin.getId());

        return mapToResponse(admin);
    }

    @Transactional
    public AdminResponse updateAdmin(Long adminId, AdminUpdateRequest request) {
        log.info("Updating admin with ID: {}", adminId);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (request.getUsername() != null && !request.getUsername().equals(admin.getUsername())) {
            if (adminRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }
            admin.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            admin.setPasswordHash(PasswordUtil.hashPassword(request.getPassword()));
        }

        admin = adminRepository.save(admin);
        log.info("Admin updated successfully");

        return mapToResponse(admin);
    }

    public AdminResponse getAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return mapToResponse(admin);
    }

    @Transactional
    public BalanceResponse getBalance(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        return BalanceResponse.builder()
                .totalCredits(admin.getTotalSmsCredits())
                .usedCredits(admin.getUsedSmsCredits())
                .remainingCredits(admin.getRemainingCredits())
                .build();
    }

    @Transactional
    public BalanceResponse updateBalance(Long adminId, BalanceUpdateRequest request) {
        log.info("Updating balance for admin ID: {}, deducting {} SMS parts",
                adminId, request.getSentSmsParts());

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (admin.getRemainingCredits() < request.getSentSmsParts()) {
            throw new InsufficientBalanceException("Insufficient SMS credits");
        }

        admin.setUsedSmsCredits(admin.getUsedSmsCredits() + request.getSentSmsParts());
        adminRepository.save(admin);

        log.info("Balance updated successfully. Remaining: {}", admin.getRemainingCredits());

        return BalanceResponse.builder()
                .totalCredits(admin.getTotalSmsCredits())
                .usedCredits(admin.getUsedSmsCredits())
                .remainingCredits(admin.getRemainingCredits())
                .build();
    }

    @Transactional
    public BalanceResponse topupBalance(Long adminId, BalanceTopUpRequest request) {
        log.info("Topping up balance for admin ID: {}, adding {} credits",
                adminId, request.getAddCredits());

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        admin.setTotalSmsCredits(admin.getTotalSmsCredits() + request.getAddCredits());
        adminRepository.save(admin);

        log.info("Balance topped up successfully. New total: {}", admin.getTotalSmsCredits());

        return BalanceResponse.builder()
                .totalCredits(admin.getTotalSmsCredits())
                .usedCredits(admin.getUsedSmsCredits())
                .remainingCredits(admin.getRemainingCredits())
                .build();
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    private AdminResponse mapToResponse(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setTotalSmsCredits(admin.getTotalSmsCredits());
        response.setUsedSmsCredits(admin.getUsedSmsCredits());
        response.setRemainingCredits(admin.getRemainingCredits());
        response.setCreatedAt(admin.getCreatedAt());
        return response;
    }
}

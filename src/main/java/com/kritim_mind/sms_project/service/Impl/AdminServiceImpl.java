package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.AdminUpdateRequest;
import com.kritim_mind.sms_project.dto.request.BalanceTopUpRequest;
import com.kritim_mind.sms_project.dto.request.BalanceUpdateRequest;
import com.kritim_mind.sms_project.dto.response.AdminResponse;
import com.kritim_mind.sms_project.dto.response.BalanceResponse;
import com.kritim_mind.sms_project.exception.InsufficientBalanceException;
import com.kritim_mind.sms_project.exception.UnauthorizedException;
import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.repository.AdminRepository;
import com.kritim_mind.sms_project.service.Interface.AdminService;
import com.kritim_mind.sms_project.utils.DuplicateResourceException;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public AdminResponse updateAdmin(Long adminId, AdminUpdateRequest request) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (request.getUsername() != null && !request.getUsername().equals(admin.getUsername())) {
            if (adminRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }
            admin.setUsername(request.getUsername());
        }

        if (request.getEmail() != null &&
                !request.getEmail().equals(admin.getEmail())) {

            if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists");
            }
            admin.setEmail(request.getEmail());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {

            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new UnauthorizedException("Current password is required");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPasswordHash())) {
                throw new UnauthorizedException("Current password is incorrect");
            }

            admin.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        admin = adminRepository.save(admin);
        return mapToResponse(admin);
    }

    @Override
    public AdminResponse getAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return mapToResponse(admin);
    }

    @Transactional
    @Override
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
    @Override
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
    @Override
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

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    private AdminResponse mapToResponse(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setEmail(admin.getEmail());
        response.setTotalSmsCredits(admin.getTotalSmsCredits());
        response.setUsedSmsCredits(admin.getUsedSmsCredits());
        response.setRemainingCredits(admin.getRemainingCredits());
        response.setCreatedAt(admin.getCreatedAt());
        return response;
    }
}

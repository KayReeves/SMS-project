package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.request.AdminUpdateRequest;
import com.kritim_mind.sms_project.dto.request.BalanceTopUpRequest;
import com.kritim_mind.sms_project.dto.request.BalanceUpdateRequest;
import com.kritim_mind.sms_project.dto.response.AdminResponse;
import com.kritim_mind.sms_project.dto.response.BalanceResponse;
import com.kritim_mind.sms_project.model.Admin;

public interface AdminService {

    AdminResponse updateAdmin(Long adminId, AdminUpdateRequest request);

    AdminResponse getAdmin(Long adminId);

    BalanceResponse getBalance(Long adminId);

    BalanceResponse updateBalance(Long adminId, BalanceUpdateRequest request);

    BalanceResponse topupBalance(Long adminId, BalanceTopUpRequest request);

    Admin findByUsername(String username);
}

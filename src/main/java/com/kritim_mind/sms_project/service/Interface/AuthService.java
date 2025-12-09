    package com.kritim_mind.sms_project.service.Interface;

    import com.kritim_mind.sms_project.dto.request.LoginRequest;
    import com.kritim_mind.sms_project.dto.response.LoginResponse;

    public interface AuthService {
        LoginResponse login(LoginRequest request);
    }
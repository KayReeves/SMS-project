package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.LoginRequest;
import com.kritim_mind.sms_project.dto.response.LoginResponse;
import com.kritim_mind.sms_project.exception.UnauthorizedException;
import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.repository.AdminRepository;
import com.kritim_mind.sms_project.service.Interface.AuthService;
import com.kritim_mind.sms_project.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginResponse login(LoginRequest request) {

        try {
            Admin admin = adminRepository
                    .findByUsernameOrEmail(request.getLogin())
                    .orElseThrow(() ->
                            new UnauthorizedException("Incorrect username/email or password"));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            admin.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication, admin.getId());

            return new LoginResponse(
                    admin.getUsername(),
                    admin.getId(),
                    admin.getEmail(),
                    token,
                    "Bearer"
            );

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Incorrect username/email or password");

        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}

package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.LoginRequest;
import com.kritim_mind.sms_project.dto.response.LoginResponse;
import com.kritim_mind.sms_project.exception.UnauthorizedException;
import com.kritim_mind.sms_project.model.Admin;
import com.kritim_mind.sms_project.repository.AdminRepository;
import com.kritim_mind.sms_project.service.Interface.AuthService;
import com.kritim_mind.sms_project.utils.JwtTokenProvider;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            Admin admin = adminRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin does not exist"));

            String token = jwtTokenProvider.generateToken(authentication, admin.getId());

            return new LoginResponse(
                    admin.getUsername(),
                    token,
                    "Bearer"
            );

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new UnauthorizedException("Incorrect username or password");

        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}

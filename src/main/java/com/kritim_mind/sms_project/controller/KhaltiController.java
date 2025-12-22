package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.KhaltiInitiateRequest;
import com.kritim_mind.sms_project.dto.request.KhaltiVerifyRequest;
import com.kritim_mind.sms_project.dto.response.KhaltiInitiateResponse;
import com.kritim_mind.sms_project.service.KhaltiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/khalti")
@CrossOrigin("*") // allow your frontend origin
public class KhaltiController {

    private final KhaltiService khaltiService;

    public KhaltiController(KhaltiService khaltiService) {
        this.khaltiService = khaltiService;
    }

    /** Initiate Payment */
    @PostMapping("/initiate")
    public KhaltiInitiateResponse initiate(@RequestBody KhaltiInitiateRequest request) {
        return khaltiService.initiatePayment(request);
    }

    /** Verify Payment */
    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody KhaltiVerifyRequest request) {
        return khaltiService.verifyPayment(request);
    }
}

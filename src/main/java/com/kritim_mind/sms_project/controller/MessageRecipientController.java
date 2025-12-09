package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.RecipientStatusUpdateRequest;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.RecipientResponse;
import com.kritim_mind.sms_project.service.Interface.MessageRecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messagerecipient")
@RequiredArgsConstructor
public class MessageRecipientController {

    private final MessageRecipientService recipientService;

    @GetMapping("/messages/{message_id}/recipients")
    public ResponseEntity<ApiResponse<List<RecipientResponse>>> getRecipientsByMessageId(
            @PathVariable("message_id") Long messageId) {
        List<RecipientResponse> recipients = recipientService.getRecipientsByMessageId(messageId);
        return ResponseEntity.ok(ApiResponse.success(recipients));
    }

    @GetMapping("/messages/{message_id}/recipients/paged")
    public ResponseEntity<ApiResponse<Page<RecipientResponse>>> getRecipientsByMessageIdPaged(
            @PathVariable("message_id") Long messageId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RecipientResponse> recipients = recipientService.getRecipientsByMessageId(messageId, pageable);
        return ResponseEntity.ok(ApiResponse.success(recipients));
    }

    @GetMapping("/message_recipients/{recipient_id}")
    public ResponseEntity<ApiResponse<RecipientResponse>> getRecipientById(
            @PathVariable("recipient_id") Long recipientId) {
        RecipientResponse recipient = recipientService.getRecipientById(recipientId);
        return ResponseEntity.ok(ApiResponse.success(recipient));
    }

    @PutMapping("/update/message_recipients/{recipient_id}")
    public ResponseEntity<ApiResponse<RecipientResponse>> updateRecipientStatus(
            @PathVariable("recipient_id") Long recipientId,
            @RequestBody RecipientStatusUpdateRequest request) {
        RecipientResponse recipient = recipientService.updateRecipientStatus(recipientId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Recipient status updated successfully", recipient));
    }

    @DeleteMapping("/delete/message_recipients/{recipient_id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecipient(
            @PathVariable("recipient_id") Long recipientId) {
        recipientService.deleteRecipient(recipientId);
        return ResponseEntity.ok(ApiResponse.success("Recipient deleted successfully", null));
    }
}

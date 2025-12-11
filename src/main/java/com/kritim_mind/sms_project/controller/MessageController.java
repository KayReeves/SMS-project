package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.MessageRequest;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.MessageResponse;
import com.kritim_mind.sms_project.service.Interface.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getAllMessages(
            @RequestParam(required = false) Long sender_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MessageResponse> messages = messageService.getAllMessages(sender_id, from, to, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/{message_id}")
    public ResponseEntity<ApiResponse<MessageResponse>> getMessageById(
            @PathVariable("message_id") Long messageId) {
        MessageResponse message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<MessageResponse>> createMessage(
            @RequestBody MessageRequest request) {
        MessageResponse message = messageService.createMessage(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent successfully", message));
    }

    @PutMapping("/{message_id}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable("message_id") Long messageId,
            @RequestParam String content) {
        MessageResponse message = messageService.updateMessage(messageId, content);
        return ResponseEntity.ok(ApiResponse.success("Message updated successfully", message));
    }

    @DeleteMapping("/{message_id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable("message_id") Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(ApiResponse.success("Message deleted successfully", null));
    }
}

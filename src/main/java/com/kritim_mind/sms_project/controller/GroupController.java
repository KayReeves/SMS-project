package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.request.AddContactToGroupRequest;
import com.kritim_mind.sms_project.dto.request.GroupRequest;
import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.dto.response.GroupResponse;
import com.kritim_mind.sms_project.service.Interface.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups(
            @RequestParam(required = false) Boolean is_deleted) {
        List<GroupResponse> groups = groupService.getAllGroups(is_deleted);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @GetMapping("/{group_id}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(
            @PathVariable("group_id") Long groupId) {
        GroupResponse group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(ApiResponse.success(group));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @RequestBody GroupRequest request) {
        GroupResponse group = groupService.createGroup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Group created successfully", group));
    }

    @PutMapping("/update/{group_id}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable("group_id") Long groupId,
            @RequestBody GroupRequest request) {
        GroupResponse group = groupService.updateGroup(groupId, request);
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", group));
    }

    @DeleteMapping("/delete/{group_id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable("group_id") Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", null));
    }

    @PostMapping("/{group_id}/contacts")
    public ResponseEntity<ApiResponse<GroupResponse>> addContactToGroup(
            @PathVariable("group_id") Long groupId,
            @RequestBody AddContactToGroupRequest request) {
        GroupResponse group = groupService.addContactToGroup(groupId, request.getContactId());
        return ResponseEntity.ok(ApiResponse.success("Contact added to group successfully", group));
    }

    @DeleteMapping("/delete/{group_id}/contacts/{contact_id}")
    public ResponseEntity<ApiResponse<Void>> removeContactFromGroup(
            @PathVariable("group_id") Long groupId,
            @PathVariable("contact_id") Long contactId) {
        groupService.removeContactFromGroup(groupId, contactId);
        return ResponseEntity.ok(ApiResponse.success("Contact removed from group successfully", null));
    }
}

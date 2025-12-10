package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.GroupRequest;
import com.kritim_mind.sms_project.dto.response.ContactResponse;
import com.kritim_mind.sms_project.dto.response.GroupResponse;
import com.kritim_mind.sms_project.model.Contact;
import com.kritim_mind.sms_project.model.Group;
import com.kritim_mind.sms_project.repository.ContactRepository;
import com.kritim_mind.sms_project.repository.GroupRepository;
import com.kritim_mind.sms_project.service.Interface.GroupService;
import com.kritim_mind.sms_project.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public List<GroupResponse> getAllGroups(Boolean isDeleted) {
        List<Group> groups = isDeleted != null
                ? groupRepository.findByIsDeleted(isDeleted)
                : groupRepository.findAll();

        return groups.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupResponse getGroupById(Long id) {
        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        return mapToResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse createGroup(GroupRequest request) {
        log.info("Creating group: {}", request.getName());

        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isDeleted(false)
                .build();

        group = groupRepository.save(group);
        log.info("Group created with ID: {}", group.getId());

        return mapToResponse(group);
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(Long id, GroupRequest request) {
        log.info("Updating group ID: {}", id);

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        group.setName(request.getName());
        group.setDescription(request.getDescription());

        group = groupRepository.save(group);
        log.info("Group updated successfully");

        return mapToResponse(group);
    }

    @Override
    @Transactional
    public void deleteGroup(Long id) {
        log.info("Soft deleting group ID: {}", id);

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        group.setIsDeleted(true);
        groupRepository.save(group);

        log.info("Group deleted successfully");
    }

    @Override
    @Transactional
    public GroupResponse addContactToGroup(Long groupId, Long contactId) {
        log.info("Adding contact {} to group {}", contactId, groupId);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        group.getContacts().add(contact);
        group = groupRepository.save(group);

        log.info("Contact added to group successfully");
        return mapToResponse(group);
    }

    @Override
    @Transactional
    public void removeContactFromGroup(Long groupId, Long contactId) {
        log.info("Removing contact {} from group {}", contactId, groupId);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        group.getContacts().remove(contact);
        groupRepository.save(group);

        log.info("Contact removed from group successfully");
    }

    private GroupResponse mapToResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setContactCount(group.getContacts().size());
        response.setContacts(group.getContacts().stream()
                .map(this::mapContactToResponse)
                .collect(Collectors.toList()));
        response.setCreatedAt(group.getCreatedAt());
        response.setUpdatedAt(group.getUpdatedAt());
        return response;
    }

    private ContactResponse mapContactToResponse(Contact contact) {
        ContactResponse response = new ContactResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setPhoneNo(contact.getPhoneNo());
        return response;
    }
}

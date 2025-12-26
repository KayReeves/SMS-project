package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.dto.request.GroupRequest;
import com.kritim_mind.sms_project.dto.response.ContactResponse;
import com.kritim_mind.sms_project.dto.response.GroupResponse;
import com.kritim_mind.sms_project.exception.ResourceNotFoundException;
import com.kritim_mind.sms_project.model.Contact;
import com.kritim_mind.sms_project.model.Group;
import com.kritim_mind.sms_project.repository.ContactRepository;
import com.kritim_mind.sms_project.repository.GroupRepository;
import com.kritim_mind.sms_project.service.Interface.GroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
        Group group = groupRepository.findById(id)
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
    public void suspendGroup(Long id) {
        log.info("Soft deleting group ID: {}", id);

        Group group = groupRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        group.setIsDeleted(true);
        groupRepository.save(group);

        log.info("Group deleted successfully");
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

    @Override
    @Transactional
    public GroupResponse addContactsToGroupFromFile(MultipartFile file, GroupRequest groupRequest) {

        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSizeBytes = file.getSize();

        log.info("Uploaded file: name={}, type={}, size={} bytes",
                originalFileName, contentType, fileSizeBytes);

        if (fileSizeBytes == 0) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        if (contentType == null || !contentType.equalsIgnoreCase("text/csv")) {
            throw new IllegalArgumentException("Only CSV files are allowed");
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            List<Contact> contactsToAdd = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // CSV format: name,phoneNo
                String[] parts = line.split(",");
                if (parts.length != 2) continue;

                String name = parts[0].trim();
                String phoneNo = parts[1].trim();


                Contact contact = contactRepository.findByPhoneNo(phoneNo)
                        .orElseGet(() -> Contact.builder()
                                .name(name)
                                .phoneNo(phoneNo)
                                .isDeleted(false)
                                .build());

                contactsToAdd.add(contact);
            }

            if (contactsToAdd.isEmpty()) {
                throw new ResourceNotFoundException("No valid contacts found in file");
            }


            contactsToAdd = contactRepository.saveAll(contactsToAdd);


            List<Long> contactIds = contactsToAdd.stream()
                    .map(Contact::getId)
                    .toList();
            GroupResponse groupResponse = createGroup(groupRequest);
            GroupResponse response = addContactToGroup(groupResponse.getId(), contactIds);
            System.out.println(originalFileName);
            System.out.println(fileSizeBytes);
            System.out.println(contentType);
            response.setOriginalFileName(originalFileName);
            response.setContentType(contentType);
            response.setFileSizeBytes(fileSizeBytes);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) {
        log.info("Deleting group ID: {}", groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        groupRepository.deleteGroup(group.getId());
    }

    @Override
    @Transactional
    public GroupResponse addContactToGroup(Long groupId, List<Long> contactIds) {
        log.info("Adding contacts {} to group {}", contactIds, groupId);

        Group group = groupRepository.findByIdAndIsDeleted(groupId, false)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        List<Contact> contacts = contactRepository.findAllById(contactIds);

        if (contacts.isEmpty()) {
            throw new ResourceNotFoundException("No valid contacts found");
        }

        group.getContacts().addAll(contacts);

        group = groupRepository.save(group);

        log.info("Contacts added to group successfully");
        return mapToResponse(group);
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

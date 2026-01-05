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
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
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
                .originalFileName(request.getOriginalFileName())
                .contentType(request.getContentType())
                .fileSizeBytes(request.getFileSizeBytes())
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
    public GroupResponse addContactsToGroupFromFile(
            MultipartFile file,
            GroupRequest groupRequest
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSizeBytes = file.getSize();

        boolean isCsv =
                "text/csv".equalsIgnoreCase(contentType)
                        || (filename != null && filename.toLowerCase().endsWith(".csv"));

        boolean isExcel =
                "application/vnd.ms-excel".equalsIgnoreCase(contentType)
                        || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        .equalsIgnoreCase(contentType)
                        || "application/octet-stream".equalsIgnoreCase(contentType)
                        || (filename != null && (
                        filename.toLowerCase().endsWith(".xls")
                                || filename.toLowerCase().endsWith(".xlsx")));

        if (!isCsv && !isExcel) {
            throw new IllegalArgumentException("Only CSV or Excel files are allowed");
        }

        Map<String, Contact> uniqueContacts =
                isCsv ? parseCsv(file) : parseExcel(file);

        if (uniqueContacts.isEmpty()) {
            throw new ResourceNotFoundException("No valid contacts found in file");
        }

        List<Contact> savedContacts =
                contactRepository.saveAll(uniqueContacts.values());

        List<Long> contactIds = savedContacts.stream()
                .map(Contact::getId)
                .toList();

        groupRequest.setOriginalFileName(filename);
        groupRequest.setContentType(contentType);
        groupRequest.setFileSizeBytes(fileSizeBytes);

        GroupResponse groupResponse = createGroup(groupRequest);

        return addContactToGroup(groupResponse.getId(), contactIds);
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
        response.setOriginalFileName(group.getOriginalFileName());
        response.setFileSizeBytes(group.getFileSizeBytes());
        response.setContentType(group.getContentType());
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
    // ================= CSV PARSER =================

    private Map<String, Contact> parseCsv(MultipartFile file) {

        Map<String, Contact> uniqueContacts = new HashMap<>();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {

                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String phoneNo = normalizePhone(parts[1]);

                if (phoneNo.isBlank()) continue;

                uniqueContacts.computeIfAbsent(phoneNo, pn ->
                        contactRepository.findByPhoneNo(pn)
                                .orElse(Contact.builder()
                                        .name(name)
                                        .phoneNo(pn)
                                        .isDeleted(false)
                                        .build())
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV parsing failed", e);
        }

        return uniqueContacts;
    }


    // ================= EXCEL PARSER =================

    private Map<String, Contact> parseExcel(MultipartFile file) {

        Map<String, Contact> map = new HashMap<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellValue(row.getCell(0));
                String phone = getCellValue(row.getCell(1));

                if (phone.isBlank()) continue;

                map.computeIfAbsent(phone, p ->
                        contactRepository.findByPhoneNo(p)
                                .orElse(Contact.builder()
                                        .name(name)
                                        .phoneNo(p)
                                        .isDeleted(false)
                                        .build()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Excel parsing failed", e);
        }

        return map;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    private String normalizePhone(String phone) {
        if (phone == null) return "";

        return phone.replaceAll("[^0-9]", "").trim();
    }
}

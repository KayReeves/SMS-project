package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.dto.request.GroupRequest;
import com.kritim_mind.sms_project.dto.response.GroupResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupService {
    List<GroupResponse> getAllGroups(Boolean isDeleted);

    GroupResponse getGroupById(Long id);

    GroupResponse createGroup(GroupRequest request);

    GroupResponse updateGroup(Long id, GroupRequest request);

    void suspendGroup(Long id);

    void deleteGroup(Long id);

    GroupResponse addContactToGroup(Long groupId, List<Long> contactIds);

    void removeContactFromGroup(Long groupId, Long contactId);

    GroupResponse addContactsToGroupFromFile(MultipartFile file,GroupRequest groupRequest);
}

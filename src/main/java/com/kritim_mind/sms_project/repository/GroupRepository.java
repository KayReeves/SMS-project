package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Group;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByIsDeleted(Boolean isDeleted);

    Optional<Group> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.isDeleted = false")
    long countActiveGroups();

    @Modifying
    @Transactional
    @Query("DELETE FROM Group g WHERE g.id = :groupId")
    void deleteGroup(@Param("groupId") long groupId);

    @Query("SELECT g FROM Group g JOIN g.contacts c WHERE c.id = :contactId")
    List<Group> findGroupsByContactId(@Param("contactId") Long contactId);

}

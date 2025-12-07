package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByIsDeleted(Boolean isDeleted);

    Optional<Group> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.isDeleted = false")
    long countActiveGroups();
}

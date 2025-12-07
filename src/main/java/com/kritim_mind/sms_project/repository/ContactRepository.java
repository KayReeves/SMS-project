package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByIsDeleted(Boolean isDeleted);

    Optional<Contact> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    boolean existsByPhoneNo(String phoneNo);

    @Query("SELECT c FROM Contact c WHERE c.phoneNo = :phoneNo AND c.isDeleted = false")
    Optional<Contact> findActiveByPhoneNo(String phoneNo);

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.isDeleted = false")
    long countActiveContacts();
}

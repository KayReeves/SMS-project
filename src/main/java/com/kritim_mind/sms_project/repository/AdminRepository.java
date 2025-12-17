package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Admin> findByEmail(String email);
    @Query("SELECT a FROM Admin a WHERE a.username = :login OR a.email = :login")
    Optional<Admin> findByUsernameOrEmail(@Param("login") String login);
}

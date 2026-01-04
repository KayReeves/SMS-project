package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByNameAndActiveTrue(String name);

    boolean existsByName(String name);
}
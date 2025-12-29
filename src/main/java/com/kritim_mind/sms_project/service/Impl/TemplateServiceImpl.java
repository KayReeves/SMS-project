package com.kritim_mind.sms_project.service.Impl;

import com.kritim_mind.sms_project.exception.ResourceNotFoundException;
import com.kritim_mind.sms_project.model.Template;
import com.kritim_mind.sms_project.repository.TemplateRepository;
import com.kritim_mind.sms_project.service.Interface.TemplateService;
import com.kritim_mind.sms_project.utils.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    @Override
    public Template createTemplate(Template template) {

        if (templateRepository.existsByName(template.getName())) {
            throw new DuplicateResourceException("Template with this name already exists");
        }

        return templateRepository.save(template);
    }

    @Override
    public Template getTemplateByName(String name) {
        return templateRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
    }

    @Override
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public Template updateTemplate(Long id, Template updatedTemplate) {

        Template existing = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        existing.setName(updatedTemplate.getName());
        existing.setContent(updatedTemplate.getContent());
        existing.setDescription(updatedTemplate.getDescription());
        existing.setActive(updatedTemplate.isActive());

        return templateRepository.save(existing);
    }

    @Override
    public void deactivateTemplate(Long id) {

        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        template.setActive(false);
        templateRepository.save(template);
    }
}

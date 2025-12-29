package com.kritim_mind.sms_project.service.Interface;

import com.kritim_mind.sms_project.model.Template;

import java.util.List;

public interface TemplateService {

    Template createTemplate(Template template);

    Template getTemplateByName(String name);

    List<Template> getAllTemplates();

    Template updateTemplate(Long id, Template template);

    void deactivateTemplate(Long id);
}

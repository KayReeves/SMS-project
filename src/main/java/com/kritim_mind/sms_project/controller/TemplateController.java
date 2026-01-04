package com.kritim_mind.sms_project.controller;

import com.kritim_mind.sms_project.dto.response.ApiResponse;
import com.kritim_mind.sms_project.model.Template;
import com.kritim_mind.sms_project.service.Interface.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;


    @PostMapping
    public ResponseEntity<ApiResponse<Template>> createTemplate(
            @RequestBody Template template
    ) {
        Template created = templateService.createTemplate(template);
        return ResponseEntity.ok(
                ApiResponse.success("Template created successfully", created)
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<Template>> getTemplate(
            @PathVariable String name
    ) {
        Template template = templateService.getTemplateByName(name);
        return ResponseEntity.ok(
                ApiResponse.success("Template fetched successfully", template)
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<Template>>> getAllTemplates() {
        List<Template> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(
                ApiResponse.success("Templates fetched successfully", templates)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Template>> updateTemplate(
            @PathVariable Long id,
            @RequestBody Template template
    ) {
        Template updated = templateService.updateTemplate(id, template);
        return ResponseEntity.ok(
                ApiResponse.success("Template updated successfully", updated)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateTemplate(
            @PathVariable Long id
    ) {
        templateService.deactivateTemplate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Template deactivated successfully", null)
        );
    }
}
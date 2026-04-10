package com.nissens.imaging.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nissens.imaging.entity.ProductProject;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class ProjectPromptService {

    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;

    public ProjectPromptService(PromptBuilderService promptBuilderService, ObjectMapper objectMapper) {
        this.promptBuilderService = promptBuilderService;
        this.objectMapper = objectMapper;
    }

    public String buildPromptForProject(ProductProject project) {
        Map<String, String> inputs = parseStructuredInputs(project.getStructuredInputsJson());

        String structuredPrompt = promptBuilderService.buildStructuredPrompt(
                project.getCategory(),
                project.getSubcategory(),
                inputs
        );

        String analysisText = safe(project.getAnalysisSummary());

        if (!analysisText.isBlank()) {
            return structuredPrompt + "\n\nProduct analysis context:\n" + analysisText;
        }

        return structuredPrompt;
    }

    private Map<String, String> parseStructuredInputs(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
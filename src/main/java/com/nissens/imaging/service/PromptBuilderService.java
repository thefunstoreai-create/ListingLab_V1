package com.nissens.imaging.service;

import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderService {

    public String buildPrompt(ProductProject project, List<ReferenceImage> refs) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a clean ecommerce product image. ");
        prompt.append("Product name: ").append(project.getProductName()).append(". ");
        prompt.append("Category: ").append(project.getProductCategory()).append(". ");
        prompt.append("Style preset: ").append(project.getStylePreset()).append(". ");

        if (project.getNotes() != null && !project.getNotes().isBlank()) {
            prompt.append("Important notes: ").append(project.getNotes()).append(". ");
        }

        prompt.append("Preserve the product's core structure based on uploaded reference images. ");
        prompt.append("Do not invent extra decorative features. ");
        prompt.append("Keep dimensions and proportions realistic. ");

        if (refs != null && !refs.isEmpty()) {
            prompt.append("Reference image count: ").append(refs.size()).append(". ");
        }

        return prompt.toString();
    }
}
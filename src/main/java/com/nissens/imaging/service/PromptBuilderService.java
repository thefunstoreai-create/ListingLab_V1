package com.nissens.imaging.service;

import com.nissens.imaging.catalog.CategoryConfigService;
import com.nissens.imaging.catalog.StructuredProductInputService;
import com.nissens.imaging.catalog.SubcategoryDefinition;
import com.nissens.imaging.entity.ProductAnalysis;
import com.nissens.imaging.entity.ProductProject;
import com.nissens.imaging.entity.ReferenceImage;
import com.nissens.imaging.entity.StylePreset;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PromptBuilderService {

    private final CategoryConfigService categoryConfigService;
    private final StructuredProductInputService structuredProductInputService;

    public PromptBuilderService(CategoryConfigService categoryConfigService,
                                StructuredProductInputService structuredProductInputService) {
        this.categoryConfigService = categoryConfigService;
        this.structuredProductInputService = structuredProductInputService;
    }

    public String buildPrompt(ProductProject project,
                              List<ReferenceImage> refs,
                              StylePreset stylePreset,
                              ProductAnalysis analysis) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a clean ecommerce product image. ");
        prompt.append("Product name: ").append(project.getProductName()).append(". ");
        prompt.append("Category: ").append(project.getProductCategory()).append(". ");
        prompt.append("Style preset: ").append(stylePreset).append(". ");

        if (project.getNotes() != null && !project.getNotes().isBlank()) {
            prompt.append("Important notes: ").append(project.getNotes()).append(". ");
        }

        if (analysis != null) {
            appendAnalysis(prompt, analysis);
        }

        prompt.append("Preserve the product's core structure based on uploaded reference images. ");
        prompt.append("Do not invent extra decorative features. ");
        prompt.append("Keep dimensions and proportions realistic. ");

        if (refs != null && !refs.isEmpty()) {
            prompt.append("Reference image count: ").append(refs.size()).append(". ");
        }

        return prompt.toString();
    }

    public String buildStructuredPrompt(String categoryName,
                                        String subcategoryName,
                                        Map<String, String> inputs) {
        SubcategoryDefinition subcategory = categoryConfigService.getSubcategory(categoryName, subcategoryName);

        if (subcategory == null) {
            throw new IllegalArgumentException("Unknown category/subcategory: " + categoryName + " / " + subcategoryName);
        }

        return structuredProductInputService.applyTemplate(subcategory.getPromptTemplate(), inputs);
    }

    private void appendAnalysis(StringBuilder prompt, ProductAnalysis analysis) {
        if (analysis.getDetectedProductType() != null && !analysis.getDetectedProductType().isBlank()) {
            prompt.append("Detected product type: ").append(analysis.getDetectedProductType()).append(". ");
        }

        if (analysis.getDetectedPrimaryColor() != null && !analysis.getDetectedPrimaryColor().isBlank()) {
            prompt.append("Detected primary color: ").append(analysis.getDetectedPrimaryColor()).append(". ");
        }

        if (analysis.getDetectedMaterial() != null && !analysis.getDetectedMaterial().isBlank()) {
            prompt.append("Detected material: ").append(analysis.getDetectedMaterial()).append(". ");
        }

        if (analysis.getDetectedStyle() != null && !analysis.getDetectedStyle().isBlank()) {
            prompt.append("Detected product style: ").append(analysis.getDetectedStyle()).append(". ");
        }

        if (analysis.getDetectedBrandText() != null && !analysis.getDetectedBrandText().isBlank()) {
            prompt.append("Detected brand text: ").append(analysis.getDetectedBrandText()).append(". ");
        }

        if (analysis.getPackagingType() != null && !analysis.getPackagingType().isBlank()) {
            prompt.append("Packaging type: ").append(analysis.getPackagingType()).append(". ");
        }

        if (analysis.getAnalysisNotes() != null && !analysis.getAnalysisNotes().isBlank()) {
            prompt.append("Analysis notes: ").append(analysis.getAnalysisNotes()).append(". ");
        }
    }
}
package com.nissens.imaging.controller;

import com.nissens.imaging.catalog.CategoryConfigService;
import com.nissens.imaging.catalog.CategoryDefinition;
import com.nissens.imaging.catalog.FieldDefinition;
import com.nissens.imaging.catalog.SubcategoryDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalog")
public class CatalogApiController {

    private final CategoryConfigService categoryConfigService;

    public CatalogApiController(CategoryConfigService categoryConfigService) {
        this.categoryConfigService = categoryConfigService;
    }

    @GetMapping("/categories")
    public List<CategoryDefinition> getCategories() {
        return categoryConfigService.getAllCategories();
    }

    @GetMapping("/subcategories")
    public ResponseEntity<List<SubcategoryDefinition>> getSubcategories(
            @RequestParam String categoryName
    ) {
        Optional<CategoryDefinition> categoryOpt = categoryConfigService.findCategoryByName(categoryName);

        return categoryOpt
                .map(category -> ResponseEntity.ok(category.getSubcategories()))
                .orElseGet(() -> ResponseEntity.ok(Collections.emptyList()));
    }

    @GetMapping("/fields")
    public ResponseEntity<List<FieldDefinition>> getFields(
            @RequestParam String categoryName,
            @RequestParam String subcategoryName
    ) {
        Optional<SubcategoryDefinition> subcategoryOpt =
                categoryConfigService.findSubcategory(categoryName, subcategoryName);

        return subcategoryOpt
                .map(subcategory -> ResponseEntity.ok(subcategory.getFields()))
                .orElseGet(() -> ResponseEntity.ok(Collections.emptyList()));
    }
}
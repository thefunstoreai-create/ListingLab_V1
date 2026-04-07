package com.nissens.imaging.catalog;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryConfigService {

    public List<CategoryDefinition> getAllCategories() {
        return List.of(
                buildJewelleryCategory(),
                buildDisposablesCategory()
        );
    }

    public CategoryDefinition getCategory(String categoryName) {
        return getAllCategories().stream()
                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .orElse(null);
    }

    public SubcategoryDefinition getSubcategory(String categoryName, String subcategoryName) {
        CategoryDefinition category = getCategory(categoryName);
        if (category == null) {
            return null;
        }

        return category.getSubcategories().stream()
                .filter(s -> s.getName().equalsIgnoreCase(subcategoryName))
                .findFirst()
                .orElse(null);
    }

    private CategoryDefinition buildJewelleryCategory() {
        SubcategoryDefinition rings = new SubcategoryDefinition(
                "Rings",
                List.of(
                        new FieldDefinition("material", "Material", "select", true,
                                Arrays.asList("Sterling Silver", "Gold-tone Metal", "Titanium", "Stainless Steel")),
                        new FieldDefinition("colour", "Colour", "select", true,
                                Arrays.asList("Silver", "Gold", "Rose Gold", "Black")),
                        new FieldDefinition("style", "Style", "select", true,
                                Arrays.asList("Minimalist", "Bold", "Classic", "Modern")),
                        new FieldDefinition("target", "Target", "select", true,
                                Arrays.asList("Retail", "Wholesale"))
                ),
                """
                Generate a clean ecommerce product image.

                Product: {material} {style} ring in {colour}.

                Target: {target}.

                Style:
                - Jewellery catalogue
                - Soft studio lighting
                - Close-up macro
                - Clean white or neutral background

                Constraints:
                - Maintain exact structure from reference images
                - No added features
                - Realistic reflections and metal texture
                """
        );

        SubcategoryDefinition noseRings = new SubcategoryDefinition(
                "Nose Rings",
                List.of(
                        new FieldDefinition("material", "Material", "select", true,
                                Arrays.asList("Stainless Steel", "Titanium", "Gold-tone Metal", "Sterling Silver")),
                        new FieldDefinition("colour", "Colour", "select", true,
                                Arrays.asList("Silver", "Gold", "Black", "Rose Gold")),
                        new FieldDefinition("style", "Style", "select", true,
                                Arrays.asList("Minimalist", "Hoop", "Stud", "Bold")),
                        new FieldDefinition("target", "Target", "select", true,
                                Arrays.asList("Retail", "Wholesale"))
                ),
                """
                Generate a clean ecommerce product image.

                Product: {material} {style} nose ring in {colour}.

                Target: {target}.

                Style:
                - Jewellery catalogue
                - Soft studio lighting
                - Close-up macro
                - Clean white or neutral background

                Constraints:
                - Maintain exact structure from reference images
                - No added features
                - Realistic reflections and metal texture
                """
        );

        return new CategoryDefinition("Jewellery", List.of(rings, noseRings));
    }

    private CategoryDefinition buildDisposablesCategory() {
        SubcategoryDefinition paperCups = new SubcategoryDefinition(
                "Paper Cups",
                List.of(
                        new FieldDefinition("size", "Size", "select", true,
                                Arrays.asList("250ml", "350ml", "500ml")),
                        new FieldDefinition("colourOrPrint", "Colour / Print", "text", true, List.of()),
                        new FieldDefinition("material", "Material", "select", true,
                                Arrays.asList("Single Wall", "Ripple", "Double Wall")),
                        new FieldDefinition("packSize", "Pack Size", "select", true,
                                Arrays.asList("50", "100", "200"))
                ),
                """
                Generate a clean ecommerce product image.

                Product: {size} {material} paper cups, pack of {packSize}, design {colourOrPrint}.

                Style:
                - Wholesale catalogue
                - Neutral background
                - Slight perspective angle
                - Stack or grouped presentation

                Constraints:
                - Accurate proportions
                - No branding unless specified
                """
        );

        SubcategoryDefinition plasticCutlery = new SubcategoryDefinition(
                "Plastic Cutlery",
                List.of(
                        new FieldDefinition("type", "Type", "select", true,
                                Arrays.asList("Forks", "Knives", "Spoons", "Mixed Set")),
                        new FieldDefinition("colour", "Colour", "select", true,
                                Arrays.asList("White", "Black", "Clear", "Silver")),
                        new FieldDefinition("material", "Material", "select", true,
                                Arrays.asList("Plastic", "Heavy Duty Plastic", "Compostable")),
                        new FieldDefinition("packSize", "Pack Size", "select", true,
                                Arrays.asList("25", "50", "100", "200"))
                ),
                """
                Generate a clean ecommerce product image.

                Product: {colour} {material} {type}, pack of {packSize}.

                Style:
                - Wholesale catalogue
                - Neutral background
                - Clean grouped arrangement
                - Clear visibility of quantity and type

                Constraints:
                - Accurate proportions
                - No extra packaging unless specified
                """
        );

        return new CategoryDefinition("Disposables", List.of(paperCups, plasticCutlery));
    }
}
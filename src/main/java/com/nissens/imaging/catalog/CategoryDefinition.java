package com.nissens.imaging.catalog;

import java.util.List;

public class CategoryDefinition {

    private String name;
    private List<SubcategoryDefinition> subcategories;

    public CategoryDefinition(String name, List<SubcategoryDefinition> subcategories) {
        this.name = name;
        this.subcategories = subcategories;
    }

    public String getName() {
        return name;
    }

    public List<SubcategoryDefinition> getSubcategories() {
        return subcategories;
    }
}
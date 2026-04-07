package com.nissens.imaging.catalog;

import java.util.List;

public class SubcategoryDefinition {

    private String name;
    private List<FieldDefinition> fields;
    private String promptTemplate;

    public SubcategoryDefinition(String name, List<FieldDefinition> fields, String promptTemplate) {
        this.name = name;
        this.fields = fields;
        this.promptTemplate = promptTemplate;
    }

    public String getName() {
        return name;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public String getPromptTemplate() {
        return promptTemplate;
    }
}
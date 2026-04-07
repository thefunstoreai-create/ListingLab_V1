package com.nissens.imaging.catalog;

import java.util.List;

public class FieldDefinition {

    private String key;
    private String label;
    private String type;
    private boolean required;
    private List<String> options;

    public FieldDefinition(String key, String label, String type, boolean required, List<String> options) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.required = required;
        this.options = options;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getOptions() {
        return options;
    }
}
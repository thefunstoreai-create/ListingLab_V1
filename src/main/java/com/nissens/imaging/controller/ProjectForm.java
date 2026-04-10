package com.nissens.imaging.controller;

public class ProjectForm {

    private String projectName;
    private String category;
    private String subcategory;
    private String structuredInputsJson;

    public ProjectForm() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getStructuredInputsJson() {
        return structuredInputsJson;
    }

    public void setStructuredInputsJson(String structuredInputsJson) {
        this.structuredInputsJson = structuredInputsJson;
    }
}
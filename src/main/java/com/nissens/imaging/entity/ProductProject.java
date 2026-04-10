package com.nissens.imaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_project")
public class ProductProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    @Column
    private String productName;

    @Column
    private String productCategory;

    @Column
    private String stylePreset;

    @Column(nullable = false)
    private String category;

    @Column
    private String subcategory;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String structuredInputsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String analysisSummary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String generationPrompt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public ProductProject() {
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getStylePreset() {
        return stylePreset;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getStructuredInputsJson() {
        return structuredInputsJson;
    }

    public String getNotes() {
        return notes;
    }

    public String getAnalysisSummary() {
        return analysisSummary;
    }

    public String getGenerationPrompt() {
        return generationPrompt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setStylePreset(String stylePreset) {
        this.stylePreset = stylePreset;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setStructuredInputsJson(String structuredInputsJson) {
        this.structuredInputsJson = structuredInputsJson;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setAnalysisSummary(String analysisSummary) {
        this.analysisSummary = analysisSummary;
    }

    public void setGenerationPrompt(String generationPrompt) {
        this.generationPrompt = generationPrompt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
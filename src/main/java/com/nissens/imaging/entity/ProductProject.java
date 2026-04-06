package com.nissens.imaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ProductProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(length = 4000)
    private String notes;

    @Enumerated(EnumType.STRING)
    private StylePreset stylePreset;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public StylePreset getStylePreset() {
        return stylePreset;
    }

    public void setStylePreset(StylePreset stylePreset) {
        this.stylePreset = stylePreset;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
package com.nissens.imaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReferenceImage> referenceImages = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedImage> generatedImages = new ArrayList<>();

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

    public List<ReferenceImage> getReferenceImages() {
        return referenceImages;
    }

    public void setReferenceImages(List<ReferenceImage> referenceImages) {
        this.referenceImages = referenceImages;
    }

    public List<GeneratedImage> getGeneratedImages() {
        return generatedImages;
    }

    public void setGeneratedImages(List<GeneratedImage> generatedImages) {
        this.generatedImages = generatedImages;
    }

    @Transient
    public String getCoverImagePath() {
        if (referenceImages != null && !referenceImages.isEmpty()) {
            for (ReferenceImage img : referenceImages) {
                if (img.getWebPath() != null && !img.getWebPath().isBlank()) {
                    return img.getWebPath();
                }
            }
        }

        if (generatedImages != null && !generatedImages.isEmpty()) {
            for (GeneratedImage img : generatedImages) {
                if (img.getWebPath() != null && !img.getWebPath().isBlank()) {
                    return img.getWebPath();
                }
            }
        }

        return "";
    }
}
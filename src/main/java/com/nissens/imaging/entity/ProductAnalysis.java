package com.nissens.imaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ProductAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private ProductProject project;

    private String detectedProductType;
    private String detectedPrimaryColor;
    private String detectedMaterial;
    private String detectedStyle;
    private String detectedBrandText;
    private String packagingType;

    @Column(length = 4000)
    private String analysisNotes;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ProductProject getProject() {
        return project;
    }

    public void setProject(ProductProject project) {
        this.project = project;
    }

    public String getDetectedProductType() {
        return detectedProductType;
    }

    public void setDetectedProductType(String detectedProductType) {
        this.detectedProductType = detectedProductType;
    }

    public String getDetectedPrimaryColor() {
        return detectedPrimaryColor;
    }

    public void setDetectedPrimaryColor(String detectedPrimaryColor) {
        this.detectedPrimaryColor = detectedPrimaryColor;
    }

    public String getDetectedMaterial() {
        return detectedMaterial;
    }

    public void setDetectedMaterial(String detectedMaterial) {
        this.detectedMaterial = detectedMaterial;
    }

    public String getDetectedStyle() {
        return detectedStyle;
    }

    public void setDetectedStyle(String detectedStyle) {
        this.detectedStyle = detectedStyle;
    }

    public String getDetectedBrandText() {
        return detectedBrandText;
    }

    public void setDetectedBrandText(String detectedBrandText) {
        this.detectedBrandText = detectedBrandText;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(String packagingType) {
        this.packagingType = packagingType;
    }

    public String getAnalysisNotes() {
        return analysisNotes;
    }

    public void setAnalysisNotes(String analysisNotes) {
        this.analysisNotes = analysisNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
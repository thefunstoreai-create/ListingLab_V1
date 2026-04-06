package com.nissens.imaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GeneratedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProductProject project;

    @Column(length = 8000)
    private String promptUsed;

    @Enumerated(EnumType.STRING)
    private StylePreset stylePreset;

    @Enumerated(EnumType.STRING)
    private GenerationStatus status;

    private String filePath;
    private String providerRequestId;
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = GenerationStatus.PENDING;
        }
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

    public String getPromptUsed() {
        return promptUsed;
    }

    public void setPromptUsed(String promptUsed) {
        this.promptUsed = promptUsed;
    }

    public StylePreset getStylePreset() {
        return stylePreset;
    }

    public void setStylePreset(StylePreset stylePreset) {
        this.stylePreset = stylePreset;
    }

    public GenerationStatus getStatus() {
        return status;
    }

    public void setStatus(GenerationStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getProviderRequestId() {
        return providerRequestId;
    }

    public void setProviderRequestId(String providerRequestId) {
        this.providerRequestId = providerRequestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
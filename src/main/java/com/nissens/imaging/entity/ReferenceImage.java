package com.nissens.imaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ReferenceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProductProject project;

    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private String filePath;
    private LocalDateTime uploadedAt;

    @PrePersist
    public void onUpload() {
        this.uploadedAt = LocalDateTime.now();
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

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    @Transient
    public String getWebPath() {
        if (filePath == null || filePath.isBlank()) {
            return "";
        }

        String normalized = filePath.replace("\\", "/");
        int idx = normalized.indexOf("/uploads/");
        if (idx >= 0) {
            return normalized.substring(idx);
        }

        idx = normalized.indexOf("uploads/");
        if (idx >= 0) {
            return "/" + normalized.substring(idx);
        }

        return "";
    }
}
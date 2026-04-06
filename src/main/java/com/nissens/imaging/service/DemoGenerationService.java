package com.nissens.imaging.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class DemoGenerationService {

    public String createDemoGeneratedImage(String sourceFilePath, Long projectId) {
        if (sourceFilePath == null || sourceFilePath.isBlank()) {
            return null;
        }

        try {
            Path source = Paths.get(sourceFilePath).toAbsolutePath().normalize();
            if (!Files.exists(source)) {
                return null;
            }

            Path outputDir = Paths.get("uploads", "generated", "project-" + projectId)
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(outputDir);

            String originalName = source.getFileName().toString();
            String newName = UUID.randomUUID() + "_generated_" + originalName;

            Path target = outputDir.resolve(newName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create demo generated image", e);
        }
    }
}
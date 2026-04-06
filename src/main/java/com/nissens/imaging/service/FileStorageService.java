package com.nissens.imaging.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.storage.root:uploads}")
    private String storageRoot;

    public String store(MultipartFile file, String subfolder) {
        try {
            Path root = Paths.get(storageRoot, subfolder).toAbsolutePath().normalize();
            Files.createDirectories(root);

            String safeName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = root.resolve(safeName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
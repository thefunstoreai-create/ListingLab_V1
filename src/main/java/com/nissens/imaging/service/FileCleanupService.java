package com.nissens.imaging.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Service
public class FileCleanupService {

    public void deleteIfExists(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    public void deleteDirectoryIfExists(String directoryPath) {
        if (directoryPath == null || directoryPath.isBlank()) {
            return;
        }

        try {
            Path dir = Paths.get(directoryPath).toAbsolutePath().normalize();
            if (!Files.exists(dir)) {
                return;
            }

            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete path: " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete directory: " + directoryPath, e);
        }
    }
}
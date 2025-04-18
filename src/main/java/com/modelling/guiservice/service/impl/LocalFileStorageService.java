package com.modelling.guiservice.service.impl;

import com.modelling.guiservice.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private final Path storagePath;
    private final String baseUrl;

    public LocalFileStorageService(
            @Value("${app.storage.local.path}") String storagePath,
            @Value("${app.storage.local.base-url}") String baseUrl) {
        this.storagePath = Paths.get(storagePath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        initializeStorageDirectory();
    }

    private void initializeStorageDirectory() {
        try {
            Files.createDirectories(storagePath);
            log.info("Local storage initialized at: {}", storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize local storage directory", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path targetPath = storagePath.resolve(fileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        String fileUrl = baseUrl + "/" + fileName;

        log.debug("File uploaded locally: {}", targetPath);
        return fileUrl;
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            fileUrls.add(uploadFile(file));
        }
        return fileUrls;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = storagePath.resolve(fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.debug("Deleted local file: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error deleting local file: {}", e.getMessage());
            throw new IOException("Failed to delete local file", e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID() + "_" +
                (originalFileName != null ? originalFileName.replace(" ", "_") : "file");
    }
}
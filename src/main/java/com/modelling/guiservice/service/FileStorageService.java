package com.modelling.guiservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {
    String uploadFile(MultipartFile file) throws IOException;

    List<String> uploadFiles(List<MultipartFile> files) throws IOException;

    void deleteFile(String fileUrl) throws IOException;
}

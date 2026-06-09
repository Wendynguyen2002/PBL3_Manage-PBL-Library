package com.example.pblManagement.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String pblClassId, Long groupId);

    Resource loadFile(String filePath);

    void deleteFile(String filePath);

    String getFileType(String fileName);
}

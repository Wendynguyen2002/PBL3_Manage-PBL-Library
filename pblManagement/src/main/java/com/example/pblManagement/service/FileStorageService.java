package com.example.pblManagement.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file, String pblClassId, Long groupId) throws IOException;

    Resource loadFile(String filePath) throws Exception;

    void deleteFile(String filePath) throws IOException;

    String getFileType(String fileName);
}

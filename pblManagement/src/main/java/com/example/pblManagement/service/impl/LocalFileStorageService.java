package com.example.pblManagement.service.impl;

import com.example.pblManagement.exceptions.FileStorageException;
import com.example.pblManagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LocalFileStorageService implements FileStorageService {
    @Value("${file.upload.dir:/app/uploads/final-reports}")
    private String uploadDir;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");

    @Override
    public String storeFile(MultipartFile file, String pblClassId, Long groupId) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String originalFileName = file.getOriginalFilename();
            String extension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName = String.format("class-%s_group-%d_%s%s",
                    pblClassId, groupId, timestamp, extension);

            Path classDir = Paths.get(uploadDir, pblClassId);
            if (!Files.exists(classDir)) {
                Files.createDirectories(classDir);
            }

            Path filePath = classDir.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return Paths.get(pblClassId, newFileName).toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file for class " + pblClassId + " group " + groupId, e);
        }
    }

    @Override
    public Resource loadFile(String filePath) {
        try {
            Path fullPath = Paths.get(uploadDir).resolve(filePath).normalize();
            Resource resource = new UrlResource(fullPath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("File not found: " + filePath);
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Invalid file path: " + filePath, e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path fullPath = Paths.get(uploadDir).resolve(filePath).normalize();
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file: " + filePath, e);
        }
    }

    @Override
    public String getFileType(String fileName) {
        if (fileName == null) return "UNKNOWN";
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".pdf")) return "PDF";
        if (lowerName.endsWith(".docx")) return "DOCX";
        if (lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx")) return "PPT";
        return "OTHER";
    }
}

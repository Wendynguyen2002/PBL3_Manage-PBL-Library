package com.example.pblManagement.service.impl;

import com.example.pblManagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String storeFile(MultipartFile file, String pblClassId, Long groupId) throws IOException {
        // Generate unique filename: class-{pblClassId}_group-{groupId}_{timestamp}.{ext}
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String newFileName = String.format("class-%s_group-%d_%s%s",
                pblClassId, groupId, timestamp, extension);

        // Create directory structure: {uploadDir}/{pblClassId}/
        Path classDir = Paths.get(uploadDir, pblClassId);
        if (!Files.exists(classDir)) {
            Files.createDirectories(classDir);
        }

        Path filePath = classDir.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for database storage
        return Paths.get(pblClassId, newFileName).toString();
    }

    @Override
    public Resource loadFile(String filePath) throws Exception {
        Path fullPath = Paths.get(uploadDir).resolve(filePath).normalize();
        Resource resource = new UrlResource(fullPath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new Exception("File not found: " + filePath);
        }
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Path fullPath = Paths.get(uploadDir).resolve(filePath).normalize();
        Files.deleteIfExists(fullPath);
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

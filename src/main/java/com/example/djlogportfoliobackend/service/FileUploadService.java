package com.example.djlogportfoliobackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        String fileExtension = getFileExtension(originalFileName);
        if (!isImageFile(fileExtension)) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        String fileName = UUID.randomUUID().toString() + "." + fileExtension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("File uploaded successfully: {}", fileName);

        return "/uploads/" + fileName;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    private boolean isImageFile(String extension) {
        return extension.equals("jpg") ||
               extension.equals("jpeg") ||
               extension.equals("png") ||
               extension.equals("gif") ||
               extension.equals("webp");
    }
}
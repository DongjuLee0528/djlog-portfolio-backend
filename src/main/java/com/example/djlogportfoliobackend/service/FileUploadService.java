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

/**
 * 파일 업로드 서비스
 * 이미지 파일의 업로드와 저장을 처리합니다.
 */
@Service
@Slf4j
public class FileUploadService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    /**
     * 이미지 파일 업로드
     * 업로드된 파일의 유효성을 검증하고 서버에 저장합니다.
     * 파일명은 UUID로 고유하게 생성됩니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 파일의 웹 경로
     * @throws IOException 파일 I/O 오류 시
     * @throws IllegalArgumentException 비어있는 파일, 잘못된 파일명, 지원하지 않는 파일 형식 시
     */
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

    /**
     * 파일 확장자 추출
     * 파일명에서 확장자를 추출하여 소문자로 변환합니다.
     *
     * @param fileName 파일명
     * @return 파일 확장자 (소문자)
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 이미지 파일 형식 유효성 검증
     * 지원되는 이미지 확장자인지 확인합니다.
     * 지원 형식: jpg, jpeg, png, gif, webp
     *
     * @param extension 파일 확장자
     * @return 이미지 파일 여부
     */
    private boolean isImageFile(String extension) {
        return extension.equals("jpg") ||
               extension.equals("jpeg") ||
               extension.equals("png") ||
               extension.equals("gif") ||
               extension.equals("webp");
    }
}
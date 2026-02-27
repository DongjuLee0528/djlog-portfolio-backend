package com.example.djlogportfoliobackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
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

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

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
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = sanitizeFileName(originalFileName);
        String fileExtension = getFileExtension(sanitizedFileName);

        validateFileExtension(fileExtension);
        validateMimeType(file);
        validateFileSize(file);
        validateFileContent(file);

        String fileName = UUID.randomUUID().toString() + "." + fileExtension;

        Path uploadPath = getSecureUploadPath();
        Path filePath = uploadPath.resolve(fileName);

        // Path traversal 공격 방지
        if (!filePath.normalize().startsWith(uploadPath.normalize())) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

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
    /**
     * 파일 기본 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }
    }

    /**
     * 파일명 보안 처리
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        // 위험한 문자 제거
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "");

        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 파일명입니다.");
        }

        return sanitized;
    }

    /**
     * 파일 확장자 검증
     */
    private void validateFileExtension(String extension) {
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. 허용된 형식: " + ALLOWED_EXTENSIONS);
        }
    }

    /**
     * MIME 타입 검증
     */
    private void validateMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 파일 타입입니다. 허용된 타입: " + ALLOWED_MIME_TYPES);
        }
    }

    /**
     * 파일 크기 검증
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 크기: " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        if (file.getSize() == 0) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
    }

    /**
     * 파일 내용 검증 (매직 넘버 체크)
     */
    private void validateFileContent(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = new byte[12];
            int bytesRead = inputStream.read(header);

            if (bytesRead < 4) {
                throw new IllegalArgumentException("파일이 손상되었거나 유효하지 않습니다.");
            }

            if (!isValidImageHeader(header)) {
                throw new IllegalArgumentException("이미지 파일의 형식이 올바르지 않습니다.");
            }
        }
    }

    /**
     * 이미지 파일 헤더 검증 (매직 넘버)
     */
    private boolean isValidImageHeader(byte[] header) {
        // JPEG
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) {
            return true;
        }
        // PNG
        if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
            return true;
        }
        // GIF
        if ((header[0] == 0x47 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x38) &&
            (header[4] == 0x37 || header[4] == 0x39) && header[5] == 0x61) {
            return true;
        }
        // WebP
        if (header[0] == 0x52 && header[1] == 0x49 && header[2] == 0x46 && header[3] == 0x46 &&
            header[8] == 0x57 && header[9] == 0x45 && header[10] == 0x42 && header[11] == 0x50) {
            return true;
        }

        return false;
    }

    /**
     * 안전한 업로드 경로 확보
     */
    private Path getSecureUploadPath() throws IOException {
        Path uploadPath = Paths.get(uploadDir).normalize().toAbsolutePath();

        // 업로드 디렉토리가 프로젝트 루트 외부를 가리키는지 확인
        Path projectRoot = Paths.get(System.getProperty("user.dir")).normalize().toAbsolutePath();
        if (!uploadPath.startsWith(projectRoot)) {
            log.warn("Upload path is outside project root: {}", uploadPath);
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        return uploadPath;
    }

}
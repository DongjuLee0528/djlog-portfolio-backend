package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 파일 업로드 REST API 컨트롤러
 *
 * 이미지 파일 업로드 기능을 제공한다.
 * 보안 검증과 로깅을 포함하며, 지원되는 파일 형식과 크기를 제한한다.
 *
 * 보안 기능:
 * - 파일 형식 및 크기 검증
 * - IP 주소 추적 및 로깅
 * - 보안 헤더 자동 추가
 * - CORS 제한 적용
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = {"${security.cors.allowed-origins}"})
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        Map<String, String> response = new HashMap<>();

        try {
            // 요청 로깅
            String clientIp = getClientIpAddress(request);
            log.info("File upload request from IP: {}, filename: {}, size: {} bytes",
                    clientIp, file.getOriginalFilename(), file.getSize());

            String imageUrl = fileUploadService.uploadFile(file);
            response.put("url", imageUrl);

            // 보안 헤더 추가
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Content-Type-Options", "nosniff");
            headers.add("X-Frame-Options", "DENY");
            headers.add("Content-Security-Policy", "default-src 'none'");

            return ResponseEntity.ok().headers(headers).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload attempt from IP {}: {}", getClientIpAddress(request), e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IOException e) {
            log.error("File upload failed from IP {}: {}", getClientIpAddress(request), e.getMessage(), e);
            response.put("error", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Unexpected error during file upload from IP {}: {}", getClientIpAddress(request), e.getMessage(), e);
            response.put("error", "예상치 못한 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
package com.example.djlogportfoliobackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Redis 기반 세션 관리 서비스
 * 사용자 세션 정보를 Redis에 저장하고 관리합니다.
 *
 * 주요 기능:
 * - 활성 세션 추적 및 관리
 * - 사용자별 로그인 이력 저장
 * - 동시 로그인 제한
 * - 세션 만료 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionManagementService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SESSION_PREFIX = "session:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";
    private static final String LOGIN_HISTORY_PREFIX = "login_history:";
    private static final Duration DEFAULT_SESSION_TTL = Duration.ofHours(24);
    private static final int MAX_SESSIONS_PER_USER = 3;

    /**
     * 사용자 세션 생성
     *
     * @param username 사용자명
     * @param sessionId 세션 ID (JWT 토큰 ID)
     * @param clientInfo 클라이언트 정보 (IP, User-Agent 등)
     */
    public void createSession(String username, String sessionId, SessionInfo clientInfo) {
        try {
            // 1. 세션 정보 저장
            String sessionKey = SESSION_PREFIX + sessionId;
            SessionData sessionData = SessionData.builder()
                    .username(username)
                    .sessionId(sessionId)
                    .clientIp(clientInfo.getClientIp())
                    .userAgent(clientInfo.getUserAgent())
                    .loginTime(LocalDateTime.now())
                    .lastAccessTime(LocalDateTime.now())
                    .build();

            redisTemplate.opsForValue().set(sessionKey, toJson(sessionData), DEFAULT_SESSION_TTL);

            // 2. 사용자별 활성 세션 목록 업데이트
            String userSessionsKey = USER_SESSIONS_PREFIX + username;
            redisTemplate.opsForSet().add(userSessionsKey, sessionId);
            redisTemplate.expire(userSessionsKey, DEFAULT_SESSION_TTL);

            // 3. 동시 로그인 제한 확인 및 처리
            limitConcurrentSessions(username);

            // 4. 로그인 이력 저장
            recordLoginHistory(username, clientInfo);

            log.info("Session created successfully: user={}, sessionId={}, clientIp={}",
                    username, sessionId, clientInfo.getClientIp());

        } catch (Exception e) {
            log.error("Failed to create session for user {}: {}", username, e.getMessage(), e);
        }
    }

    /**
     * 세션 정보 조회
     *
     * @param sessionId 세션 ID
     * @return 세션 데이터
     */
    public Optional<SessionData> getSession(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            String sessionJson = redisTemplate.opsForValue().get(sessionKey);

            if (sessionJson != null) {
                SessionData sessionData = fromJson(sessionJson, SessionData.class);
                // 마지막 접근 시간 업데이트
                updateLastAccessTime(sessionId);
                return Optional.of(sessionData);
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to get session {}: {}", sessionId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 세션 삭제 (로그아웃)
     *
     * @param sessionId 세션 ID
     */
    public void removeSession(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;

            // 세션 데이터 조회
            Optional<SessionData> sessionOpt = getSession(sessionId);
            if (sessionOpt.isPresent()) {
                String username = sessionOpt.get().getUsername();

                // 1. 세션 삭제
                redisTemplate.delete(sessionKey);

                // 2. 사용자 세션 목록에서 제거
                String userSessionsKey = USER_SESSIONS_PREFIX + username;
                redisTemplate.opsForSet().remove(userSessionsKey, sessionId);

                log.info("Session removed successfully: user={}, sessionId={}", username, sessionId);
            }

        } catch (Exception e) {
            log.error("Failed to remove session {}: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 사용자의 모든 세션 삭제 (강제 로그아웃)
     *
     * @param username 사용자명
     */
    public void removeAllUserSessions(String username) {
        try {
            String userSessionsKey = USER_SESSIONS_PREFIX + username;
            Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);

            if (sessionIds != null) {
                for (String sessionId : sessionIds) {
                    redisTemplate.delete(SESSION_PREFIX + sessionId);
                }
                redisTemplate.delete(userSessionsKey);

                log.info("All sessions removed for user: {}, count: {}", username, sessionIds.size());
            }

        } catch (Exception e) {
            log.error("Failed to remove all sessions for user {}: {}", username, e.getMessage());
        }
    }

    /**
     * 사용자의 활성 세션 목록 조회
     *
     * @param username 사용자명
     * @return 활성 세션 목록
     */
    public List<SessionData> getUserActiveSessions(String username) {
        try {
            String userSessionsKey = USER_SESSIONS_PREFIX + username;
            Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);

            if (sessionIds == null || sessionIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<SessionData> activeSessions = new ArrayList<>();
            for (String sessionId : sessionIds) {
                getSession(sessionId).ifPresent(activeSessions::add);
            }

            return activeSessions;

        } catch (Exception e) {
            log.error("Failed to get active sessions for user {}: {}", username, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 동시 로그인 제한 처리
     *
     * @param username 사용자명
     */
    private void limitConcurrentSessions(String username) {
        try {
            List<SessionData> activeSessions = getUserActiveSessions(username);

            if (activeSessions.size() > MAX_SESSIONS_PER_USER) {
                // 가장 오래된 세션부터 제거
                activeSessions.sort(Comparator.comparing(SessionData::getLastAccessTime));

                int sessionsToRemove = activeSessions.size() - MAX_SESSIONS_PER_USER;
                for (int i = 0; i < sessionsToRemove; i++) {
                    removeSession(activeSessions.get(i).getSessionId());
                }

                log.info("Removed {} old sessions for user {} due to concurrent login limit",
                        sessionsToRemove, username);
            }

        } catch (Exception e) {
            log.error("Failed to limit concurrent sessions for user {}: {}", username, e.getMessage());
        }
    }

    /**
     * 로그인 이력 기록
     *
     * @param username 사용자명
     * @param clientInfo 클라이언트 정보
     */
    private void recordLoginHistory(String username, SessionInfo clientInfo) {
        try {
            String historyKey = LOGIN_HISTORY_PREFIX + username;
            LoginHistoryEntry entry = LoginHistoryEntry.builder()
                    .timestamp(LocalDateTime.now())
                    .clientIp(clientInfo.getClientIp())
                    .userAgent(clientInfo.getUserAgent())
                    .build();

            redisTemplate.opsForList().leftPush(historyKey, toJson(entry));
            redisTemplate.opsForList().trim(historyKey, 0, 99); // 최대 100개 유지
            redisTemplate.expire(historyKey, Duration.ofDays(30));

        } catch (Exception e) {
            log.error("Failed to record login history for user {}: {}", username, e.getMessage());
        }
    }

    /**
     * 마지막 접근 시간 업데이트
     *
     * @param sessionId 세션 ID
     */
    private void updateLastAccessTime(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            String sessionJson = redisTemplate.opsForValue().get(sessionKey);

            if (sessionJson != null) {
                SessionData sessionData = fromJson(sessionJson, SessionData.class);
                sessionData.setLastAccessTime(LocalDateTime.now());

                redisTemplate.opsForValue().set(sessionKey, toJson(sessionData), DEFAULT_SESSION_TTL);
            }

        } catch (Exception e) {
            log.error("Failed to update last access time for session {}: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 세션 통계 조회
     *
     * @return 세션 통계
     */
    public SessionStats getSessionStats() {
        try {
            Set<String> allSessionKeys = redisTemplate.keys(SESSION_PREFIX + "*");
            Set<String> allUserSessionKeys = redisTemplate.keys(USER_SESSIONS_PREFIX + "*");

            return SessionStats.builder()
                    .totalActiveSessions(allSessionKeys != null ? allSessionKeys.size() : 0)
                    .totalActiveUsers(allUserSessionKeys != null ? allUserSessionKeys.size() : 0)
                    .build();

        } catch (Exception e) {
            log.error("Failed to get session stats: {}", e.getMessage());
            return SessionStats.builder().totalActiveSessions(0).totalActiveUsers(0).build();
        }
    }

    // JSON 직렬화/역직렬화 헬퍼 메서드
    private String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    // 내부 데이터 클래스들
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SessionData {
        private String username;
        private String sessionId;
        private String clientIp;
        private String userAgent;
        private LocalDateTime loginTime;
        private LocalDateTime lastAccessTime;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SessionInfo {
        private String clientIp;
        private String userAgent;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LoginHistoryEntry {
        private LocalDateTime timestamp;
        private String clientIp;
        private String userAgent;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SessionStats {
        private int totalActiveSessions;
        private int totalActiveUsers;
    }
}
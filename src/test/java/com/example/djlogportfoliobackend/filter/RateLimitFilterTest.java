package com.example.djlogportfoliobackend.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RateLimitFilter 테스트
 * 개선된 Rate Limiting 로직의 동작을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;
    private Cache<String, AtomicInteger> cache;

    @BeforeEach
    void setUp() {
        cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build();

        rateLimitFilter = new RateLimitFilter(cache);
        ReflectionTestUtils.setField(rateLimitFilter, "maxRequestsPerMinute", 5);
        ReflectionTestUtils.setField(rateLimitFilter, "rateLimitStrategy", "ip_uri");
        ReflectionTestUtils.setField(rateLimitFilter, "enableGlobalLimit", false);
    }

    @Test
    void testAtomicCountIncrement() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/profile");
        when(request.getMethod()).thenReturn("GET");

        // When - 여러 요청을 빠르게 실행
        for (int i = 0; i < 3; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }

        // Then - 정확히 3번 카운트되어야 함
        String key = "rate_limit:ip_uri:127.0.0.1:/api/profile";
        AtomicInteger counter = cache.getIfPresent(key);
        assertNotNull(counter);
        assertEquals(3, counter.get());
        verify(filterChain, times(3)).doFilter(request, response);
    }

    @Test
    void testRateLimitExceeded() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/profile");
        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // When - 제한을 초과하는 요청
        for (int i = 0; i < 6; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }

        // Then - 마지막 요청은 차단되어야 함
        verify(response).setStatus(429);
        verify(filterChain, times(5)).doFilter(request, response);
    }

    @Test
    void testDifferentUrisSeparateCounters() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getMethod()).thenReturn("GET");

        // When - 다른 URI로 요청
        when(request.getRequestURI()).thenReturn("/api/profile");
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        when(request.getRequestURI()).thenReturn("/api/projects");
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        // Then - 각각 별도 카운터를 가져야 함
        AtomicInteger profileCounter = cache.getIfPresent("rate_limit:ip_uri:127.0.0.1:/api/profile");
        AtomicInteger projectsCounter = cache.getIfPresent("rate_limit:ip_uri:127.0.0.1:/api/projects");

        assertNotNull(profileCounter);
        assertNotNull(projectsCounter);
        assertEquals(1, profileCounter.get());
        assertEquals(1, projectsCounter.get());
    }
}
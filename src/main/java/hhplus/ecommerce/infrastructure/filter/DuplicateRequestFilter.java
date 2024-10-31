package hhplus.ecommerce.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import hhplus.ecommerce.application.common.ErrorCode;
import hhplus.ecommerce.application.common.ErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class DuplicateRequestFilter implements Filter {

    private static final long TIME_LIMIT = 500; // 0.5초 시간 제한
    private final Cache<String, Long> requestCache = Caffeine.newBuilder().expireAfterWrite(TIME_LIMIT, TimeUnit.MILLISECONDS).build();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String token = httpRequest.getHeader("Request-Token");

        System.out.println("Request-Token: " + token);

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        long currentTime = System.currentTimeMillis();
        Long lastRequestTime = requestCache.getIfPresent(token);

        if (lastRequestTime != null && (currentTime - lastRequestTime) < TIME_LIMIT) {
            ErrorCode errorCode = ErrorCode.DUPLICATE_REQUEST;
            ErrorResponse errorResponse = new ErrorResponse(errorCode);

            String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
            httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            try (PrintWriter writer = httpResponse.getWriter()) {
                writer.write(jsonResponse);
                writer.flush();
            }
            return;
        }

        // 요청 캐시 업데이트
        requestCache.put(token, currentTime);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}

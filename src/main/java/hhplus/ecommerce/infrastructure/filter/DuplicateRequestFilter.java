package hhplus.ecommerce.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DuplicateRequestFilter implements Filter {

    private final Map<String, Long> requestCache = new ConcurrentHashMap<>(); // ConcurrentHashMap으로 변경
    private static final long TIME_LIMIT = 500; // 0.5초 시간 제한

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String token = httpRequest.getHeader("Request-Token");

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(servletRequest, servletResponse); // 토큰이 없을 때는 그대로 통과
            return;
        }

        long currentTime = System.currentTimeMillis();
        Long lastRequestTime = requestCache.get(token);

        if (lastRequestTime != null && (currentTime - lastRequestTime) < TIME_LIMIT) {
            httpResponse.sendError(HttpServletResponse.SC_CONFLICT, "Duplicate request detected");
            return;
        }

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

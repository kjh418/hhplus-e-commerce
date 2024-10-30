package hhplus.ecommerce.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;


@Component
public class XSSFilter implements Filter {
    private static final Pattern XSS_PATTERN = Pattern.compile("<.*?>|<script.*?>|\"|'|%3C|%3E|%27|%22|javascript:|onerror|onclick", Pattern.CASE_INSENSITIVE);
    private static final Logger logger = LoggerFactory.getLogger(XSSFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if (containsXSS(httpRequest.getRequestURI()) || containsXSSInParameters(httpRequest) || containsXSSInHeaders(httpRequest)) {
            logger.warn("XSS 감지. 요청 URI={}, 파라미터={}, 헤더={}",
                    httpRequest.getRequestURI(), httpRequest.getParameterMap(), Collections.list(httpRequest.getHeaderNames()));
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 입력이 감지되었습니다.");
            return;
        }

        filterChain.doFilter(new XSSRequestWrapper(httpRequest), servletResponse);
    }

    private boolean containsXSS(String value) {
        return value != null && XSS_PATTERN.matcher(value).find();
    }

    private boolean containsXSSInParameters(HttpServletRequest request) {
        return request.getParameterMap().values().stream()
                .flatMap(java.util.Arrays::stream)
                .anyMatch(this::containsXSS);
    }

    private boolean containsXSSInHeaders(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream().anyMatch(name -> containsXSS(name) || containsXSS(request.getHeader(name)));
    }

    protected static class XSSRequestWrapper extends HttpServletRequestWrapper {

        public XSSRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values == null) return null;

            return java.util.Arrays.stream(values)
                    .map(this::sanitize)
                    .toArray(String[]::new);
        }

        @Override
        public String getParameter(String parameter) {
            return sanitize(super.getParameter(parameter));
        }

        @Override
        public String getHeader(String name) {
            return sanitize(super.getHeader(name));
        }

        private String sanitize(String value) {
            if (value == null) return null;
            return value.replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\\(", "&#40;")
                    .replaceAll("\\)", "&#41;")
                    .replaceAll("'", "&#39;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("eval\\((.*)\\)", "")
                    .replaceAll("[\"']\\s*javascript:(.*)[\"']", "\"\"")
                    .replaceAll("script", "");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
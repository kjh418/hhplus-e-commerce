package hhplus.ecommerce.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class XSSFilterTest {
    private XSSFilter xssFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        xssFilter = new XSSFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void URI에_악성스크립트가_포함된_경우_요청차단() throws ServletException, IOException {
        request.setRequestURI("/test<script>alert('xss')</script>");

        xssFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void 파라미터에_악성스크립트가_포함된_경우_요청차단() throws ServletException, IOException {
        request.setRequestURI("/test");
        request.setParameter("input", "<script>alert('xss')</script>");

        xssFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void 헤더에_악성스크립트가_포함된_경우_요청차단() throws ServletException, IOException {
        request.setRequestURI("/test");
        request.addHeader("X-Header", "<img src='' onerror='alert(1)' />");

        xssFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void 정상적인_입력이_들어온_경우_요청통과() throws ServletException, IOException {
        request.setRequestURI("/test");
        request.setParameter("input", "safeInput");

        xssFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(any(XSSFilter.XSSRequestWrapper.class), any(HttpServletResponse.class));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
}
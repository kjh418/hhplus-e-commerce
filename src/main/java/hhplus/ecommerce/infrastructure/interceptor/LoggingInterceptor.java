package hhplus.ecommerce.infrastructure.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();

        logger.info("Incoming request: URI = [{}], Method = [{}], IP = [{}]", requestURI, method, clientIp);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        logger.info("Request successfully handled. URI = [{}], Status = [{}]", request.getRequestURI(), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            logger.error("Request failed. URI = [{}], Error = [{}]", request.getRequestURI(), ex.getMessage(), ex);
        } else {
            logger.info("Request completed. URI = [{}], Status = [{}]", request.getRequestURI(), response.getStatus());
        }
    }
}

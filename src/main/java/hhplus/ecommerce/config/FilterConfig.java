package hhplus.ecommerce.config;

import hhplus.ecommerce.infrastructure.filter.DuplicateRequestFilter;
import hhplus.ecommerce.infrastructure.filter.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<XSSFilter> xssFilter() {
        FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XSSFilter());

        // 모든 URL 패턴에 적용
        registrationBean.addUrlPatterns("/*");

        // 필터의 우선순위 지정
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<DuplicateRequestFilter> duplicateRequestFilter() {
        FilterRegistrationBean<DuplicateRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DuplicateRequestFilter());

        registrationBean.addUrlPatterns("/payment/*", "/points/*/charge");

        registrationBean.setOrder(2);

        return registrationBean;
    }
}

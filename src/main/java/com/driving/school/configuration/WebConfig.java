package com.driving.school.configuration;

import com.driving.school.components.LoggedInUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoggedInUserInterceptor loggedInUserInterceptor;

    @Autowired
    public WebConfig(LoggedInUserInterceptor loggedInUserInterceptor) {
        this.loggedInUserInterceptor = loggedInUserInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggedInUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/loginUser", "/register", "/registerUser", "/assets/**", "/home");
    }
}

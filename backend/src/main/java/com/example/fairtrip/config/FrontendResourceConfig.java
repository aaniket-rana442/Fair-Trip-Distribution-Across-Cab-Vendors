package com.example.fairtrip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FrontendResourceConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/index.html", "/login.html")
                .addResourceLocations("classpath:/static/frontend/");
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/frontend/css/");
        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/frontend/js/");
        registry.addResourceHandler("/user/**")
            .addResourceLocations("classpath:/static/frontend/user/");
        registry.addResourceHandler("/admin/**")
            .addResourceLocations("classpath:/static/frontend/admin/");
        registry.addResourceHandler("/vendor/**")
            .addResourceLocations("classpath:/static/frontend/vendor/");
    }
}
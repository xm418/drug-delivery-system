package com.medical.drugdelivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 * 前端（Vue，跑在5173端口）和后端（8080端口）端口不一样，
 * 浏览器会拦跨域请求，所以这里统一放开
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域规则：允许所有来源、常用方法和所有请求头
     * allowCredentials开了之后，allowedOrigins不能写"*"，所以用allowedOriginPatterns
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // 预检请求结果缓存1小时，减少OPTIONS请求次数
    }
}

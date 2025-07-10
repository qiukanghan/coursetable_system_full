package com.example.coursetable_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端域名访问（生产环境需指定具体域名，而非 *）
        config.addAllowedOriginPattern("*");
        // 允许的请求方法（GET、POST、PUT等）
        config.addAllowedMethod("*");
        // 允许的请求头（如 Content-Type、Authorization 等）
        config.addAllowedHeader("*");
        // 允许前端携带 Cookie
        config.setAllowCredentials(true);
        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

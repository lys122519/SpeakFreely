package com.sf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 跨域配置
 * @author: leung
 * @date: 2022-03-25 21:59
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许证书（cookies）
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 跨域允许时间
                .maxAge(3600);

    }


    // 当前跨域请求最大有效时长。这里默认1天
    private static final long MAX_AGE = 24 * 60 * 60;

    //@Bean
    //public CorsFilter corsFilter() {
    //    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //    CorsConfiguration corsConfiguration = new CorsConfiguration();
    //    // 1 设置访问源地址
    //    //corsConfiguration.addAllowedOrigin("http://localhost:8080");
    //    //corsConfiguration.addAllowedOrigin("http://localhost:8082");
    //    //corsConfiguration.addAllowedOrigin("http://localhost:3000");
    //    //corsConfiguration.addAllowedOrigin("http://49.233.159.44");
    //    //corsConfiguration.addAllowedOrigin("https://itsmepcy.top:666");
    //    //corsConfiguration.addAllowedOrigin("https://itsmepcy.top");
    //    corsConfiguration.addAllowedOrigin("*");
    //    // 2 设置访问源请求头
    //    corsConfiguration.addAllowedHeader("*");
    //    // 3 设置访问源请求方法
    //    corsConfiguration.addAllowedMethod("*");
    //    corsConfiguration.setMaxAge(MAX_AGE);
    //    // 4 对接口配置跨域设置
    //    source.registerCorsConfiguration("/**", corsConfiguration);
    //    return new CorsFilter(source);
    //}
}
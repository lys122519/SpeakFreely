package com.sf.config;


import com.sf.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 拦截器配置
 * @author: leung
 * @date: 2022-03-28 14:48
 */

//@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .excludePathPatterns("/**/**");
                //拦截所有请求，判断token是否合法来决定是否需要登录
                //.addPathPatterns("/**")
                //.excludePathPatterns("/user/loginAccount","/user/loginEmail",
                //        "/user/forget",
                //        "/user/register", "/**/export", "/**/import", "/file/**");
                //.excludePathPatterns("/**/*.html", "/**/*.js", "/**/*.css", "/**/*.tff")
    }

    // 注入JwtInterceptor对象
    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
}

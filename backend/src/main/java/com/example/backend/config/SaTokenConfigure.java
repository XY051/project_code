package com.example.backend.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    /**
     * 注册 Sa-Token 全局过滤器（必须在 Spring Boot 3 中注册，否则上下文不生效）
     */
    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude("/login", "/admin/login", "/register")
                .setBeforeAuth(obj -> {
                    // 跨域配置
                    SaHolder.getResponse()
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
                            .setHeader("Access-Control-Max-Age", "864000")
                            .setHeader("Access-Control-Allow-Headers", "*");

                    // OPTIONS 预检请求直接放行
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .back();
                });
    }
    /**
     * JWT 集成
     */
    @Bean
    public StpLogic stpLogic() {
        return new StpLogicJwtForSimple();
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final String SIGN = "R2xvYmFsUGF0dGVybi0qKg==";
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**");
    }
}
package com.example.backend.config;

import com.example.backend.utils.SysCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!SysCache.isAllow() || SysCache.isBlack()) {
            response.setContentType("application/json;charset=utf-8");
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}

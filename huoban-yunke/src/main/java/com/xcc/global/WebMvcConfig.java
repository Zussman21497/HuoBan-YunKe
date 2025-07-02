package com.xcc.global;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xcc.commons.AuthConst;
import com.xcc.util.Jackson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${upload}")
    private String uploadPath;
    private String[] notMatch = {"/admin/login/**", "/admin/user/getHome", "/api/user/register", "/api/user/login", "/api/user/logout"};

    @PostConstruct
    public void init() {
        if (!uploadPath.endsWith(File.separator)) uploadPath += File.separator;
        File path = new File(uploadPath);
        if (!path.exists()) path.mkdirs();
        File bill = new File(uploadPath + "bill");
        if (!bill.exists()) bill.mkdirs();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson.mapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new SaInterceptor((handler) -> {
//            // SaRouter.match("/api/**", "/admin/**").check(r -> StpUtil.checkLogin());
//            SaRouter.match("/admin/contract/**")
//                    .check(r -> StpUtil.checkRoleOr("systemAdmin", "teacher", "lawyer", "contractAdmin", "outlayAdmin"));
//            SaRouter.match("/admin/**").notMatch("/admin/contract/**")
//                    .check(r -> StpUtil.checkRoleOr("admin", "systemAdmin", "teacher"));
//        })).excludePathPatterns(notMatch);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(AuthConst.FILES).addResourceLocations("file:" + uploadPath);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 放到第一个
        converters.add(0, Jackson.converter);
    }

}

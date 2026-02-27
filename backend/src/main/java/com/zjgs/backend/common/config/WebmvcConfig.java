package com.zjgs.backend.common.config;

//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebmvcConfig implements WebMvcConfigurer {


//    //获取图片上传地址路径
//    @Value("${file.upload-imgUrl}")
//    private String fileUploadImgUrl;
//
//
//    //获取excel地址
//    @Value("${file.excel-url}")
//    private String fileExcelUrl;


    //设置cors解决前后端跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
//    //添加静态资源过滤配置
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        //添加访问资源地址/images/**
//        registry.addResourceHandler("/images/**")
//                //添加资源真实地址指向到服务器
//                .addResourceLocations("file:" + fileUploadImgUrl);
//
//        //添加excel地址访问
//        registry.addResourceHandler("/excel/**")
//                .addResourceLocations("file:" + fileExcelUrl);
//    }
}

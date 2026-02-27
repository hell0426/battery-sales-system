package com.zjgs.backend.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //把当前配置类在springboot启动时加载进去
public class SwaggerConfig {
    /*
    配置swagger基本信息
     */
    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("在线教育接口文档")
                        .description("api接口文档")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("我的文档")
                        .url("https://blog.csdn.net/2202_76007821?spm=1000.2115.3001.5343"));
    }
}
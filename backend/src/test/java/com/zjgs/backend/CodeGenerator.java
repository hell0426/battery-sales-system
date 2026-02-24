package com.zjgs.backend;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.Collections;


public class CodeGenerator{

    @Test
    public void codeGenerator() {
        // 1. 数据库连接配置
        String url = "jdbc:mysql://localhost:3306/battery_sales?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String username = "root";
        String password = "123456"; // 你的数据库密码

        // 2. 项目路径配置
        String projectPath = "D:\\MyGraduationProject\\backend";

        try {
            FastAutoGenerator.create(url, username, password)
                    .globalConfig(builder -> builder
                            .author("Luke") // 作者名，改成你的名字也可以
                            .outputDir(projectPath + "\\src\\main\\java") // 输出目录
                            .commentDate("yyyy-MM-dd") // 注释日期格式
                            .enableSpringdoc() // 开启 Swagger 3.0 (SpringDoc) 模式
                            .disableOpenDir() // 生成完禁止自动打开文件夹
                    )
                    .packageConfig(builder -> builder
                            .parent("com.zjgs.backend") //  父包名
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .xml("mapper") // XML 放在 resources/mapper 下
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    projectPath + "\\src\\main\\resources\\mapper" //  XML 输出路径
                            ))
                    )
                    .strategyConfig(builder ->
                            builder.addInclude("product", "customer", "orders", "order_item") // 设置需要生成的4张表名
                                    .entityBuilder()
                                    .enableLombok() // 启用 Lombok
                                    .enableTableFieldAnnotation() // 启用字段注解
                                    .logicDeleteColumnName("is_deleted") //  自动识别逻辑删除字段

                                    .controllerBuilder()
                                    .enableRestStyle() // 启用 REST 风格 (@RestController)
                                    .enableFileOverride() // 允许覆盖旧文件
                    )
                    .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 引擎
                    .execute();

            System.out.println("======== 代码生成成功！ ========");
        } catch (Exception e) {
            System.out.println("======== 代码生成失败，请检查数据库配置 ========");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
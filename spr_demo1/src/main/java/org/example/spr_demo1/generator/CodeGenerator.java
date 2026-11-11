package org.example.spr_demo1.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器（实验二验收点：一键生成持久层全套基础代码）
 *
 * 使用方式：在 IDEA 里右键本类的 main 方法 → Run。
 * 前置条件：MySQL 已启动，medical_db 已按 sql/medical_db.sql 建表。
 *
 * 注意：输出目录故意指向 target/mp-generator，避免直接覆盖手写代码。
 * 生成后自己挑需要的文件拷进 src/main/java，或者先把 outputDir 改成 src/main/java 再跑。
 */
public class CodeGenerator {

    private static final String URL =
            "jdbc:mysql://localhost:3306/medical_db?useUnicode=true&characterEncoding=utf8"
                    + "&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        String outputDir = System.getProperty("user.dir") + "/target/mp-generator";

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                // 1. 全局配置
                .globalConfig(builder -> builder
                        .author("罗浩然")
                        .outputDir(outputDir)
                        .commentDate("yyyy-MM-dd")
                        // 生成后不自动弹开文件夹
                        .disableOpenDir())
                // 2. 包名配置
                .packageConfig(builder -> builder
                        .parent("org.example.spr_demo1")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .xml("mapper")
                        // XML 放到 resources 下
                        .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "/resources/mapper")))
                // 3. 表与策略配置
                .strategyConfig(builder -> builder
                        .addInclude("department", "doctor", "patient")
                        .addTablePrefix("t_")
                        // Entity 策略
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .logicDeleteColumnName("deleted")
                        // Mapper 策略
                        .mapperBuilder()
                        .enableBaseResultMap()
                        // Service 策略
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        // Controller 策略
                        .controllerBuilder()
                        .enableRestStyle())
                // 4. 指定模板引擎（3.5.x 必须显式引入 freemarker 依赖）
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}

package org.example.spr_demo1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类。
 * @MapperScan 统一扫描 Mapper 接口，省去每个接口上写 @Mapper（本工程两者都保留了，留一种即可）。
 */
@SpringBootApplication
@MapperScan("org.example.spr_demo1.mapper")
public class SprDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(SprDemo1Application.class, args);
    }

}

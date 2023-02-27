package com.example.zydemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.zydemo.file.mapper"})
public class ZydemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZydemoApplication.class, args);
    }

}

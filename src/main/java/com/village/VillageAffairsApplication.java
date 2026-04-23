package com.village;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.village.dao")
public class VillageAffairsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VillageAffairsApplication.class, args);
    }
}
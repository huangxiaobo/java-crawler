package com.huangxiaobo.crawler.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.common", "com.huangxiaobo.crawler.web"})
@MapperScan("com.huangxiaobo.crawler.common.mapper")
public class CrawlerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerWebApplication.class, args);
    }
}

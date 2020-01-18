package com.huangxiaobo.crawler.processor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.common", "com.huangxiaobo.crawler.processor"})
@MapperScan("com.huangxiaobo.crawler.common.mapper")
public class CrawlerProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerProcessorApplication.class, args);
    }
}

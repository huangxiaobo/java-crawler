package com.huangxiaobo.crawler.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.common"})
public class CrawlerProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerProcessorApplication.class, args);
    }
}

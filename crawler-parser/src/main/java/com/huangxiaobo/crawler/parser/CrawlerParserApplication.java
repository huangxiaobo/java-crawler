package com.huangxiaobo.crawler.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.*"})
public class CrawlerParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerParserApplication.class, args);
    }
}

package com.huangxiaobo.crawler.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.*"})
public class CrawlerWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrawlerWebApplication.class, args);
  }
}

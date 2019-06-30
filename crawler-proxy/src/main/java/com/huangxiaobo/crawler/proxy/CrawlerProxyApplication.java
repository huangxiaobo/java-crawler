package com.huangxiaobo.crawler.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.huangxiaobo.*"})
public class CrawlerProxyApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrawlerProxyApplication.class, args);
  }
}

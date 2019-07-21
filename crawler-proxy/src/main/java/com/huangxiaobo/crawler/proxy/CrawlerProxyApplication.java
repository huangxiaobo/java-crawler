package com.huangxiaobo.crawler.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.*"})
public class CrawlerProxyApplication {

  @Autowired
  private ProxyPoolManager proxyPoolManager;

  public static void main(String[] args) {
    SpringApplication.run(CrawlerProxyApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onStart() {
    proxyPoolManager.start();
  }
}

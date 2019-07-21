package com.huangxiaobo.crawler.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 加载配置文件
 */
@Configuration
public class CrawlerProxyConfig {

  /**
   * 是否使用代理抓取
   */
  @Value("${spring.proxy.useProxy}")
  public boolean useProxy;
  /**
   * 代理文件
   */
  @Value("${spring.proxy.proxyPath}")
  public String proxyPath;
}

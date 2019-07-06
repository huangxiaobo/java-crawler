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

  /**
   * 下载网页线程数
   */
  @Value("${spring.fetch.threadSize}")
  public int fetchThreadSize;

  /**
   * 解析网页线程书
   */
  @Value("${spring.parse.threadSize}")
  public int parseThreadSize;

  /**
   * 爬虫入口
   */
  @Value("${spring.application.startUrl}")
  public String startURL;

  /**
   * user 保存路径
   */
  @Value("${spring.application.savePath}")
  public String savePath;
}

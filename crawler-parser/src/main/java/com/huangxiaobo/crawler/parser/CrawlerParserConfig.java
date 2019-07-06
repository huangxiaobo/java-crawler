package com.huangxiaobo.crawler.parser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class CrawlerParserConfig {

  /**
   * 解析网页线程书
   */
  @Value("${spring.parse.threadSize}")
  public int parseThreadSize;

  /**
   * user 保存路径
   */
  @Value("${spring.application.savePath}")
  public String savePath;

  @Bean
  public TaskExecutor parseTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(4);
    executor.setThreadNamePrefix("default_task_executor_thread");
    executor.initialize();
    return executor;
  }
}

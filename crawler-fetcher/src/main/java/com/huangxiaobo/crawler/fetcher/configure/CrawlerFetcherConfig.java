package com.huangxiaobo.crawler.fetcher.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * 加载配置文件
 */
@Configuration
public class CrawlerFetcherConfig {

    /**
     * 下载网页线程数
     */
    @Value("${spring.fetcher.threadSize}")
    public int fetchThreadSize;

    /**
     * 解析网页线程书
     */
    @Value("${spring.fetcher.threadSize}")
    public int threadSize;

    /**
     * 爬虫入口
     */
    @Value("#{'${spring.fetcher.startUrls}'.split(',')}")
    public List<String> startURLs;

    /**
     * user 保存路径
     */
    @Value("${spring.fetcher.proxyUrl}")
    public String proxyUrl;

    @Bean
    public TaskExecutor fetchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return executor;
    }
}

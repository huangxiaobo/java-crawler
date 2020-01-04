package com.huangxiaobo.crawler.processor;

import org.springframework.beans.factory.annotation.Value;

public class CrawlerProcessorConfig {

    /**
     * user 保存路径
     */
    @Value("${spring.application.savePath}")
    public String savePath;
}

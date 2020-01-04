package com.huangxiaobo.crawler.common;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
抓取任务，通过mq传递
 */

@Data
public class FetcherTask {

    private String x;

    private static Logger logger = LoggerFactory.getLogger(FetcherTask.class);
    public String url;
    public String fetcherClassName;
    public String parserClassName;

    public FetcherTask(String url, String fetcherClassName, String parserClassName) {
        this.url = url;
        this.fetcherClassName = fetcherClassName;
        this.parserClassName = parserClassName;
    }

    public String toString() {
        return String.format("FetcherTask(url=%s,fetcherClassName=%s,parserClassName=%s)",
                url, fetcherClassName, parserClassName
        );
    }
}
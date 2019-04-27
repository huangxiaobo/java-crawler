package com.crawler.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
抓取任务，通过mq传递
 */
public class FetcherTask {

    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);
    public String url;
    public boolean useProxy;//是否通过代理下载
    public String fetcherClassName;

    public FetcherTask(String url, boolean useProxy, String fetcherClassName) {
        this.url = url;
        this.useProxy = useProxy;
        this.fetcherClassName = fetcherClassName;
    }


    public String getUrl() {
        if (null != this.url) {
            return this.url;
        }
        return null;
    }

    public boolean getUserProxy() {
        return useProxy;
    }
}
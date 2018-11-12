package com.crawler.fetcher;

import com.crawler.Constants;
import com.crawler.utils.HttpClientUtil;
import com.crawler.element.Page;
import com.crawler.proxy.Direct;
import com.crawler.proxy.Proxy;
import com.crawler.proxy.ProxyPool;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetcherTask {
    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);
    protected String url;
    protected HttpRequestBase request;
    protected boolean useProxy;//是否通过代理下载
    public Class fetcherClass;

    public FetcherTask(String url, boolean useProxy, Class fetcherClass) {
        this.url = url;
        this.useProxy = useProxy;
        this.fetcherClass = fetcherClass;
    }


    public FetcherTask(HttpRequestBase request, Class fetcherClass) {
        this.request = request;
        this.fetcherClass = fetcherClass;

        this.url = this.request.getURI().toString();
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
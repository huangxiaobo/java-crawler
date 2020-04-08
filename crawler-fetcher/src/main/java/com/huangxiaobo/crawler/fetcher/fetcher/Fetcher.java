package com.huangxiaobo.crawler.fetcher.fetcher;

import com.huangxiaobo.crawler.common.FetcherTask;
import com.huangxiaobo.crawler.common.HttpClientUtil;
import com.huangxiaobo.crawler.common.Page;
import com.huangxiaobo.crawler.common.ParseTask;
import com.huangxiaobo.crawler.fetcher.service.FetcherService;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * page task 下载网页并解析，具体解析由子类实现 若使用代理，从ProxyPool中取
 */
public class Fetcher implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);
    protected FetcherTask fetcherTask;
    protected HttpRequestBase request;

    protected FetcherService fetcherManager;

    public Fetcher(FetcherTask task) {
        this.fetcherTask = task;
    }

    public void setFetcherManager(FetcherService fetcherManager) {
        this.fetcherManager = fetcherManager;
    }

    public void run() {
        Thread.currentThread().setName("fetcher-thread-task");
        logger.info("url: " + fetcherTask.getUrl());
        String url = fetcherTask.getUrl();
        boolean success = false;
        try {
            if (url != null) {
                request = new HttpGet(url);
            }

            String currentProxy = fetcherManager.getProxy();
            int len = currentProxy.length();
            currentProxy = currentProxy.substring(1, len - 1);
            String[] address = currentProxy.split(":");
            logger.info(String.format("===============> ip=%s, port=%s", address[0], address[1]));

            HttpHost proxy = new HttpHost(address[0], Integer.valueOf(address[1]));
            request.setConfig(HttpClientUtil.getRequestConfigBuilder().setProxy(proxy).build());

            long requestStartTime = System.currentTimeMillis();
            Page page = new Page(HttpClientUtil.getWebPage(request));
            long requestEndTime = System.currentTimeMillis();
            long requestCostTime = requestEndTime - requestStartTime;

            int status = page.getStatusCode();

            String logStr = Thread.currentThread().getName() + " " + currentProxy +
                    "  executing request " + page.getUrl() + " response statusCode:" + status +
                    "  request cost time:" + requestCostTime + "ms";

            logger.warn(logStr);
            if (status == HttpStatus.SC_OK) {
                if (page.getHtml().contains("zhihu") && !page.getHtml().contains("安全验证")) {
                    logger.debug(logStr);
                    success = true;
                    parse(page);
                } else {
                    // 代理异常，没有正确返回目标url
                    logger.warn("proxy exception:" + currentProxy.toString());
                    logger.warn("page" + page.getHtml());
                }
            }
        } catch (Exception e) {
            logger.error(String
                    .format("request to %s due to exception %s", url, e.getClass().getSimpleName()));
        } finally {
            logger.info(String.format("request to %s %s.", url, success));
            if (!success) {
                retry();
            }
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    /**
     * 如果下载page失败，retry
     */
    protected void retry() {
        this.fetcherManager.addFetchTask(fetcherTask, true);
    }

    /**
     * 下载page成功后的解析，子类实现page的处理
     */
    protected void parse(Page page) {
        // 加入待解析队列
        this.fetcherManager.addParseTask(new ParseTask(page.getHtml(), this.fetcherTask.getParserClassName()));
    }
}

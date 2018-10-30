package com.crawler.proxy.task;

import com.crawler.Constants;
import com.crawler.zhihu.element.Page;
import com.crawler.Spider;
import com.crawler.proxy.Proxy;
import com.crawler.proxy.ProxyPool;
import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/6.
 */
public class ProxyTestTask implements Runnable{
    private Logger logger = LoggerFactory.getLogger(ProxyTestTask.class);
    private Proxy proxy;

    public ProxyTestTask(Proxy proxy) {
        this.proxy = proxy;
    }

    public void run() {

        long startTime = System.currentTimeMillis();
        HttpGet request = new HttpGet(Constants.INDEX_URL);
        try{
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT).
                setConnectionRequestTimeout(Constants.TIMEOUT).
                setConnectTimeout(Constants.TIMEOUT).
                setProxy(new HttpHost(proxy.getIp(), proxy.getPort())).
                setCookieSpec(CookieSpecs.STANDARD).
                build();
            request.setConfig(requestConfig);
            Page page = Spider.getInstance().getWebPage(request);

            long endTime = System.currentTimeMillis();

            String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() +
                "  executing request " + page.getUrl()  + " response statusCode:" + page.getStatusCode() +
                "  request cost time:" + (endTime - startTime) + "ms";

            if (page == null|| page.getStatusCode() != 200) {
                logger.warn(logStr);
                return;
            }
            logger.info("available proxy: " + proxy.toString());

            request.releaseConnection();
            ProxyPool.proxyQueue.add(proxy);

        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }
    }

}

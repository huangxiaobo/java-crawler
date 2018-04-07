package com.crawl.proxy;

import com.crawl.Config;
import com.crawl.element.Page;
import com.crawl.HttpClientUtil;
import com.crawl.proxy.task.ProxyParseTask;
import com.crawl.proxy.task.ProxySerializeTask;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.crawl.Constants.TIME_INTERVAL;

/**
 * Created by hxb on 2018/4/6.
 */
public class ProxyHttpClient {

    private Logger logger = LoggerFactory.getLogger(ProxyHttpClient.class);

    private static ProxyHttpClient instance;
    public ThreadPoolExecutor proxyTestExecutor;
    public ThreadPoolExecutor proxyExecutor;

    public static ProxyHttpClient getInstance() {
        if (instance == null) {
            instance = new ProxyHttpClient();
        }
        return instance;
    }

    public ProxyHttpClient() {

        initThreadPool();
        initProxy();
    }

    public void initThreadPool() {
        //创建线程池
        proxyTestExecutor = new ThreadPoolExecutor(100, 100,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1000),
            new ThreadPoolExecutor.DiscardPolicy());

        proxyExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>());
    }

    public void initProxy() {
        Proxy[] proxyArray = null;

        try{
            int availableProxyCount = 0;
            proxyArray =  (Proxy[])HttpClientUtil.deserializeObject(Config.proxyPath);
            for(Proxy p:proxyArray) {
                if (p == null) {
                    continue;
                }
                p.setTimeInterval(TIME_INTERVAL);
                p.setFailureTimes(0);
                p.setSuccessfulTimes(0);

                long nowTime = System.currentTimeMillis();
                if (nowTime - p.getLastSuccessfulTime() < 1000 * 60 * 60) {
                    ProxyPool.proxyQueue.add(p);
                    ProxyPool.proxySet.add(p);
                    availableProxyCount += 1;
                }
            }

            logger.info("available proxy count: " + availableProxyCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start() {
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        for (String url : ProxyPool.proxyMap.keySet()) {
                            proxyExecutor.execute(new ProxyParseTask(url, false));
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(1000 * 60 * 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        new Thread(new ProxySerializeTask()).start();
    }


    public Page getWebPage(String url) throws IOException {
        return getWebPage(url, "utf-8");
    }

    public Page getWebPage(String url, String charset) throws IOException {
        CloseableHttpResponse response = null;
        Page page = new Page();

        response = HttpClientUtil.getResponse(url);
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setUrl(url);
        try {
            if (page.getStatusCode() == 200) {
                page.setHtml(EntityUtils.toString(response.getEntity(), charset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return page;
    }

    public Page getWebPage(HttpRequestBase request) throws IOException {
        logger.info("request" + request);
        CloseableHttpResponse response = HttpClientUtil.getResponse(request);

        Page page = new Page();
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setHtml(EntityUtils.toString(response.getEntity()));
        page.setUrl(request.getURI().toString());

        return page;
    }
}

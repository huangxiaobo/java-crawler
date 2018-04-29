package com.crawl.proxy.task;

import com.crawl.proxy.task.ProxyTestTask;
import com.crawl.zhihu.element.Page;
import com.crawl.utils.HttpClientUtil;
import com.crawl.proxy.Direct;
import com.crawl.proxy.Proxy;
import com.crawl.proxy.ProxyHttpClient;
import com.crawl.proxy.parser.ProxyListPageParser;
import com.crawl.proxy.ProxyListPageParserFactory;
import com.crawl.proxy.ProxyPool;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.crawl.proxy.ProxyPool.proxyQueue;
import static com.crawl.Constants.TIME_INTERVAL;

/**
 *
 */
public class ProxyParseTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(ProxyTestTask.class);

    public String url = "";
    private boolean proxyFlag = false;
    private Proxy currentProxy = null;

    private static ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();

    public ProxyParseTask(String url, boolean proxyFlag) {
        this.url = url;
        this.proxyFlag = proxyFlag;
    }

    public void run() {
        HttpGet request = null;
        Page page = null;
        try {
            if (proxyFlag) {
                request = new HttpGet(url);
                currentProxy = proxyQueue.take();
                if (!(currentProxy instanceof Direct)) {
                    HttpHost httpHost = new HttpHost(currentProxy.getIp(), currentProxy.getPort());
                    request.setConfig(
                        HttpClientUtil.getRequestConfigBuilder().setProxy(httpHost).build());
                }
                page = proxyHttpClient.getWebPage(request);
            } else {
                page = proxyHttpClient.getWebPage(url);
            }

            page.setProxy(currentProxy);

            if (page.getStatusCode() == HttpStatus.SC_OK) {
                parse(page);
            } else {
                Thread.sleep(100);
                retry();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (currentProxy != null) {
                currentProxy.setTimeInterval(TIME_INTERVAL);
                proxyQueue.add(currentProxy);
            }

            if (request != null) {
                request.releaseConnection();
            }
        }
    }

    private void parse(Page page) {
        if (page.getHtml() == null || page.getHtml() == "") {
            return;
        }

        ProxyListPageParser proxyListPageParser = ProxyListPageParserFactory.getProxyListPageParser(
            ProxyPool.proxyMap.get(page.getUrl())
        );
        if (proxyListPageParser == null) {
            return;
        }

        List<Proxy> proxyList = proxyListPageParser.parse(page.getHtml());
        for (Proxy proxy : proxyList) {
            ProxyPool.lock.readLock().lock();
            boolean contained = ProxyPool.proxySet.contains(proxy);
            ProxyPool.lock.readLock().unlock();
            if (!contained) {
                ProxyPool.lock.writeLock().lock();
                ProxyPool.proxySet.add(proxy);
                ProxyPool.lock.writeLock().unlock();

                ProxyHttpClient.getInstance().proxyTestExecutor.execute(new ProxyTestTask(proxy));
            }
        }
    }

    private void retry() {
        ProxyHttpClient.getInstance().proxyExecutor.execute(new ProxyParseTask(url, true));
    }
}


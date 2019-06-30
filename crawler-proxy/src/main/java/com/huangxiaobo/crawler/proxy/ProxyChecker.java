package com.huangxiaobo.crawler.proxy;

import com.huangxiaobo.crawler.common.Constants;
import com.huangxiaobo.crawler.common.Page;
import com.huangxiaobo.crawler.common.Proxy;
import com.huangxiaobo.crawler.common.HttpClientUtil;
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
public class ProxyChecker implements Runnable {

  private Logger logger = LoggerFactory.getLogger(ProxyChecker.class);
  private Proxy proxy;
  private ProxyPoolManager pool;

  public ProxyChecker(ProxyPoolManager pool, Proxy proxy) {
    this.pool = pool;
    this.proxy = proxy;
  }

  public void run() {

    long startTime = System.currentTimeMillis();
    HttpGet request = new HttpGet(Constants.INDEX_URL);
    try {
      RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.TIMEOUT).
          setConnectionRequestTimeout(Constants.TIMEOUT).
          setConnectTimeout(Constants.TIMEOUT).
          setProxy(new HttpHost(proxy.getIp(), proxy.getPort())).
          setCookieSpec(CookieSpecs.STANDARD).
          build();
      request.setConfig(requestConfig);
      Page page = new Page(HttpClientUtil.getWebPage(request));

      long endTime = System.currentTimeMillis();

      String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() +
          "  executing request " + page.getUrl() + " response statusCode:" + page.getStatusCode() +
          "  request cost time:" + (endTime - startTime) + "ms";

      if (page == null || page.getStatusCode() != 200) {
        logger.warn(logStr);
        return;
      }
      logger.info("available proxy: " + proxy.toString());

      request.releaseConnection();
      pool.addProxy(proxy);
    } catch (IOException e) {
      // e.printStackTrace();
    } finally {
      if (request != null) {
        request.releaseConnection();
      }
    }
  }
}

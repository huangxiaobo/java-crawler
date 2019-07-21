package com.huangxiaobo.crawler.proxy;

import com.huangxiaobo.crawler.common.Proxy;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

/**
 * 代理管理
 * 从代理网站上抓取代理
 */
@Service
public class ProxyPoolManager {

  public static final long TIME_INTERVAL = 1000;
  private static final String KEY = "Proxy";

  private final DelayQueue<Proxy> proxyQueue = new DelayQueue();
  private final Set<Proxy> proxySet = new HashSet<Proxy>();
  @Autowired
  private CrawlerProxyConfig config;
  private Logger logger = LoggerFactory.getLogger(ProxyPoolManager.class);
  private ThreadPoolExecutor proxyTestExecutor;

  @Autowired
  private HashOperations<String, String, Object> hashOperations;

  public ProxyPoolManager() {
    logger.info("proxy pool init");
  }

  public void start() {
    initThreadPool();
    initProxy();
  }

  public void initThreadPool() {
    //创建线程池
    proxyTestExecutor = new ThreadPoolExecutor(100, 100,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1000),
        new ThreadPoolExecutor.DiscardPolicy());
  }

  public void initProxy() {

    try {
      BufferedReader reader = new BufferedReader(new FileReader(config.proxyPath));
      String line = reader.readLine();

      while (line != null) {
        System.out.println(line);

        Proxy proxy;
        try {
          String[] attrs = line.split(",");
          proxy = new Proxy(attrs[0], Integer.valueOf(attrs[1]), TIME_INTERVAL);
        } catch (Exception e) {
          line = reader.readLine();
          continue;
        }

        proxy.setTimeInterval(TIME_INTERVAL);
        proxy.setFailureTimes(0);
        proxy.setSuccessfulTimes(0);

        boolean contained = proxySet.contains(proxy);
        if (!contained) {
          proxySet.add(proxy);
          proxyTestExecutor.execute(new ProxyChecker(this, proxy));
        }

        logger.info("add proxy" + proxy);

        line = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Proxy getProxy() throws InterruptedException {
    return proxyQueue.take();
  }

  public Proxy getProxyNowait() {
    return proxyQueue.poll();
  }

  public void addProxy(Proxy proxy) {
    logger.info("add proxy>>>>>>>>>>>>>>>>>>>.: " + proxy);
    proxyQueue.add(proxy);
    logger.info(String.format("add proxy key=%s, ip: %s proxy: %s", KEY, proxy.getIp(), proxy));
    hashOperations.put(KEY, proxy.getIp(), proxy);

  }

  public int getProxyCount() {
    return proxyQueue.size();
  }
}

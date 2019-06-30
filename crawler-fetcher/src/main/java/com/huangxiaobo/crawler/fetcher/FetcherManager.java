package com.huangxiaobo.crawler.fetcher;

import com.google.gson.Gson;
import com.huangxiaobo.crawler.common.Constants;
import com.huangxiaobo.crawler.common.FetcherTask;
import com.huangxiaobo.crawler.common.ParseTask;
import com.huangxiaobo.crawler.common.RabbitmqClient;
import com.huangxiaobo.crawler.common.bloomfilter.MemoryBloomFilter;
import com.huangxiaobo.crawler.proxy.ProxyPoolManager;
import java.lang.reflect.Constructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class FetcherManager {

  @Autowired
  public MemoryBloomFilter bloomFilter;
  private Logger logger = LoggerFactory.getLogger(FetcherManager.class);
  @Autowired
  private RabbitmqClient rabbitmqClient;

  @Autowired
  private ProxyPoolManager proxyPoolManager;

  @Autowired
  private FetcherManager fetcherManager;

  @Autowired
  @Qualifier("fetchTaskExecutor")
  private TaskExecutor fetchTaskExecutor;

  /*
  从mq中获取任务，然后抓取用户详情
   */
  public FetcherManager() {
//    bloomFilter = new MemoryBloomFilter();
  }

  public void start() {

  }

  @RabbitListener(queues = Constants.MQ_QUEUE_NAME)
  public void receive(String message) {
    System.out.println("FetcherManager [x] Received '" + message + "'" + this);

    FetcherTask fetcherTask;
    try {
      fetcherTask = new Gson().fromJson(message, FetcherTask.class);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    logger.info("start download url: " + fetcherTask.getUrl());

    try {
      Class<?> clazz = Class.forName(fetcherTask.fetcherClassName);

      Class[] classes = new Class[]{FetcherTask.class};
      Constructor constructor = clazz.getDeclaredConstructor(classes);
      constructor.setAccessible(true);

      Fetcher fetcher = (Fetcher) constructor.newInstance(fetcherTask);
      fetcher.setFetcherManager(fetcherManager);
      fetcher.setProxyPoolManager(proxyPoolManager);

      fetchTaskExecutor.execute(fetcher);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean addFetchTask(FetcherTask fetcherTask) {
    // 添加任务
    return addFetchTask(fetcherTask, false);
  }

  public boolean addFetchTask(FetcherTask fetcherTask, boolean force) {
    // 强制添加任务
    String url = fetcherTask.getUrl();
    if (force == false && true == bloomFilter.contains(url)) {
      logger.warn(url + " is exists.");
      return false;
    }

    logger.info("add url:" + url);
    bloomFilter.add(url);

    rabbitmqClient.sendFetchTask(new Gson().toJson(fetcherTask));

    return true;
  }

  public boolean addParseTask(ParseTask task) {
    logger.info("add parse task:" + task.getParserName());
    String s = new Gson().toJson(task);
    rabbitmqClient.sendParseTask(new Gson().toJson(task));
    return true;
  }
}

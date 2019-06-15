package com.hibo.crawler.scheduler;

import com.hibo.crawler.CrawlerConfig;
import com.hibo.crawler.fetcher.FetcherManager;
import com.hibo.crawler.fetcher.FetcherTask;
import com.hibo.crawler.fetcher.UserDetailFetcher;
import com.hibo.crawler.parser.ParserManager;
import com.hibo.crawler.parser.UserDetailParser;
import com.hibo.crawler.processor.ProcessorManager;
import com.hibo.crawler.proxy.ProxyPoolManager;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

  //创建线程池，池中保存的线程数为3，允许的最大线程数为5
  public ThreadPoolExecutor pool = null;
  public ThreadPoolExecutor persistencePool = null;
  @Autowired
  public ProcessorManager processorManager;
  @Autowired
  public FetcherManager[] fetcherManagers;
  @Autowired
  public ParserManager parserManager;
  @Autowired
  public ProxyPoolManager proxyPoolManager;
  @Autowired
  public CrawlerConfig crawlerConfig;
  private Logger logger = LoggerFactory.getLogger(Scheduler.class);

  public Scheduler() {
  }

  @Bean
  public Scheduler schedulerTemplate() {
    logger.info("crawler start.");
    initThreadPool();

    Scheduler sc = new Scheduler();

    return sc;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void doSomethingAfterStartup() {
    System.out.println("hello world, I have just started up");

    parserManager.start();
    for (FetcherManager fetcherManager : fetcherManagers) {
      fetcherManager.start();
    }
    processorManager.start();
    proxyPoolManager.start();

    FetcherManager fetcherManager = fetcherManagers[0];
    fetcherManager
        .addFetchTask(new FetcherTask(crawlerConfig.startURL, UserDetailFetcher.class.getName(),
            UserDetailParser.class.getName()));
    String[] urls = new String[]{
        "https://www.zhihu.com/people/gong-qing-tuan-zhong-yang-67",
        "https://www.zhihu.com/people/cloudycity"
    };
    for (String url : urls) {
      fetcherManager.addFetchTask(new FetcherTask(url, UserDetailFetcher.class.getName(),
          UserDetailParser.class.getName()));
    }
  }

  private void initThreadPool() {
    pool = new ThreadPoolExecutor(30, 50, 0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>()
    );

    persistencePool = new ThreadPoolExecutor(30, 50, 0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1000),
        new ThreadPoolExecutor.DiscardPolicy()
    );

    // 创建监视线程
    new Thread(new TaskMonitor(persistencePool)).start();
    new Thread(new TaskMonitor(pool)).start();
  }

  /**
   * Created by hxb on 2018/4/14.
   */
  class TaskMonitor implements Runnable {

    private Logger logger = LoggerFactory.getLogger(TaskMonitor.class);

    private ThreadPoolExecutor executor;

    public TaskMonitor(ThreadPoolExecutor executor) {
      this.executor = executor;
    }

    public void run() {
      while (executor != null && !executor.isShutdown() && !executor.isTerminated()) {
        logger.info(
            String.format(
                "[monitor] [%d/%d] active: %d completed: %s queueSize: %s task: %s isShutdown: %s isTerminated: %s",
                executor.getPoolSize(),
                executor.getMaximumPoolSize(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getQueue().size(),
                executor.getTaskCount(),
                executor.isShutdown(),
                executor.isTerminated()));
        try {
          Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

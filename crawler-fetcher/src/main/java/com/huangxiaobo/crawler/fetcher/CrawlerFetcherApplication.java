package com.huangxiaobo.crawler.fetcher;

import com.huangxiaobo.crawler.common.Constants;
import com.huangxiaobo.crawler.common.FetcherTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.*"})
public class CrawlerFetcherApplication {

  @Autowired
  public FetcherManager fetcherManager;

  @Autowired
  public CrawlerFetcherConfig crawlerConfig;

  public static void main(String[] args) {
    SpringApplication.run(CrawlerFetcherApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void doSomethingAfterStartup() {
    System.out.println("hello world, I have just started up");

    fetcherManager.start();

    fetcherManager.addFetchTask(
        new FetcherTask(
            crawlerConfig.startURL,
            UserDetailFetcher.class.getName(),
            Constants.USER_DETAIL_PARSER_CLASS_NAME
        )
    );
    String[] urls = new String[]{
        "https://www.zhihu.com/people/gong-qing-tuan-zhong-yang-67",
        "https://www.zhihu.com/people/cloudycity"
    };
    for (String url : urls) {
      fetcherManager.addFetchTask(
          new FetcherTask(
              url,
              UserDetailFetcher.class.getName(),
              Constants.USER_DETAIL_PARSER_CLASS_NAME
          )
      );
    }
  }
}

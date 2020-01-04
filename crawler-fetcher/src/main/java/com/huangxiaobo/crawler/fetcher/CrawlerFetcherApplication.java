package com.huangxiaobo.crawler.fetcher;

import com.huangxiaobo.crawler.common.Constants;
import com.huangxiaobo.crawler.common.FetcherTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = {"com.huangxiaobo.crawler.*"})
public class CrawlerFetcherApplication {

    @Autowired
    public FetcherManager fetcherManager;

    @Autowired
    public CrawlerFetcherConfig crawlerConfig;

    public static void main(String[] args) {
        SpringApplication.run(CrawlerFetcherApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doAfterStartup() {
        System.out.println("hello world, I have just started up");

        fetcherManager.start();
        for (String url : crawlerConfig.startURLs) {
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

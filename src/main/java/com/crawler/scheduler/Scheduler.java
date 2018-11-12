package com.crawler.scheduler;

import com.crawler.bloomfilter.BloomFilter;
import com.crawler.bloomfilter.MemoryBloomFilter;
import com.crawler.fetcher.FetcherManager;
import com.crawler.element.User;
import com.crawler.fetcher.FetcherTask;
import com.crawler.fetcher.UserDetailFetcher;
import com.crawler.monitor.UserDetailTaskMonitor;
import com.crawler.monitor.UserPersistenceTaskMonitor;
import com.crawler.parser.ParseTask;
import com.crawler.parser.ParserManager;
import com.crawler.pipeline.ProcessorManager;
import com.crawler.pipeline.UserPrintProcessor;
import com.crawler.pipeline.UserToDiskProcessor;
import com.crawler.proxy.ProxyHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class Scheduler implements Runnable {
    private Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private ArrayBlockingQueue<String> urls = new ArrayBlockingQueue<>(1000);

    //创建线程池，池中保存的线程数为3，允许的最大线程数为5
    public ThreadPoolExecutor pool = null;

    public ThreadPoolExecutor persistencePool = null;

    @Autowired
    public ProcessorManager<User> pipelineManager;

    public FetcherManager fetcherManager = null;

    public BloomFilter bloomFilter = null;


    public ParserManager parserManager = null;



    @Override
    public void run() {


        initThreadPool();
        setupProcessors();

        bloomFilter = new MemoryBloomFilter();
        // 代理
        ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
        proxyHttpClient.start();
        // 用户信息

        fetcherManager = new FetcherManager(this);
        fetcherManager.start();

        parserManager = new ParserManager();
        parserManager.start();

        String url = "https://www.zhihu.com/people/huang-liao-57";
        //urls.add(url);
        fetcherManager.addTask(new FetcherTask(url, false, UserDetailFetcher.class));

        while (true) {
//            logger.info("Scheduler running--------------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
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
        new Thread(new UserPersistenceTaskMonitor(persistencePool)).start();
        new Thread(new UserDetailTaskMonitor(pool)).start();
    }

    private void setupProcessors() {
        pipelineManager.setProcessors();
    }

    public String take() throws InterruptedException {
        return urls.take();
    }


    public void addParseTask(ParseTask parserTask) {
        this.parserManager.addParseTask(parserTask);
    }


    public void addFetcherTask(FetcherTask fetcherTask) {
        this.fetcherManager.addTask(fetcherTask);
    }

}

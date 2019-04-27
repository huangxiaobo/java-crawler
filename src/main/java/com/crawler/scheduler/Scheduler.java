package com.crawler.scheduler;

import com.crawler.bloomfilter.BloomFilter;
import com.crawler.bloomfilter.MemoryBloomFilter;
import com.crawler.fetcher.FetcherManager;
import com.crawler.fetcher.FetcherTask;
import com.crawler.fetcher.UserDetailFetcher;
import com.crawler.monitor.UserDetailTaskMonitor;
import com.crawler.monitor.UserPersistenceTaskMonitor;
import com.crawler.parser.ParseTask;
import com.crawler.parser.ParserManager;
import com.crawler.processor.ProcessorManager;
import com.crawler.proxy.ProxyHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private ArrayBlockingQueue<String> urls = new ArrayBlockingQueue<>(1000);

    //创建线程池，池中保存的线程数为3，允许的最大线程数为5
    public ThreadPoolExecutor pool = null;

    public ThreadPoolExecutor persistencePool = null;

    public ProcessorManager processorManager;

    public FetcherManager fetcherManager = null;

    public BloomFilter bloomFilter = null;

    public ParserManager parserManager = null;


    public Scheduler() {
    }


    @Override
    public void run() {
        logger.info("crawler start.");
        initThreadPool();

        bloomFilter = new MemoryBloomFilter();
        // 代理
        ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
        proxyHttpClient.start();
        // 用户信息

        try {
            fetcherManager = new FetcherManager(this);
            fetcherManager.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        parserManager = new ParserManager();
        parserManager.start();

        processorManager = new ProcessorManager();
        processorManager.setupProcessors();

        String url = "https://www.zhihu.com/people/huang-liao-57";
        fetcherManager.addTask(new FetcherTask(url, true, UserDetailFetcher.class.getName()));

        while (true) {
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

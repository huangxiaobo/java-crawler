package com.crawler.fetcher;

import com.crawler.bloomfilter.BloomFilter;
import com.crawler.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.security.cert.TrustAnchor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FetcherManager {
    private Logger logger = LoggerFactory.getLogger(FetcherManager.class);

    public Scheduler scheduler;
    public ThreadPoolExecutor pool = null;
    private BloomFilter bloomFilter = null;
    private ArrayBlockingQueue<FetcherTask> urls;

    public FetcherManager(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.bloomFilter = this.scheduler.bloomFilter;
        this.urls = new ArrayBlockingQueue<FetcherTask>(1 << 10);

        pool = new ThreadPoolExecutor(30, 50, 0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>()
        );
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    FetcherTask fetcherTask;
                    try {
                        fetcherTask = urls.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
                    logger.info("start download url: " + fetcherTask.getUrl());

                    try {
                        Class fetchClass = fetcherTask.fetcherClass;

                        Class[] classes = new Class[]{FetcherManager.class, String.class, boolean.class};
                        Constructor c0 = fetchClass.getDeclaredConstructor(classes);
                        c0.setAccessible(true);

                        Fetcher cls = (Fetcher) c0.newInstance(FetcherManager.this, fetcherTask.getUrl(), fetcherTask.getUserProxy());

                        pool.execute(cls);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }


    public void sendPipeline(Object object) {
        //scheduler.pipelineManager.process(object);
    }

    public boolean addTask(FetcherTask fetcherTask) {
        String url = fetcherTask.getUrl();
        if (true == bloomFilter.contains(url)) {
            logger.warn(url + "is exists.");
            return false;
        }

        boolean ret = urls.add(fetcherTask);
        System.out.println(ret);
        //urls.put(fetcherTask);
        bloomFilter.add(url);
        return true;
    }

    public FetcherTask take() throws InterruptedException {
        return urls.take();
    }
}

package com.huangxiaobo.crawler.fetcher;

import com.google.gson.Gson;
import com.huangxiaobo.crawler.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class FetcherManager {

    private final ArrayBlockingQueue<String> proxyQueue = new ArrayBlockingQueue(1000);
    @Autowired
    public MemoryBloomFilter bloomFilter;
    private Logger logger = LoggerFactory.getLogger(FetcherManager.class);
    @Autowired
    private RabbitmqClient rabbitmqClient;
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

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    if (proxyQueue.size() > 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    try {
                        RestTemplate restTemplate = new RestTemplate();

                        final String baseUrl = "http://127.0.0.1:5010/get/";
                        URI uri = new URI(baseUrl);

                        HttpHeaders headers = new HttpHeaders();
                        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
                        ResponseEntity<String> entity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

                        logger.info("" + entity.getStatusCodeValue());
                        logger.info("" + entity.getBody());
                        String proxy = entity.getBody();
                        proxyQueue.add(proxy);

                        logger.info("proxy queue size: " + proxyQueue.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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

        logger.info("start download url: " + fetcherTask);

        try {
            String fetcherClassName = fetcherTask.fetcherClassName;
            if (!fetcherClassName.startsWith("com.huangxiaobo.crawler.fetcher")) {
                fetcherClassName = "com.huangxiaobo.crawler.fetcher." + fetcherClassName;
            }

            Class<?> clazz = Class.forName(fetcherClassName);

            Class[] classes = new Class[]{FetcherTask.class};
            Constructor constructor = clazz.getDeclaredConstructor(classes);
            constructor.setAccessible(true);

            Fetcher fetcher = (Fetcher) constructor.newInstance(fetcherTask);
            fetcher.setFetcherManager(fetcherManager);

            fetchTaskExecutor.execute(fetcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProxy() {
        try {
            return proxyQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addProxy(Proxy proxy) {
        return;
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
        rabbitmqClient.sendParseTask(new Gson().toJson(task));
        return true;
    }
}

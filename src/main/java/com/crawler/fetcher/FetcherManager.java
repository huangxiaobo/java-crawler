package com.crawler.fetcher;

import com.crawler.bloomfilter.BloomFilter;
import com.crawler.scheduler.Scheduler;
import com.crawler.utils.RabbitmqUtil;
import com.google.gson.Gson;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FetcherManager {

    private Logger logger = LoggerFactory.getLogger(FetcherManager.class);

    public Scheduler scheduler;
    public ThreadPoolExecutor pool;
    private BloomFilter bloomFilter;
    private Channel rabbitmqChannel;

    private static final String MQ_QUEUE_NAME = "zhihu.user.url.queue";
    private static final String MQ_EXCHANGE_NAME = "zhihu.user.url.exchange";
    private static final String MQ_ROUTING_KEY = "zhihu.user.url.bindingkey";

    /*
    一个队列，两个group
    1. 一个负责从队列中获取userId 列表，然后抓取用户详情

    2. 一个负责根据userId, 负责抓取用户关注着
     */
    public FetcherManager(Scheduler scheduler) throws IOException {
        this.scheduler = scheduler;
        this.bloomFilter = this.scheduler.bloomFilter;

        pool = new ThreadPoolExecutor(30, 50, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());

        Connection rabbitmqConnection = RabbitmqUtil.getConnection();
        rabbitmqChannel = rabbitmqConnection.createChannel();

        /**
         * 声明一个队列
         * queueDeclare String queue, 队列名字
         * boolean durable, 持久化
         * boolean exclusive,
         * boolean autoDelete,
         * Map<String, Object> arguments
         */
        rabbitmqChannel.queueDeclare(MQ_QUEUE_NAME, false, false, false, null);

        /**
         * 声明一个交换器
         *
         */
        rabbitmqChannel.exchangeDeclare(MQ_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        /**
         * 绑定队列和交换器
         *
         */
        rabbitmqChannel.queueBind(MQ_QUEUE_NAME, MQ_EXCHANGE_NAME, MQ_ROUTING_KEY);
    }

    public void start() {
        Connection rabbitmqConnection = RabbitmqUtil.getConnection();
        final Channel channel;
        try {
            channel = rabbitmqConnection.createChannel();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        new Thread(
            () -> {
                while (true) {

                    DeliverCallback callback =
                        (consumerTag, delivery) -> {
                            String message = new String(delivery.getBody(), "UTF-8");
                            System.out.println(" [x] Received '" + message + "'");

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

                                Class[] classes = new Class[]{FetcherManager.class, String.class, boolean.class};
                                Constructor constructor = clazz.getDeclaredConstructor(classes);
                                constructor.setAccessible(true);

                                Fetcher cls = (Fetcher) constructor
                                    .newInstance(FetcherManager.this, fetcherTask.getUrl(), fetcherTask.getUserProxy());

                                pool.execute(cls);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        };

                    try {
                        channel.basicConsume(MQ_QUEUE_NAME, true, callback, consumerTag -> {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ei) {
                            ei.printStackTrace();
                        }
                    }
                }
            })
            .start();
    }


    public boolean addTask(FetcherTask fetcherTask) {
        String url = fetcherTask.getUrl();
        if (true == bloomFilter.contains(url)) {
            logger.warn(url + "is exists.");
            return false;
        }

        // urls.put(fetcherTask);
        logger.info("add url:" + url);
        bloomFilter.add(url);
        try {
            String s = new Gson().toJson(fetcherTask);
            rabbitmqChannel.basicPublish(MQ_EXCHANGE_NAME, MQ_ROUTING_KEY, null, s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    //
    // public FetcherTask take() throws InterruptedException {
    //    return urls.take();
    // }
}

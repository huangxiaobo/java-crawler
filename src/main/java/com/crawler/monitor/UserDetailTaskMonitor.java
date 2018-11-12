package com.crawler.monitor;

import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/14.
 */
public class UserDetailTaskMonitor implements Runnable{
    private Logger logger = LoggerFactory.getLogger(UserDetailTaskMonitor.class);

    private ThreadPoolExecutor executor;

    public UserDetailTaskMonitor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }


    public void run() {
        while (executor != null && !executor.isShutdown() && !executor.isTerminated()) {
            logger.info(String.format(
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

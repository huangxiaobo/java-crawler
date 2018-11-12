package com.crawler.monitor;

import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监视用户信息持久化线程池的状态
 */
public class UserPersistenceTaskMonitor implements Runnable {

    private Logger logger = LoggerFactory.getLogger(UserPersistenceTaskMonitor.class);
    private ThreadPoolExecutor executor;


    public UserPersistenceTaskMonitor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void run() {
        while (executor != null) {
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

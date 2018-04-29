package com.crawl.zhihu;

import com.crawl.zhihu.element.User;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 已经抓取的用户信息队列
 */
public class ZhihuUserQueue {

    private Logger logger = LoggerFactory.getLogger(ZhihuUserQueue.class);

    private static ZhihuUserQueue instance;
    private LinkedBlockingQueue<User> userLinkedBlockingQueue = new LinkedBlockingQueue<User>();

    public static ZhihuUserQueue getInstance() {
        if (instance == null) {
            instance = new ZhihuUserQueue();
        }
        return instance;
    }

    public boolean add(User user) {
        return userLinkedBlockingQueue.add(user);
    }

    public User take() throws InterruptedException {
        return userLinkedBlockingQueue.take();
    }
}

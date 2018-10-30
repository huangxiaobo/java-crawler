package com.crawler.zhihu;

import com.crawler.Crawler;
import com.crawler.zhihu.bloomfilter.BloomFilter;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户的urlToken队列
 * 爬取用户关注的人的列表后，解析出其urlToken, 然后放入此队列
 */
public class ZhihuUserUrlTokenQueue {

    private Logger logger = LoggerFactory.getLogger(ZhihuUserUrlTokenQueue.class);
    private static ZhihuUserUrlTokenQueue instance = null;

    private ArrayBlockingQueue<String> urlTokenList = new ArrayBlockingQueue<String>(1 << 10);
    private BloomFilter bloomFilter = Crawler.getInstance().bloomFilter;

    public static ZhihuUserUrlTokenQueue getInstance() {
        if (instance == null) {
            instance = new ZhihuUserUrlTokenQueue();
        }
        return instance;
    }

    public boolean add(String urlToken) {
        if (true == bloomFilter.contains(urlToken)) {
            logger.warn(urlToken + "is exists.");
            return false;
        }

        urlTokenList.add(urlToken);
        bloomFilter.add(urlToken);
        return true;
    }

    public String take() throws InterruptedException {
        return urlTokenList.take();
    }
}

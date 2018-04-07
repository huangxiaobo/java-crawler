package com.crawl.zhihu;

import com.crawl.element.Topic;
import com.crawl.element.Page;
import com.crawl.element.TopicCategory;
import com.crawl.HttpClientUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/1.
 */
public class ZhihuTopicParser extends Thread{
    private Logger logger = LoggerFactory.getLogger(ZhihuTopicParser.class);
    private static ZhihuTopicParser instance = null;

    private final LinkedBlockingQueue<TopicCategory> topicCategoryList = new LinkedBlockingQueue<TopicCategory>();

    public static ZhihuTopicParser getInstance() {
        if (instance == null) {
            instance = new ZhihuTopicParser();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TopicCategory topicCategory = topicCategoryList.take();

                logger.info("TopicCategory: " + topicCategory.toString());
                Page page = getTopicPage(topicCategory);

                parsePage(page);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void addTopicCategory(TopicCategory topicCategory) {
        logger.info(" " + topicCategory);
        topicCategoryList.add(topicCategory);
    }

    public Page getTopicPage(TopicCategory topicCategory) {
        // https://www.zhihu.com/topics#%E7%A7%91%E6%8A%80
        Page page = new Page();
        try {
            String topicCategoryName = topicCategory.getTopicCategoryName();
            String url = "https://www.zhihu.com/topics#" + topicCategoryName;
            page.setHtml((HttpClientUtil.getWebPage(url)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return page;
    }

    public List<Topic> parsePage(Page page) {
        List<Topic> topicList = new ArrayList<Topic>();

        Document doc = Jsoup.parse(page.getHtml());
        System.out.println(page.getHtml());



        return topicList;
    }

}

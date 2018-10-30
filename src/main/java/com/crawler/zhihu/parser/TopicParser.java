package com.crawler.zhihu.parser;

import com.crawler.utils.HttpClientUtil;
import com.crawler.zhihu.element.Page;
import com.crawler.zhihu.element.Topic;
import com.crawler.zhihu.element.TopicCategory;
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
public class TopicParser extends Thread {

    private Logger logger = LoggerFactory.getLogger(TopicParser.class);
    private static TopicParser instance = null;

    private final LinkedBlockingQueue<TopicCategory> topicCategoryList = new LinkedBlockingQueue<TopicCategory>();

    public static TopicParser getInstance() {
        if (instance == null) {
            instance = new TopicParser();
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

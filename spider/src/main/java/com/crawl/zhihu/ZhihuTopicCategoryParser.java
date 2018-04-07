package com.crawl.zhihu;

import com.crawl.element.Page;
import com.crawl.element.TopicCategory;
import com.crawl.utils.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZhihuTopicCategoryParser extends Thread{

    private Logger logger = LoggerFactory.getLogger(ZhihuTopicCategoryParser.class);

    private static ZhihuTopicCategoryParser zhihuTopicCategoryParser = null;

    public static ZhihuTopicCategoryParser getInstance() {
        if (zhihuTopicCategoryParser == null) {
            synchronized (ZhihuTopicCategoryParser.class) {
                if (zhihuTopicCategoryParser == null) {
                    zhihuTopicCategoryParser = new ZhihuTopicCategoryParser();
                }
            }
        }
        return zhihuTopicCategoryParser;
    }

    @Override
    public void run() {
        Page page = getPage();

        List<TopicCategory> topicCategoryList = parsePage(page);

        logger.info("====================================");


        for (TopicCategory topicCategory : topicCategoryList) {
            ZhihuTopicParser.getInstance().addTopicCategory(topicCategory);
        }
    }


    public Page getPage() {
        StringBuffer sb = new StringBuffer();
        try {
            FileUtils.readToBuffer(sb,
                "D:\\workspace\\java\\spider\\src\\main\\resources\\topic.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Page page = new Page(sb.toString());
        return page;
    }

    public List<TopicCategory> parsePage(Page page) {
        List<TopicCategory> topicCategoryList = new ArrayList<TopicCategory>();
        Document doc = Jsoup.parse(page.getHtml());
        System.out.println(page.getHtml());

        Elements ul = doc.select("div.zm-topic-cat-page > ul");
        Elements li = ul.select("li");

        for (Element element : li) {
            Integer topicCategoryId = Integer.parseInt(element.attr("data-id"));
            String topicCategoryName = element.text();
            logger.info(">" + topicCategoryId + " : " + topicCategoryName);
            topicCategoryList.add(new TopicCategory(topicCategoryId, topicCategoryName));
        }

        return topicCategoryList;
    }
}

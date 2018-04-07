package com.crawl.zhihu;

import com.crawl.element.User;
import com.crawl.element.Page;
import com.crawl.Spider;
import com.crawl.parser.Parser;
import com.jayway.jsonpath.PathNotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jayway.jsonpath.JsonPath;

/**
 * Created by hxb on 2018/4/1.
 */
public class ZhihuUserDetailParser implements Parser {

    private Logger logger = LoggerFactory.getLogger(ZhihuUserDetailParser.class);
    private static ZhihuUserDetailParser instance;

    private static Spider spider = Spider.getInstance();

    public static ZhihuUserDetailParser getInstance() {
        if (instance == null) {
            instance = new ZhihuUserDetailParser();
        }
        return instance;
    }


    public void addUserId(String userId) {
        logger.info("add user id: " + userId);
        spider.zhihuUserUrlTokenQueue.add(userId);
    }


    public Object parse(String userId, Page page) {
        Document doc = Jsoup.parse(page.getHtml());

        String dataStateJson = doc.select("[data-state]").first().attr("data-state");
        logger.info(dataStateJson);

        User user = new User();
        user.urlToken = userId;

        String commonJsonPath = String.format("$.entities.users.['%s']", userId);
        JsonPath.parse(dataStateJson).read(commonJsonPath);

        user.name = JsonPath.parse(dataStateJson).read(commonJsonPath + ".name");
        user.urlToken = JsonPath.parse(dataStateJson).read(commonJsonPath + ".urlToken");
        user.headline = JsonPath.parse(dataStateJson).read(commonJsonPath + ".headline");
        user.id = JsonPath.parse(dataStateJson).read(commonJsonPath + ".id");
        user.avatarUrl = JsonPath.parse(dataStateJson).read(commonJsonPath + ".avatarUrl");
        user.gender = JsonPath.parse(dataStateJson).read(commonJsonPath + ".gender");
        user.userType = JsonPath.parse(dataStateJson).read(commonJsonPath + ".userType");
        user.url = JsonPath.parse(dataStateJson).read(commonJsonPath + ".url");
        user.followerCount = JsonPath.parse(dataStateJson).read(commonJsonPath + ".followerCount");
        user.followingCount = JsonPath.parse(dataStateJson).read(commonJsonPath + ".followingCount");
        user.voteupCount = JsonPath.parse(dataStateJson).read(commonJsonPath + ".voteupCount");
        user.thankedCount = JsonPath.parse(dataStateJson).read(commonJsonPath + ".thankedCount");
        user.answerCount = JsonPath.parse(dataStateJson).read(commonJsonPath + ".answerCount");
        user.articlesCount = JsonPath.parse(dataStateJson).read(commonJsonPath + ".articlesCount");

        logger.info(user.toString());

        return user;
    }
}

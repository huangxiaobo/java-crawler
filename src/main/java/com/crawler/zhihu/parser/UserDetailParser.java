package com.crawler.zhihu.parser;

import com.crawler.zhihu.element.Page;
import com.crawler.zhihu.element.User;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/1.
 */
public class UserDetailParser implements Parser {

    private Logger logger = LoggerFactory.getLogger(UserDetailParser.class);
    private static UserDetailParser instance;

    public static UserDetailParser getInstance() {
        if (instance == null) {
            instance = new UserDetailParser();
        }
        return instance;
    }


    public Object parse(String urlToken, Page page) {
        Document doc = Jsoup.parse(page.getHtml());
        JSONObject dataStateJson;
        JSONObject userJson;
        User user = null;
        try {
            dataStateJson = new JSONObject(doc.select("[data-state]").first().attr("data-state"));

            userJson = dataStateJson.getJSONObject("entities").getJSONObject("users").getJSONObject(urlToken);
        } catch (Exception e) {
            logger.warn(String.format("user %s may be not exists.", urlToken));
            return null;
        }

        try {
            user = new Gson().fromJson(userJson.toString(), User.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("parse page of %s failed.", urlToken));
            if (null != dataStateJson) {
                logger.info(dataStateJson.toString());
            }
        }
        return user;
    }
}

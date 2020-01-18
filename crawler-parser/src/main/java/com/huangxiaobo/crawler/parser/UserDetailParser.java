package com.huangxiaobo.crawler.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huangxiaobo.crawler.common.Constants;
import com.huangxiaobo.crawler.common.FetcherTask;
import com.huangxiaobo.crawler.common.ParseTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by hxb on 2018/4/1.
 */
public class UserDetailParser extends Parser {

    private Logger logger = LoggerFactory.getLogger(UserDetailParser.class);

    public UserDetailParser(ParseTask task) {
        super(task);
    }

    public void parse(ParseTask task) {
        Document doc = Jsoup.parse(task.getContent());

        try {
            JsonParser parser = new JsonParser();
            String jsonData = doc.select("#js-initialData").first().childNode(0).toString();
            JsonObject dataObject = parser.parse(jsonData).getAsJsonObject();

            JsonObject usersJson = dataObject.get("initialState").getAsJsonObject().get("entities")
                    .getAsJsonObject().get("users").getAsJsonObject();

            //will return members of your object
            Set<Map.Entry<String, JsonElement>> entries = usersJson.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {

                String urlToken = entry.getKey();
                System.out.println(urlToken);

                String useJson = entry.getValue().toString();
                parserManager.addProcessTask(useJson);

                // 抓取关注他的人的所有用户详情
                String url = "https://www.zhihu.com/people/" + urlToken;
                this.parserManager.addFetchTask(
                        new FetcherTask(url, "UserDetailFetcher",
                                UserDetailParser.class.getName()));

                int followingCount = entry.getValue().getAsJsonObject().get("followingCount").getAsInt();
                // 抓取用户的关注者信息
                for (int j = 0; j < followingCount / 20 + 1; j++) {
                    String followeesUrl = String.format(Constants.USER_FOLLOWEES_URL, urlToken, j * 20);

                    this.parserManager.addFetchTask(
                            new FetcherTask(
                                    followeesUrl,
                                    "UserFollowingFetcher",
                                    UserFollowingParser.class.getName()));
                }
            }

        } catch (Exception e) {
            logger.warn("page may be not exists.");
        }
    }
}

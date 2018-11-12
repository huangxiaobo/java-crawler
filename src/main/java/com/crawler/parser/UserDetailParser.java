package com.crawler.parser;

import com.crawler.element.Page;
import com.crawler.element.User;
import com.google.gson.Gson;
//import org.json.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import com.google.gson.JsonObject;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    public Object parse(Page page) {
        Document doc = Jsoup.parse(page.getHtml());

        List<User> users = new ArrayList<User>();
        try {
            JsonParser parser = new JsonParser();
            String jsonData = doc.select("#js-initialData").first().childNode(0).toString();
            JsonObject dataObject = parser.parse(jsonData).getAsJsonObject();

            JsonObject  usersJson = dataObject.get("initialState").getAsJsonObject().get("entities").getAsJsonObject().get("users").getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = usersJson.entrySet();//will return members of your object
            for (Map.Entry<String, JsonElement> entry: entries) {
                System.out.println(entry.getKey());
                User user = new Gson().fromJson(entry.getValue().toString(), User.class);
                users.add(user);
            }

        } catch (Exception e) {
            logger.warn(String.format("page of %s may be not exists.", page.getUrl()));
            return null;
        }
        return users;
    }
}

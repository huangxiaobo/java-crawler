package com.hibo.crawler.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hibo.crawler.element.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by hxb on 2018/4/1.
 */
public class UserDetailParser extends Parser {

    private Logger logger = LoggerFactory.getLogger(UserDetailParser.class);

  public Object parse(ParseTask task) {
    Document doc = Jsoup.parse(task.getContent());

        List<User> users = new ArrayList<User>();
        try {
            JsonParser parser = new JsonParser();
            String jsonData = doc.select("#js-initialData").first().childNode(0).toString();
            JsonObject dataObject = parser.parse(jsonData).getAsJsonObject();

            JsonObject usersJson = dataObject.get("initialState").getAsJsonObject().get("entities").getAsJsonObject().get("users").getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = usersJson.entrySet();//will return members of your object
            for (Map.Entry<String, JsonElement> entry : entries) {
                System.out.println(entry.getKey());
                User user = new Gson().fromJson(entry.getValue().toString(), User.class);
                users.add(user);
            }

        } catch (Exception e) {
          logger.warn("page may be not exists.");
            return null;
        }

    getParserManager().addProcessTasks(users);
        return users;
    }
}

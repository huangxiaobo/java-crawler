package com.hibo.crawler.parser;

import com.hibo.crawler.fetcher.FetcherTask;
import com.hibo.crawler.fetcher.UserDetailFetcher;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserFollowingParser extends Parser {
    private Logger logger = LoggerFactory.getLogger(UserDetailParser.class);

    public Object parse(ParseTask task) {
        // json 格式


        List<String> urlTokenList = JsonPath.parse(task.getContent()).read("$.data..url_token");
        logger.info("url token list: " + urlTokenList.toString());
        for (String s : urlTokenList) {
            if (s == null) {
                continue;
            }
            String url = "https://www.zhihu.com/people/" + s;
            getParserManager().addFetchTask(new FetcherTask(url, UserDetailFetcher.class.getName(), UserDetailParser.class.getName()));
        }

        return null;
    }
}

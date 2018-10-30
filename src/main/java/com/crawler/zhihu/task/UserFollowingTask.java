package com.crawler.zhihu.task;

import com.crawler.zhihu.element.Page;
import com.crawler.Crawler;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/5.
 */
public class UserFollowingTask extends Task {

    private Logger logger = LoggerFactory.getLogger(UserFollowingTask.class);

    public UserFollowingTask(Crawler crawler, HttpRequestBase request) {
        super(crawler, request, true);
    }


    public void parse(Page page) {
        // json 格式

        List<String> urlTokenList = JsonPath.parse(page.getHtml()).read("$.data..url_token");
        // logger.info("url token list: " + urlTokenList.toString());
        for (String s : urlTokenList) {
            if (s == null) {
                continue;
            }
            crawler.pool.execute(new UserDetailTask(this.crawler, s));
        }
    }


    public void retry() {
        Crawler.getInstance().pool.execute(new UserFollowingTask(this.crawler, request));
    }
}

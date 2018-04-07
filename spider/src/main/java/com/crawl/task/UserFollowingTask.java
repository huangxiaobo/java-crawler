package com.crawl.task;

import com.crawl.element.Page;
import com.crawl.HttpClientUtil;
import com.crawl.Spider;
import com.crawl.proxy.Direct;
import com.crawl.proxy.Proxy;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/5.
 */
public class UserFollowingTask extends Task {

    private Logger logger = LoggerFactory.getLogger(UserFollowingTask.class);

    public UserFollowingTask(HttpRequestBase request) {
        super(request, true);
    }


    public void parse(Page page) {
        // json 格式

        List<String> urlTokenList = JsonPath.parse(page.getHtml()).read("$.data..url_token");
        logger.info("url token list: " + urlTokenList.toString());
        for (String s : urlTokenList) {
            if (s == null) {
                continue;
            }
            spider.pool.execute(new UserDetailTask(s));
        }
    }


    public void retry() {
        Spider.getInstance().pool.execute(new UserFollowingTask(request));
    }
}

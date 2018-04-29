package com.crawl.zhihu.task;

import com.crawl.Constants;
import com.crawl.Spider;
import com.crawl.zhihu.element.Page;
import com.crawl.zhihu.element.User;
import com.crawl.zhihu.parser.UserDetailParser;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/6.
 */
public class UserDetailTask extends Task {

    private Logger logger = LoggerFactory.getLogger(UserDetailTask.class);

    private String urlToken;

    public UserDetailTask(String urlToken) {
        super("https://www.zhihu.com/people/" + urlToken, true);
        this.urlToken = urlToken;
    }

    public void addUserId(String urlToken) {
        logger.info("add user urlToken: " + urlToken);
//        spider.zhihuUserUrlTokenQueue.add(urlToken);
        spider.bloomFilter.add(urlToken);
    }


    public void parse(Page page) {
        if (page == null || page.getHtml() == null || page.getHtml().equals("")) {
            return;
        }

        User user = (User) UserDetailParser.getInstance().parse(urlToken, page);
        if (null == user) {
            return;
        }

        spider.userPipelineManager.process(user);

        addUserId(user.urlToken);
        logger.info("userToken: " + urlToken + " detail: " + user.toString());

        for (int j = 0; j < user.getFollowingCount() / 20 + 1; j++) {
            String nextUrl = String.format(Constants.USER_FOLLOWEES_URL, urlToken, j * 20);

            HttpGet request = new HttpGet(nextUrl);
            request.setHeader("authorization", "oauth " + spider.getAuthorization());
            spider.pool.execute(new UserFollowingTask(request));
        }
    }

    public void retry() {
        Spider.getInstance().pool.execute(new UserDetailTask(urlToken));
    }
}

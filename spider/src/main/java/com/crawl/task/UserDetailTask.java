package com.crawl.task;

import com.crawl.Constants;
import com.crawl.Spider;
import com.crawl.element.Page;
import com.crawl.element.User;
import com.crawl.zhihu.ZhihuUserDetailParser;
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


    public void parse(Page page) {
        if (page.getHtml() == null || page.getHtml() == "") {
            return;
        }
        User user = (User) ZhihuUserDetailParser.getInstance().parse(urlToken, page);

//        spider.persistencePool.execute(new UserPersistenceTask(user));
        spider.userPipelineManager.process(user);

        logger.info("userToken: " + urlToken + " followees: " + user.getFollowees() + " detail: " + user.toString());

        for (int j = 0; j < user.getFollowees() / 20 + 1; j++) {
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

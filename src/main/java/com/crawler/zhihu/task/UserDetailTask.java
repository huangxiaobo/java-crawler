package com.crawler.zhihu.task;

import com.crawler.Constants;
import com.crawler.Crawler;
import com.crawler.zhihu.element.Page;
import com.crawler.zhihu.element.User;
import com.crawler.zhihu.parser.UserDetailParser;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/6.
 */
public class UserDetailTask extends Task {

    private Logger logger = LoggerFactory.getLogger(UserDetailTask.class);

    private String urlToken;

    public UserDetailTask(Crawler crawler, String urlToken) {
        super(crawler,"https://www.zhihu.com/people/" + urlToken, true);
        this.urlToken = urlToken;
    }

    public void addUserId(String urlToken) {
        logger.info("add user urlToken: " + urlToken);
//        this.crawler.zhihuUserUrlTokenQueue.add(urlToken);
        this.crawler.bloomFilter.add(urlToken);
    }


    public void parse(Page page) {
        if (page == null || page.getHtml() == null || page.getHtml().equals("")) {
            return;
        }

        User user = (User) UserDetailParser.getInstance().parse(urlToken, page);
        if (null == user) {
            return;
        }

        logger.info("userPipeManage----------> " + this.crawler.userPipelineManager);
        this.crawler.userPipelineManager.process(user);

        addUserId(user.urlToken);
        logger.info("userToken: " + urlToken + " detail: " + user.toString());

        for (int j = 0; j < user.getFollowingCount() / 20 + 1; j++) {
            String nextUrl = String.format(Constants.USER_FOLLOWEES_URL, urlToken, j * 20);

            HttpGet request = new HttpGet(nextUrl);
//            request.setHeader("authorization", "oauth " + this.crawler.getAuthorization());
            this.crawler.pool.execute(new UserFollowingTask(this.crawler, request));
        }
    }

    public void retry() {
        Crawler.getInstance().pool.execute(new UserDetailTask(this.crawler, this.urlToken));
    }
}

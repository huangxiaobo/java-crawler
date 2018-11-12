package com.crawler.fetcher;

import com.crawler.Constants;
import com.crawler.element.Page;
import com.crawler.element.User;
import com.crawler.parser.UserDetailParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by hxb on 2018/4/6.
 */
public class UserDetailFetcher extends Fetcher {

    private Logger logger = LoggerFactory.getLogger(UserDetailFetcher.class);

    private String url;

    public UserDetailFetcher(FetcherManager fetcherManager, String url, boolean proxyFlag) {
        super(fetcherManager,  url, true);
        this.url = url;
    }


    public void parse(Page page) {
        if (page == null || page.getHtml() == null || page.getHtml().equals("")) {
            return;
        }

        List<User> users = (List<User>) UserDetailParser.getInstance().parse(page);
        if (null == users || users.size() == 0) {
            return;
        }

        for (User user: users) {
            //String urlToken = user.urlToken;

            this.fetcherManager.scheduler.pipelineManager.process(user);

            logger.info("userToken: " + user.urlToken + " detail: " + user.toString());

            for (int j = 0; j < user.getFollowingCount() / 20 + 1; j++) {
                String url = String.format(Constants.USER_FOLLOWEES_URL, user.urlToken, j * 20);

                //HttpGet request = new HttpGet(nextUrl);
                ////request.setHeader("authorization", "oauth " + this.scheduler.getAuthorization());
                //this.fetcherManager.pool.execute(new UserFollowingTask(this.fetcherManager, request));
                this.fetcherManager.addTask(new FetcherTask(url, true, UserFollowingFetcher.class));
            }
        }
    }

    public void retry() {
        fetcherManager.pool.execute(new UserDetailFetcher(this.fetcherManager, this.url, true));
    }
}

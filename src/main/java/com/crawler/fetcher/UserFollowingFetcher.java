package com.crawler.fetcher;

import com.crawler.element.Page;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/5.
 */
public class UserFollowingFetcher extends Fetcher {

    private Logger logger = LoggerFactory.getLogger(UserFollowingFetcher.class);

    public UserFollowingFetcher(FetcherManager fetcherManager, String url, boolean useProxy) {
        super(fetcherManager, url, true);
    }


    public void parse(Page page) {
        // json 格式

        List<String> urlTokenList = JsonPath.parse(page.getHtml()).read("$.data..url_token");
        logger.info("url token list: " + urlTokenList.toString());
        for (String s : urlTokenList) {
            if (s == null) {
                continue;
            }
            String url = "https://www.zhihu.com/people/" + s;
            this.fetcherManager.addTask(new FetcherTask(url, true, UserDetailFetcher.class.getName()));
        }
    }


    public void retry() {
        fetcherManager.pool.execute(new UserFollowingFetcher(this.fetcherManager, url, true));
    }
}

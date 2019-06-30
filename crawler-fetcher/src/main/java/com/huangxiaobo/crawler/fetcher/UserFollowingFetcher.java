package com.huangxiaobo.crawler.fetcher;

import com.huangxiaobo.crawler.common.FetcherTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/5.
 */
public class UserFollowingFetcher extends Fetcher {

  private Logger logger = LoggerFactory.getLogger(UserFollowingFetcher.class);

  public UserFollowingFetcher(FetcherTask task) {
    super(task);
  }

//
//    public void parse(Page page) {
//        // json 格式
//
//
//        List<String> urlTokenList = JsonPath.parse(page.getHtml()).read("$.data..url_token");
//        logger.info("url token list: " + urlTokenList.toString());
//        for (String s : urlTokenList) {
//            if (s == null) {
//                continue;
//            }
//            String url = "https://www.zhihu.com/people/" + s;
//            this.fetcherManager.addTask(new FetcherTask(url, UserDetailFetcher.class.getName()));
//        }
//    }
//
//
//    public void retry() {
//        this.fetcherManager.addTask(new FetcherTask(url, UserFollowingFetcher.class.getName()), true);
//    }
}

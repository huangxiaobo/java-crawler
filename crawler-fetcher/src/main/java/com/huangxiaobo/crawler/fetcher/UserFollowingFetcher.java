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
}

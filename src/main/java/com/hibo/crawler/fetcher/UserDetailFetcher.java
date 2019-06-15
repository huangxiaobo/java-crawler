package com.hibo.crawler.fetcher;

/**
 * Created by hxb on 2018/4/6.
 */
public class UserDetailFetcher extends Fetcher {

  public UserDetailFetcher(FetcherTask task) {
    super(task);
    }

//    private Logger logger = LoggerFactory.getLogger(UserDetailFetcher.class);
//
//
//    public UserDetailFetcher(String url) {
//        super(url);
//    }
//
//
//    public void parse(Page page) {
//        if (page == null || page.getHtml() == null || page.getHtml().equals("")) {
//            logger.error("empty page");
//            return;
//        }
//
//
//    }
//
//    public void retry() {
//        logger.warn("fetch " + url + " failed and will retry");
//        this.fetcherManager.addTask(new FetcherTask(url, UserDetailFetcher.class.getName()), true);
//    }
}

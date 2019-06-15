package com.hibo.crawler.processor;

import com.google.gson.Gson;
import com.hibo.crawler.Constants;
import com.hibo.crawler.element.User;
import com.hibo.crawler.fetcher.FetcherManager;
import com.hibo.crawler.fetcher.FetcherTask;
import com.hibo.crawler.fetcher.UserDetailFetcher;
import com.hibo.crawler.fetcher.UserFollowingFetcher;
import com.hibo.crawler.parser.UserDetailParser;
import com.hibo.crawler.parser.UserFollowingParser;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hxb on 2018/4/8.
 */
@Service
public class ProcessorManager {

  private Logger logger = LoggerFactory.getLogger(UserDetailParser.class);

    private ConcurrentHashMap<String, Processor> clsProcessorMap = new ConcurrentHashMap<>();

  @Autowired
  private FetcherManager fetcherManager;

  public void start() {
    setupProcessors();
  }
  //  @Bean
  //  public ProcessorManager processorManagerTemplate() {
  //      ProcessorManager pm = new ProcessorManager();
  //      pm.setupProcessors();
  //      return pm;
  //  }

    public void registerProcessor(String clsName, Processor processor) {
        clsProcessorMap.put(clsName, processor);
    }

  public void setupProcessors() {

    Processor<User> userPrintProcessor = new UserPrintProcessor();
    registerProcessor("User", userPrintProcessor);

    UserToDiskProcessor userToDiskProcessor = new UserToDiskProcessor();
    userPrintProcessor.next = userToDiskProcessor;
  }

  @RabbitListener(queues = Constants.MQ_JSON_QUEUE_NAME)
  public void receive(String message) {
    System.out.println("ProcessorManager [x] Received '" + message + "'");

    User user;
    try {
      user = new Gson().fromJson(message, User.class);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    logger.info("start download url: " + user);

    process(user);
  }

    public void process(Object obj) {
      User user = (User) obj;
      // 抓取关注他的人的所有用户详情
      String url = "https://www.zhihu.com/people/" + user.urlToken;
      this.fetcherManager.addFetchTask(
          new FetcherTask(url, UserDetailFetcher.class.getName(),
              UserDetailParser.class.getName()));

      // 抓取用户的关注者信息
      for (int j = 0; j < user.getFollowingCount() / 20 + 1; j++) {
        String followeesUrl = String.format(Constants.USER_FOLLOWEES_URL, user.urlToken, j * 20);

        this.fetcherManager.addFetchTask(
            new FetcherTask(
                followeesUrl,
                UserFollowingFetcher.class.getName(),
                UserFollowingParser.class.getName()));
      }

      if (clsProcessorMap.containsKey(obj.getClass().getSimpleName())) {
            Processor processor = clsProcessorMap.get(obj.getClass().getSimpleName());
            processor.process(obj);
        }
    }
}

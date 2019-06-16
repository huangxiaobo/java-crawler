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

  public void ProcessorManager() {

    Processor<User> userPrintProcessor = new UserPrintProcessor();
    clsProcessorMap.put("User", userPrintProcessor);

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

    logger.info("start process user: " + user.getUrl());

    process(user);
  }

  public void process(Object obj) {


    if (clsProcessorMap.containsKey(obj.getClass().getSimpleName())) {
      Processor processor = clsProcessorMap.get(obj.getClass().getSimpleName());
      processor.process(obj);
    }
  }
}

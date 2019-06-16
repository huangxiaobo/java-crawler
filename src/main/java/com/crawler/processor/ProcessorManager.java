package com.crawler.processor;

import com.crawler.Constants;
import com.crawler.dao.UserMapper;
import com.crawler.element.User;
import com.crawler.parser.UserDetailParser;
import com.google.gson.Gson;
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

  @Autowired(required = false)
  protected UserMapper userMapper;
  private Logger logger = LoggerFactory.getLogger(UserDetailParser.class);
  private ConcurrentHashMap<String, Processor> clsProcessorMap = new ConcurrentHashMap<>();

  public ProcessorManager() {
  }

  public void start() {

    Processor<User> userToDatabaseProcessor = new UserToDatabaseProcessor();
    userToDatabaseProcessor.setProcessorManager(this);
    userToDatabaseProcessor.next = null;

    Processor<User> userToDiskProcessor = new UserToDiskProcessor();
    userToDiskProcessor.setProcessorManager(this);
    userToDiskProcessor.next = userToDatabaseProcessor;

    Processor<User> userPrintProcessor = new UserPrintProcessor();
    userPrintProcessor.setProcessorManager(this);
    userPrintProcessor.next = userToDiskProcessor;

    clsProcessorMap.put("User", userPrintProcessor);
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

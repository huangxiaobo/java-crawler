package com.hibo.crawler.parser;

import com.google.gson.Gson;
import com.hibo.crawler.Constants;
import com.hibo.crawler.element.User;
import com.hibo.crawler.fetcher.FetcherManager;
import com.hibo.crawler.fetcher.FetcherTask;
import com.hibo.crawler.rabbitmq.RabbitmqClient;
import java.lang.reflect.Constructor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ParserManager {

  private static final Logger logger = LoggerFactory.getLogger(ParserManager.class);

  @Autowired
  public RabbitmqClient rabbitmqClient;

  @Autowired
  @Qualifier("parseTaskExecutor")
  private TaskExecutor fetchTaskExecutor;

  @Autowired
  private FetcherManager fetcherManager;

  public ParserManager() {

  }

  public void start() {

  }

  @RabbitListener(queues = Constants.MQ_PAGE_QUEUE_NAME)
  public void receive(String message) {
    System.out.println("ParserManager [x] Received '" + message + "'");

    ParseTask parseTask;
    try {
      parseTask = new Gson().fromJson(message, ParseTask.class);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    logger.info("start do parse task: " + ParseTask.class);

    try {
      Class<?> clazz = Class.forName(parseTask.getParserName());

      Class[] classes = new Class[]{};
      Constructor constructor = clazz.getDeclaredConstructor(classes);
      constructor.setAccessible(true);

      Parser cls = (Parser) constructor.newInstance();
      cls.setParserManager(this);
      cls.setFetcherManager(fetcherManager);

      fetchTaskExecutor.execute(() -> cls.parse(parseTask));
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("start do parse task failed:" + e);
    }
  }

  public void addProcessTasks(List<User> userList) {
    for (User user : userList) {
      addProcessTask(user);
    }
  }

  public void addProcessTask(User user) {
    String s = new Gson().toJson(user);
    rabbitmqClient.sendProcessTask(s);
  }

  public void addFetchTask(FetcherTask task) {
    fetcherManager.addFetchTask(task);
  }
}

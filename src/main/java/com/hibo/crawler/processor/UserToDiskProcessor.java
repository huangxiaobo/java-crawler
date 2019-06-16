package com.hibo.crawler.processor;

import com.hibo.crawler.element.User;
import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserToDiskProcessor extends Processor<User> {

  private Logger logger = LoggerFactory.getLogger(UserPrintProcessor.class);

  private PrintWriter out;

  public UserToDiskProcessor() {
    String savePath = "./target/users";
    logger.info("user info save path: " + savePath);
    try {
      out = new PrintWriter(savePath);
    } catch (IOException e) {
      e.printStackTrace();
      out = null;
    }
  }

  public void process(User user) {
    out.println(user.toString());
    out.flush();

    if (next != null) {
      next.process(user);
    }
  }
}

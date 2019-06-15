package com.hibo.crawler.processor;

import com.hibo.crawler.element.User;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserPrintProcessor extends Processor<User> {

  public UserPrintProcessor() {
    // 连接rabbitmq
  }

  public void process(User user) {

    System.out.println(">>>>>" + user.toString());

    if (next != null) {
      next.process(user);
    }
  }
}

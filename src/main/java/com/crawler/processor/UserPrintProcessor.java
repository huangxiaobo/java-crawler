package com.crawler.processor;

import com.crawler.element.User;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserPrintProcessor extends Processor<User> {

  public void process(User user) {

    System.out.println(">>>>>" + user.toString());

    if (next != null) {
      next.process(user);
    }
  }
}

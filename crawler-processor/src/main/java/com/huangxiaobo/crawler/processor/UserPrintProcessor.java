package com.huangxiaobo.crawler.processor;

import com.huangxiaobo.crawler.common.User;

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

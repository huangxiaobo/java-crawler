package com.huangxiaobo.crawler.processor;
import com.huangxiaobo.crawler.common.User;

public class UserToDatabaseProcessor extends Processor<User> {

  public void process(User user) {

    processorManager.userMapper.insertUser(user);

    if (next != null) {
      next.process(user);
    }
  }
}

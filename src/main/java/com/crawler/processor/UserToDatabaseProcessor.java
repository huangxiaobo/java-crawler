package com.crawler.processor;

import com.crawler.element.User;

public class UserToDatabaseProcessor extends Processor<User> {

  public void process(User user) {

    processorManager.userMapper.insertUser(user);

    if (next != null) {
      next.process(user);
    }
  }
}

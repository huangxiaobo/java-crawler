package com.crawler.pipeline;

import com.crawler.element.User;
import org.springframework.stereotype.Component;

/**
 * Created by hxb on 2018/4/8.
 */
@Component
public class UserPrintProcessor implements Processor<User> {

    public void process(User user) {

        System.out.println(">>>>>" + user.toString());
    }
}

package com.crawl.pipeline;

import com.crawl.element.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserPrintPipeline implements Pipeline<User> {

    public void process(User user) {

        System.out.println(">>>>>" + user.toString());
    }
}

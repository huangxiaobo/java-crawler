package com.crawler.zhihu.pipeline;

import com.crawler.zhihu.element.User;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserPrintPipeline implements Pipeline<User> {

    public void process(User user) {

        System.out.println(">>>>>" + user.toString());
    }
}

package com.crawler.processor;

import com.crawler.Config;
import com.crawler.element.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by hxb on 2018/4/8.
 */
public class UserToDiskProcessor extends Processor<User> {

    private Logger logger = LoggerFactory.getLogger(UserPrintProcessor.class);

    private PrintWriter out = null;


    public UserToDiskProcessor() {
        try {
            out = new PrintWriter(Config.savePath);
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

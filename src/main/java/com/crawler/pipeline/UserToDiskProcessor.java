package com.crawler.pipeline;

import com.crawler.Config;
import com.crawler.element.User;
import java.io.IOException;
import java.io.PrintWriter;

import com.crawler.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hxb on 2018/4/8.
 */
@Component
public class UserToDiskProcessor implements Processor<User> {

    private Logger logger = LoggerFactory.getLogger(UserPrintProcessor.class);

    private PrintWriter out = null;

    @Autowired
    private UserMapper userMapper;

    public UserToDiskProcessor() {
        try {
            out = new PrintWriter(Config.savePath);
        } catch (IOException e) {
            e.printStackTrace();
            out = null;
        }
    }

    public void prepare() {
        userMapper.deleteAll();
    }


    public void process(User user) {
        out.println(user.toString());
        out.flush();
        userMapper.insert(user);
    }
}

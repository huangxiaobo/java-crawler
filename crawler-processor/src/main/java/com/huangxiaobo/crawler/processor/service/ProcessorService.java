package com.huangxiaobo.crawler.processor.service;

import com.google.gson.Gson;
import com.huangxiaobo.crawler.common.User;
import com.huangxiaobo.crawler.common.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by hxb on 2018/4/8.
 */
@Component
@Lazy(false)
public class ProcessorService {

    private Logger logger = LoggerFactory.getLogger(ProcessorService.class);

    /**
     * mapper
     */
    private UserMapper userMapper;

    public ProcessorService(@Autowired UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RabbitListener(queues = "zhihu.user.json.queue")
    public void receive(String message) {
        logger.info("ProcessorManager [x] Received '" + message + "'");

        User user;
        try {
            user = new Gson().fromJson(message, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        logger.info("user: " + user.toString());
        try {
            userMapper.insertUser(user);
        } catch (Exception e) {
            logger.error("user: " + user + " err: " + e.toString());
        }

    }

}

package com.huangxiaobo.crawler.web;

import com.huangxiaobo.crawler.common.User;
import com.huangxiaobo.crawler.common.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private UserMapper userMapper;

    public UserController(@Autowired UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RequestMapping(value = "/")
    public Long index() {
        return userMapper.countAll();
    }

    @RequestMapping(value = "/users")
    public List<User> getUsers() {
        return userMapper.findAll();
    }
}

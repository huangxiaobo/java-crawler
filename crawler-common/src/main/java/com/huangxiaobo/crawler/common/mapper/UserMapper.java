package com.huangxiaobo.crawler.common.mapper;

import com.huangxiaobo.crawler.common.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    Long countAll();

    List<User> findAll();

    void insertUser(User user);
}

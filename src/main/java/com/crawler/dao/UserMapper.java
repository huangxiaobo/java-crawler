package com.crawler.dao;

import com.crawler.element.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

  List<User> findAll();

  void insertUser(User user);
}

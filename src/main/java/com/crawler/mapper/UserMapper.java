package com.crawler.mapper;

import com.crawler.element.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserMapper {
    // name, urlToken, gender, headline, url

    @Select("SELECT * FROM t_user")
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "id", column = "id"),
            @Result(property = "urlToken", column = "token")
    })
    List<User> getAll();

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "urlToken", column = "token")
    })
    User getOne(Long id);

    @Insert("INSERT INTO t_user(id, name, token,headline, url, following_count, follower_count) VALUES(#{id}, #{name}, #{urlToken}, #{headline}, #{url}, #{followingCount}, #{followerCount})")
    void insert(User user);

    @Update("UPDATE t_user SET name=#{name}} WHERE id =#{id}")
    void update(User user);

    @Delete("DELETE FROM t_user WHERE id =#{id}")
    void delete(Long id);


    @Delete("DELETE FROM t_user")
    void deleteAll();
}
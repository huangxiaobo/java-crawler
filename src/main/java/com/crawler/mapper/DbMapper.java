package com.crawler.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DbMapper {

    @Delete("DELETE FROM t_user")
    void delete_user();
}

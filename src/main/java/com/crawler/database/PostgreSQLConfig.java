package com.crawler.database;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PostgreSQLConfig {

//  @Bean
//  public DataSource dataSource() {
//    DruidDataSource dataSource = new DruidDataSource();
//    dataSource.setUrl(env.getProperty("spring.datasource.url"));
//    dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
//    dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
//    dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
//    dataSource.setInitialSize(2);
//    dataSource.setMaxActive(20);
//    dataSource.setMinIdle(0);
//    dataSource.setMaxWait(60000);
//    dataSource.setValidationQuery("SELECT 1");
//    dataSource.setTestOnBorrow(false);
//    dataSource.setTestWhileIdle(true);
//    dataSource.setPoolPreparedStatements(false);
//    return dataSource;
//  }
}

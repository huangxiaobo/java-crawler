package com.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@SpringBootApplication
public class Crawler {

    //@Override
    //protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    //    return application.sources(Crawler.class);
    //}

    @Bean
    public CrawlerRunner crawlerRunner() {
        return new CrawlerRunner();
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(Crawler.class, args);
    }

    @RequestMapping("/")
    String hello()
    {
        return "Hello World! JavaInterviewPoint2222222";
    }

}
package com.huangxiaobo.crawler.fetcher.controllers;

import com.huangxiaobo.crawler.common.FetcherTask;
import com.huangxiaobo.crawler.fetcher.FetcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FetcherController {

    private Logger logger = LoggerFactory.getLogger(FetcherManager.class);

    @Autowired
    private FetcherManager fetcherManager;

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    public String submitTask(@RequestBody FetcherTask task) {
        logger.info("add fetch task" + task);
        if (task.url == null) {
            logger.info("task is invalid.");
            return null;
        }
        fetcherManager.addFetchTask(task);

        return "successfully";
    }
}

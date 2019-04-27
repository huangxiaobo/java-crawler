package com.crawler.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ParserManager {

    private static final Logger logger = LoggerFactory.getLogger(ParserManager.class);

    private Map<String, Class> parsers;
    private LinkedBlockingDeque<ParseTask> parseTasks;
    private ThreadPoolExecutor parserPools = null;

    public ParserManager() {
        parsers = new HashMap<>();

        this.registerParser(UserDetailParser.class);

        parseTasks = new LinkedBlockingDeque<>(1 << 10);

        parserPools = new ThreadPoolExecutor(30, 50, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>()
        );

    }

    public void registerParser(Class parser) {
        parsers.put(parser.getClass().getSimpleName(), parser);
    }


    public void addParseTask(ParseTask parseTask) {
        this.parseTasks.add(parseTask);
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                ParseTask task;
                try {
                    task = parseTasks.take();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                parserPools.execute(() -> {
                        try {
                            Parser parser = (Parser) task.parser.newInstance();
                            logger.info("start parse page: " + task.page.getUrl());

                            parser.parse(task.page);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                );

            }
        }).start();
    }
}

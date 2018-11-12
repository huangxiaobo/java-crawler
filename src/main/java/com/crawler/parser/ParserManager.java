package com.crawler.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ParserManager {


    private Map<String, Class> parsers;
    private LinkedBlockingDeque<ParseTask> parseTasks;
    private ThreadPoolExecutor parserPools = null;

    public ParserManager() {
        parsers = new HashMap<>();

        this.registerParser(UserDetailParser.class);
        this.registerParser(UserFollowingParser.class);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
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


                    parserPools.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Parser parser = (Parser) task.parser.newInstance();
                                parser.parse(task.page);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        }).start();
    }
}

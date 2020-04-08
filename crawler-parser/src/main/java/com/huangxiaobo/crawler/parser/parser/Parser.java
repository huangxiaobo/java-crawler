package com.huangxiaobo.crawler.parser.parser;

import com.huangxiaobo.crawler.common.ParseTask;
import com.huangxiaobo.crawler.parser.service.ParserService;

/**
 * Created by hxb on 2018/4/6.
 */
public abstract class Parser implements Runnable {

    protected ParseTask parseTask;
    protected ParserService parserManager;

    public Parser(ParseTask task) {
        this.parseTask = task;
    }

    public ParserService getParserManager() {
        return parserManager;
    }

    public void setParserManager(ParserService parserManager) {
        this.parserManager = parserManager;
    }

    public abstract void parse(ParseTask task);

    @Override
    public void run() {
        parse(parseTask);
    }
}

package com.huangxiaobo.crawler.parser;

import com.huangxiaobo.crawler.common.ParseTask;

/**
 * Created by hxb on 2018/4/6.
 */
public abstract class Parser implements Runnable {

  protected ParseTask parseTask;
  protected ParserManager parserManager;

  public Parser(ParseTask task) {
    this.parseTask = task;
  }

  public ParserManager getParserManager() {
    return parserManager;
  }

  public void setParserManager(ParserManager parserManager) {
    this.parserManager = parserManager;
  }

  public abstract void parse(ParseTask task);

  @Override
  public void run() {
    parse(parseTask);
  }
}

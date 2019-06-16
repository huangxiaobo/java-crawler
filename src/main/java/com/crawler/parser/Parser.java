package com.crawler.parser;

import com.crawler.fetcher.FetcherManager;

/**
 * Created by hxb on 2018/4/6.
 */
public abstract class Parser implements Runnable {

  protected ParseTask parseTask;
  protected ParserManager parserManager;
  protected FetcherManager fetcherManager;

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

  public FetcherManager getFetcherManager() {
    return fetcherManager;
  }

  public void setFetcherManager(FetcherManager fetcherManager) {
    this.fetcherManager = fetcherManager;
  }

  @Override
  public void run() {
    parse(parseTask);
  }
}

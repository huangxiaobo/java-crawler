package com.hibo.crawler.parser;

import com.hibo.crawler.fetcher.FetcherManager;

/**
 * Created by hxb on 2018/4/6.
 */
public class Parser {

  protected ParserManager parserManager;
  protected FetcherManager fetcherManager;

  public Parser() {

  }

  public ParserManager getParserManager() {
    return parserManager;
  }

  public void setParserManager(ParserManager parserManager) {
    this.parserManager = parserManager;
  }

  public Object parse(ParseTask task) {
    return null;
  }

  public FetcherManager getFetcherManager() {
    return fetcherManager;
  }

  public void setFetcherManager(FetcherManager fetcherManager) {
    this.fetcherManager = fetcherManager;
  }
}

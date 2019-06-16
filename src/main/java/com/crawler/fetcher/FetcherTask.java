package com.crawler.fetcher;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
抓取任务，通过mq传递
 */

@Data
public class FetcherTask {

  private static Logger logger = LoggerFactory.getLogger(Fetcher.class);
  public String url;
  public String fetcherClassName;
  public String parserClassName;

  public FetcherTask(String url, String fetcherClassName, String parserClassName) {
    this.url = url;
    this.fetcherClassName = fetcherClassName;
    this.parserClassName = parserClassName;
  }
}
package com.huangxiaobo.crawler.common;

import lombok.Data;

@Data
public class ParseTask {

  private String content;
  private String parserName;                  // 解析器名字

  public ParseTask(String content, String parserName) {
    this.content = content;
    this.parserName = parserName;
  }

  public String toString() {
    return String.format("ParseTask(parserName=%s)", parserName);
  }
}

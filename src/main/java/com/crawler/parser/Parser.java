package com.crawler.parser;

import com.crawler.element.Page;

/**
 * Created by hxb on 2018/4/6.
 */
public abstract interface  Parser {


    Object parse(Page page);
}

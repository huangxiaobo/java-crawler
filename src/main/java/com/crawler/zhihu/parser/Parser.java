package com.crawler.zhihu.parser;

import com.crawler.zhihu.element.Page;

/**
 * Created by hxb on 2018/4/6.
 */
public interface Parser {

    Object parse(String userId, Page page);
}

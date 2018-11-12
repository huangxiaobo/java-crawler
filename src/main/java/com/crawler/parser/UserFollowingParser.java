package com.crawler.parser;

import com.crawler.element.Page;

public class UserFollowingParser implements Parser {
    public Object parse(Page page) {
        System.out.println(page.getHtml());
        return null;
    }
}

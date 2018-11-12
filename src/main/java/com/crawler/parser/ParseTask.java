package com.crawler.parser;

import com.crawler.element.Page;

public class ParseTask {
    Class parser;
    Page page;

    public ParseTask(Class parser, Page page) {
        this.parser = parser;
        this.page = page;
    }
}

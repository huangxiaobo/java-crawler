package com.crawl.parser;

import com.crawl.element.Page;
import java.util.List;

/**
 * Created by hxb on 2018/4/6.
 */
public interface ListPageParser extends Parser{
    List parseListPage(Page page);
}

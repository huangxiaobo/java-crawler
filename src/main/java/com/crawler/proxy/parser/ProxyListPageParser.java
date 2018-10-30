package com.crawler.proxy.parser;


import com.crawler.proxy.Proxy;
import java.util.List;


public interface ProxyListPageParser{
    /**
     * 是否只要匿名代理
     */
    static final boolean anonymousFlag = true;
    List<Proxy> parse(String content);
}

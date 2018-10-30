package com.crawler.proxy;

import com.crawler.proxy.parser.ProxyListPageParser;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hxb on 2018/4/6.
 */
public class ProxyListPageParserFactory {

    private static Map<String, ProxyListPageParser> map = new HashMap<>();

    public static ProxyListPageParser getProxyListPageParser(Class clazz) {
        String parserName = clazz.getSimpleName();

        if (map.containsKey(parserName)) {
            return map.get(parserName);
        } else {
            try {
                ProxyListPageParser proxyListPageParser = (ProxyListPageParser) clazz.newInstance();
                parserName = clazz.getSimpleName();
                map.put(parserName, proxyListPageParser);
                return proxyListPageParser;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

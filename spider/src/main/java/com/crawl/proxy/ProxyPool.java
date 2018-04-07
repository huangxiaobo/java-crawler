package com.crawl.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawl.proxy.site.XicidailiProxyListPageParser;
import com.crawl.proxy.site.MimiipProxyListPageParser;
import com.crawl.proxy.site.Ip181ProxyListPageParser;
import com.crawl.proxy.site.Ip66ProxyListPageParser;

import static com.crawl.Constants.TIME_INTERVAL;

/**
 *
 */
public class ProxyPool {

    // proxySet读写锁
    public final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public final static DelayQueue<Proxy> proxyQueue = new DelayQueue();
    public final static Set<Proxy> proxySet = new HashSet<Proxy>();
    public final static Map<String, Class> proxyMap = new HashMap<String, Class>();

    static {

        int pages = 8;
        for (int i = 1; i <= pages; i++) {
            proxyMap.put("http://www.xicidaili.com/wt/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.xicidaili.com/nn/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.xicidaili.com/wn/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.xicidaili.com/nt/" + i + ".html", XicidailiProxyListPageParser.class);
            proxyMap.put("http://www.ip181.com/daili/" + i + ".html", Ip181ProxyListPageParser.class);
            proxyMap.put("http://www.mimiip.com/gngao/" + i, MimiipProxyListPageParser.class);//高匿
            proxyMap.put("http://www.mimiip.com/gnpu/" + i, MimiipProxyListPageParser.class);//普匿
            proxyMap.put("http://www.66ip.cn/" + i + ".html", Ip66ProxyListPageParser.class);
            for (int j = 1; j < 34; j++) {
                proxyMap.put("http://www.66ip.cn/areaindex_" + j + "/" + i + ".html", Ip66ProxyListPageParser.class);
            }
        }
        proxyQueue.add(new Direct(TIME_INTERVAL));
    }

    public static Proxy getProxy() throws InterruptedException {
        return proxyQueue.take();
    }
}

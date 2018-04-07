package com.crawl.proxy.site;


import com.crawl.proxy.parser.ProxyListPageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.crawl.Constants.TIME_INTERVAL;

import java.util.ArrayList;
import java.util.List;
import com.crawl.proxy.Proxy;


public class XicidailiProxyListPageParser implements ProxyListPageParser {

    @Override
    public List<Proxy> parse(String hmtl) {
        Document document = Jsoup.parse(hmtl);
        Elements elements = document.select("table[id=ip_list] tr[class]");
        List<Proxy> proxyList = new ArrayList<Proxy>(elements.size());
        for (Element element : elements) {
            String ip = element.select("td:eq(1)").first().text();
            String port = element.select("td:eq(2)").first().text();
            String isAnonymous = element.select("td:eq(4)").first().text();
            if (!anonymousFlag || isAnonymous.contains("匿")) {
                proxyList.add(new Proxy(ip, Integer.valueOf(port), TIME_INTERVAL));
            }
        }
        return proxyList;
    }
}

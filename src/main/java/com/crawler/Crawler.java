package com.crawler;

import com.crawler.scheduler.Scheduler;
import com.crawler.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler  {

    private static Logger logger = LoggerFactory.getLogger(Crawler.class);

    private Scheduler scheduler;

    /**
     * request　header
     * 获取列表页时，必须带上
     */
    private static String authorization;


    /**
     * 初始化authorization
     */
    private static void initAuthorization() {
        logger.info("初始化authorization中...");
        String content;

        try {
            content = HttpClientUtil.getWebPage(Config.startURL);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("get ProxParser url failed.");
        }

        Pattern pattern = Pattern
                .compile("https://static\\.zhihu\\.com/heifetz/main\\.app\\.([0-9]|[a-z])*\\.js");
        Matcher matcher = pattern.matcher(content);
        String jsSrc;
        if (matcher.find()) {
            jsSrc = matcher.group(0);
        } else {
            throw new RuntimeException("not find javascript url");
        }
        String jsContent = null;
        try {
            jsContent = HttpClientUtil.getWebPage(jsSrc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("get javascript content failed.");
        }

        pattern = Pattern.compile("oauth (([0-9]|[a-z])+)");
        matcher = pattern.matcher(jsContent);
        if (matcher.find()) {
            String a = matcher.group(1);
            logger.info("初始化authorization完成");
            authorization = a;
        } else {
            throw new RuntimeException("not get authorization");
        }
    }

    public static String getAuthorization() {
        if (authorization == null) {
            initAuthorization();
        }
        return authorization;
    }



    public void run() {
        new Thread(new Scheduler()).start();
    }

    public static void main(String[] argv)  {
        Crawler crawler = new Crawler();
        crawler.run();
    }


}
package com.crawler;

import com.crawler.scheduler.Scheduler;
import com.crawler.utils.HttpClientUtil;
import com.crawler.element.Page;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class CrawlerRunner implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(Crawler.class);

    @Autowired
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



    public Page getWebPage(String url) throws IOException {
        return getWebPage(url, "UTF-8");
    }

    public Page getWebPage(String url, String charset) throws IOException {
        Page page = new Page();
        CloseableHttpResponse response = HttpClientUtil.getResponse(url);
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setUrl(url);
        try {
            if (page.getStatusCode() == 200) {
                page.setHtml(EntityUtils.toString(response.getEntity(), charset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return page;
    }

    public Page getWebPage(HttpRequestBase request) throws IOException {
        CloseableHttpResponse response = null;
        response = HttpClientUtil.getResponse(request);
        Page page = new Page();
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setHtml(EntityUtils.toString(response.getEntity()));
        page.setUrl(request.getURI().toString());
        return page;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(scheduler).start();
        while (true) {
            Thread.sleep(1000);
        }
    }


}
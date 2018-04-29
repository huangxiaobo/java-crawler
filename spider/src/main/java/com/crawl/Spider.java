package com.crawl;

import com.crawl.proxy.ProxyHttpClient;
import com.crawl.utils.HttpClientUtil;
import com.crawl.zhihu.ZhihuUserUrlTokenQueue;
import com.crawl.zhihu.bloomfilter.BloomFilter;
import com.crawl.zhihu.bloomfilter.MemoryBloomFilter;
import com.crawl.zhihu.element.Page;
import com.crawl.zhihu.element.User;
import com.crawl.zhihu.monitor.UserDetailTaskMonitor;
import com.crawl.zhihu.monitor.UserPersistenceTaskMonitor;
import com.crawl.zhihu.pipeline.PipelineManager;
import com.crawl.zhihu.pipeline.UserPrintPipeline;
import com.crawl.zhihu.pipeline.UserToDiskPipeline;
import com.crawl.zhihu.task.UserDetailTask;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spider {

    private static Logger logger = LoggerFactory.getLogger(Spider.class);

    private static Spider instance = null;

    public static ZhihuUserUrlTokenQueue zhihuUserUrlTokenQueue = ZhihuUserUrlTokenQueue
        .getInstance();

    //创建线程池，池中保存的线程数为3，允许的最大线程数为5
    public ThreadPoolExecutor pool = null;

    public ThreadPoolExecutor persistencePool = null;

    public PipelineManager<User> userPipelineManager = null;

    public BloomFilter bloomFilter = null;

    /**
     * request　header
     * 获取列表页时，必须带上
     */
    private static String authorization;

    public static Spider getInstance() {
        if (instance == null) {
            synchronized (Spider.class) {
                if (instance == null) {
                    instance = new Spider();
                }
            }
        }
        return instance;
    }

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

    private void initThreadPool() {
        pool = new ThreadPoolExecutor(30, 50, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>()
        );

        persistencePool = new ThreadPoolExecutor(30, 50, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1000),
            new ThreadPoolExecutor.DiscardPolicy()
        );

        // 创建监视线程
        new Thread(new UserPersistenceTaskMonitor(persistencePool)).start();
        new Thread(new UserDetailTaskMonitor(pool)).start();
    }

    private void initPipeline() {
        userPipelineManager = new PipelineManager<>();
        userPipelineManager.addPipeline(new UserPrintPipeline());
        userPipelineManager.addPipeline(new UserToDiskPipeline());
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

    public void start() {
        initThreadPool();
        initPipeline();

        bloomFilter = new MemoryBloomFilter();
        // 代理
        ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
        proxyHttpClient.start();
        // 用户信息
        pool.execute(new UserDetailTask(Config.startUserToken));
    }


    public static void main(String[] argv) {
        Spider spider = Spider.getInstance();
        spider.start();
    }
}
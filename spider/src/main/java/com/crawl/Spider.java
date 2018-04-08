package com.crawl;

import com.crawl.element.User;
import com.crawl.monitor.UserPersistenceTaskMonitor;
import com.crawl.pipeline.PipelineManager;
import com.crawl.pipeline.UserPersistencePipeline;
import com.crawl.pipeline.UserPrintPipeline;
import com.crawl.proxy.ProxyHttpClient;
import com.crawl.proxy.ProxyPool;
import com.crawl.task.UserDetailTask;
import com.crawl.zhihu.ZhihuUserUrlTokenQueue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.crawl.element.Page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {

    private static Logger logger = LoggerFactory.getLogger(Spider.class);
    //邮箱登录地址
    final private static String EMAIL_LOGIN_URL = "https://www.zhihu.com/login/email";
    //登录验证码地址
    final private static String YZM_URL = "https://www.zhihu.com/captcha.gif?type=login";

    private static Spider instance = null;

    public static ZhihuUserUrlTokenQueue zhihuUserUrlTokenQueue = ZhihuUserUrlTokenQueue
        .getInstance();

    public static ProxyPool proxyPool = new ProxyPool();
//    public static ProxyPool proxy;

    //创建线程池，池中保存的线程数为3，允许的最大线程数为5
    public ThreadPoolExecutor pool = null;

    public ThreadPoolExecutor persistencePool = null;

    public PipelineManager<User> userPipelineManager = null;


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
        logger.info("初始化authoriztion中...");
        String content = null;

        try {
            content = HttpClientUtil.getWebPage(Config.startURL);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("get ProxParser url failed.");
        }

        Pattern pattern = Pattern
            .compile("https://static\\.zhihu\\.com/heifetz/main\\.app\\.([0-9]|[a-z])*\\.js");
        Matcher matcher = pattern.matcher(content);
        String jsSrc = null;
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
            logger.info("初始化authoriztion完成");
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
    }

    private void initPipeline() {
        userPipelineManager=  new PipelineManager<>();
        userPipelineManager.addPipeline(new UserPrintPipeline());
        userPipelineManager.addPipeline(new UserPersistencePipeline());
    }

    public boolean login() {
        String loginState = null;
        Map<String, String> postParams = new HashMap();

        String yzm = yzm(YZM_URL);//肉眼识别验证码

        postParams.put("captcha", yzm);
        postParams.put("_xsrf", "464d07628dc5d61f9dfb9b4ae5d33838");
        postParams.put("password", "huang7818!");
        postParams.put("remember_me", "true");
        postParams.put("email", "767806886@qq.com"); //通过邮箱登录
        try {
            loginState = HttpClientUtil.postRequest(EMAIL_LOGIN_URL, postParams);//登录
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" " + loginState);
        JSONObject jo = (JSONObject) JSONValue.parse(loginState);
        if (jo.get("r").toString().equals("0")) {
            logger.info("登录知乎成功");
            /**
             * 序列化Cookies
             */
            HttpClientUtil.serializeObject(HttpClientUtil.getCookieStore(), Config.cookiePath);
            return true;
        } else {
            logger.info("登录知乎失败");
            throw new RuntimeException(HttpClientUtil.decodeUnicode(loginState));
        }
    }

    /**
     * 肉眼识别验证码
     *
     * @param url 验证码地址
     */
    public String yzm(String url) {
        String verificationCodePath = Config.verificationCodePath;
        System.out.println(" " + verificationCodePath);
        String path = verificationCodePath.substring(0, verificationCodePath.lastIndexOf("/") + 1);
        String fileName = verificationCodePath.substring(verificationCodePath.lastIndexOf("/") + 1);
        HttpClientUtil.downloadFile(url, path, fileName, true);
        logger.info("请输入 " + verificationCodePath + " 下的验证码：");
        Scanner sc = new Scanner(System.in);
        String yzm = sc.nextLine();
        return yzm;
    }

    public Page getWebPage(String url) throws IOException {
        return getWebPage(url, "UTF-8");
    }
    public Page getWebPage(String url, String charset) throws IOException {
        Page page = new Page();
        CloseableHttpResponse response = null;
        response = HttpClientUtil.getResponse(url);
        page.setStatusCode(response.getStatusLine().getStatusCode());
        page.setUrl(url);
        try {
            if(page.getStatusCode() == 200){
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

        // 代理
        ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();
        proxyHttpClient.start();

        // 用户信息
        pool.execute(new UserDetailTask(Config.startUserToken));



//        ZhihuTopicCategoryParser zhihuTopicCategoryParser = ZhihuTopicCategoryParser.getInstance();
//        zhihuTopicCategoryParser.ProxParser();
//
//        ZhihuTopicParser zhihuTopicParser = ZhihuTopicParser.getInstance();
//        zhihuTopicParser.ProxParser();

//        if (false) {
//
//            try {
//                Page page = new Page(HttpClientUtil.getWebPage("https://www.zhihu.com/topics"));
//                crawlTopicCategory(page);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            StringBuffer sb = new StringBuffer();
//            try {
//                FileUtils.readToBuffer(sb,
//                    "D:\\workspace\\java\\spider\\src\\main\\resources\\topic.html");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Page page = new Page(sb.toString());
//            crawlTopicCategory(page);
//        }
    }


    public static void main(String[] argv) {
        Spider spider = Spider.getInstance();

//        spider.login();
        spider.start();
    }
}
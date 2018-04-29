package com.crawl;

import java.io.IOException;
import java.util.Properties;

/**
 * 加载配置文件
 */
public class Config {

    /**
     * 是否使用代理抓取
     */
    public static boolean isProxy;
    /**
     * 下载网页线程数
     */
    public static int downloadThreadSize;
    /**
     * 验证码路径
     */
    public static String verificationCodePath;
    /**
     * 爬虫入口
     */
    public static String startURL;

    public static String startUserToken;
    /**
     * 下载网页数
     */
    public static int downloadPageCount;
    /**
     * cookie路径
     */
    public static String cookiePath;
    /**
     * proxyPath
     */
    public static String proxyPath;
    /**
     * user 保存路径
     */
    public static String savePath;

    static {
        Properties p = new Properties();
        try {
            p.load(Config.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        verificationCodePath = p.getProperty("verificationCodePath");
        startURL = p.getProperty("startURL");
        startUserToken = p.getProperty("startUserToken");
        downloadPageCount = Integer.valueOf(p.getProperty("downloadPageCount"));
        downloadThreadSize = Integer.valueOf(p.getProperty("downloadThreadSize"));
        cookiePath = p.getProperty("cookiePath");
        proxyPath = p.getProperty("proxyPath");
        savePath = p.getProperty("savePath");
        isProxy = Boolean.valueOf(p.getProperty("isProxy"));
    }
}

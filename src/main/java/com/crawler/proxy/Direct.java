package com.crawler.proxy;

/**
 * Created by hxb on 2018/4/6.
 */
public class Direct extends Proxy {

    public Direct(String ip, int port, long delayTime) {
        super(ip, port, delayTime);
    }

    public Direct(long delayTime) {
        this("", 0, delayTime);
    }
}

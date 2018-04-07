package com.crawl.element;

import com.crawl.proxy.Proxy;

/**
 * 网页页面对象
 */
public class Page {

    private String html = null;                 // 页面内容
    private int statusCode = 200;               // 请求返回码
    private String url = null;                  // 页面url
    private Proxy proxy = null;                 // 请求页面时使用的代理

    public Page() {

    }

    public Page(String html) {
        this.html = html;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}

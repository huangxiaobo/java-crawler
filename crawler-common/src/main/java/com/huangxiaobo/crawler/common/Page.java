package com.huangxiaobo.crawler.common;

import lombok.Data;

/**
 * 网页页面对象
 */
@Data
public class Page {

    private String html;                        // 页面内容
    private int statusCode = 200;               // 请求返回码
    private String url = null;                  // 页面url
    private Proxy proxy = null;                 // 请求页面时使用的代理


    public Page(String html) {
        this.html = html;
    }
}

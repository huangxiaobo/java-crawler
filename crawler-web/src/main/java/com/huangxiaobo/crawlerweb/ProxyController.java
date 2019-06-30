package com.huangxiaobo.crawlerweb;

import com.huangxiaobo.crawler.proxy.ProxyPoolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

  @Autowired
  private ProxyPoolManager proxyPoolManager;

  @RequestMapping(value = "/proxy")
  public int proxy() {

    return proxyPoolManager.getProxyCount();
  }
}

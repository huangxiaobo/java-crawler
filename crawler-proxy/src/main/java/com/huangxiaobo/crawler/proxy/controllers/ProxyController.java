package com.huangxiaobo.crawler.proxy.controllers;

import com.huangxiaobo.crawler.common.Proxy;
import com.huangxiaobo.crawler.proxy.ProxyPoolManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyController {

  @Autowired
  private ProxyPoolManager proxyPoolManager;

  @GetMapping(value = "/", produces = "application/json")
  public List<Proxy> getProxies(@RequestParam("count") int count) {

    List<Proxy> proxyList = new ArrayList<>(count);

    while (proxyList.size() < count) {
      Proxy proxy = proxyPoolManager.getProxyNowait();
      if (proxy == null) {
        break;
      }
      proxyList.add(proxyPoolManager.getProxyNowait());
    }

    return proxyList;
  }

  @PostMapping(value = "/", consumes = "application/json")
  public String putProxies(@RequestBody List<Proxy> proxies) {
    for (Proxy proxy : proxies) {
      proxyPoolManager.addProxy(proxy);
    }

    return "";
  }

  @GetMapping(value = "/size")
  public int size() {
    return proxyPoolManager.getProxyCount();
  }
}

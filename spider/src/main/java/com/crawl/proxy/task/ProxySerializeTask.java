package com.crawl.proxy.task;

import com.crawl.Config;
import com.crawl.utils.HttpClientUtil;
import com.crawl.proxy.Proxy;
import com.crawl.proxy.ProxyPool;

/**
 * Created by hxb on 2018/4/6.
 */
public class ProxySerializeTask implements Runnable{

    public void run() {
        while (true) {
            try{
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Proxy[] proxyArray = null;
            ProxyPool.lock.readLock().lock();
            try{
                proxyArray = new Proxy[ProxyPool.proxySet.size()];
                int i = 0;
                for (Proxy p : ProxyPool.proxySet) {
                    if (!p.isDiscardProxy()) {
                        proxyArray[i++] = p;
                    }
                }
            } finally {
                ProxyPool.lock.readLock().unlock();
            }

            HttpClientUtil.serializeObject(proxyArray, Config.proxyPath);

        }
    }

}

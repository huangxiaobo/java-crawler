package com.crawler.zhihu.bloomfilter;

/**
 * Created by hxb on 2018/4/9.
 */
public interface BloomFilter {

    boolean contains(String urlToken);

    void add(String urlToken);


}

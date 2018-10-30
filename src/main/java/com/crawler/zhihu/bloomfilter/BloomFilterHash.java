package com.crawler.zhihu.bloomfilter;

/**
 * Created by hxb on 2018/4/10.
 */
public class BloomFilterHash {
    private int cap;
    private int seed;

    public BloomFilterHash(int cap, int seed) {
        this.cap = cap;
        this.seed = seed;
    }

    public int hash(String value) {
        int result = 0;
        for (int i = 0; i < value.length(); ++i) {
            result = result * seed + value.charAt(i);
        }
        return (cap - 1) & result;
    }
}

package com.crawl.zhihu.bloomfilter;

import java.util.BitSet;

public class MemoryBloomFilter implements BloomFilter {

    /* BitSet初始分配2^24个bit */
    private static final int DEFAULT_SIZE = 1 << 25;

    /* 不同哈希函数的种子，一般应取质数 */
    private static final int[] seeds = new int[]{5, 7, 11, 13, 31, 37, 61};

    private BitSet bits = new BitSet(DEFAULT_SIZE);

    /* 哈希函数对象 */
    private BloomFilterHash[] func = new BloomFilterHash[seeds.length];

    public MemoryBloomFilter() {
        for (int i = 0; i < seeds.length; i++) {
            func[i] = new BloomFilterHash(DEFAULT_SIZE, seeds[i]);
        }
    }

    // 将字符串标记到bits中
    public void add(String value) {
        for (BloomFilterHash f : func) {
            bits.set(f.hash(value), true);
        }
    }

    // 判断字符串是否已经被bits标记
    public boolean contains(String value) {
        if (value == null) {
            return false;
        }

        boolean ret = true;
        for (BloomFilterHash f : func) {
            ret = ret && bits.get(f.hash(value));
        }

        return ret;
    }
}
package com.crawler.zhihu.pipeline;

/**
 * Created by hxb on 2018/4/8.
 */
interface Pipeline<E> {
    void process(E e);
}

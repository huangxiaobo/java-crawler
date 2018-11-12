package com.crawler.pipeline;

/**
 * Created by hxb on 2018/4/8.
 */
interface Processor<E> {
    void process(E e);

}

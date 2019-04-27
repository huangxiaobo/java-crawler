package com.crawler.processor;

/**
 * Created by hxb on 2018/4/8.
 */
abstract class Processor<E> {
    abstract void process(E e);

    public Processor<E> next;
}

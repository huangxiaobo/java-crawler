package com.hibo.crawler.processor;

/**
 * Created by hxb on 2018/4/8.
 */
public abstract class Processor<E> {

  public Processor<E> next;

  public abstract void process(E e);
}

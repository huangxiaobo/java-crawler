package com.huangxiaobo.crawler.processor;

/**
 * Created by hxb on 2018/4/8.
 */
public abstract class Processor<E> {

    public Processor<E> next;
    protected ProcessorManager processorManager;

    public Processor() {
    }

    public void setProcessorManager(ProcessorManager processorManager) {
        this.processorManager = processorManager;
    }

    public abstract void process(E e);
}

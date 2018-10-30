package com.crawler.zhihu.pipeline;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hxb on 2018/4/8.
 */
public class PipelineManager<E> {

    private LinkedBlockingQueue<Pipeline<E>> pipelineList = new LinkedBlockingQueue<>();

    public PipelineManager() {

    }

    public void addPipeline(Pipeline<E> pipeline) {
        pipelineList.add(pipeline);
    }

    public void process(E e) {
        for (Pipeline<E> pipeline : pipelineList) {
            pipeline.process(e);
        }
    }
}

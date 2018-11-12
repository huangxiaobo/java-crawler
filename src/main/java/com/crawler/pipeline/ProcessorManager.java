package com.crawler.pipeline;

import com.crawler.element.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hxb on 2018/4/8.
 */
@Component
public class ProcessorManager<E> {

    private ConcurrentHashMap<String, Processor>  processors = new ConcurrentHashMap<>();

    @Autowired
    UserToDiskProcessor userToDiskProcessor;

    @Autowired
    UserPrintProcessor userPrintProcessor;

    public ProcessorManager() {

    }

    public void registerProcessor(String clsName, Processor processor) {
        processors.put(clsName, processor);
    }

    public void setProcessors() {
        registerProcessor("User", userPrintProcessor);
        registerProcessor("User", userToDiskProcessor);

        userToDiskProcessor.prepare();
    }

    public void process(Object obj) {
         if (processors.containsKey(obj.getClass().getSimpleName())) {
             Processor processor = processors.get(obj.getClass().getSimpleName());
             processor.process(obj);
         }
    }
}

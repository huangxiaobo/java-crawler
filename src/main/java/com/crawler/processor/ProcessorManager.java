package com.crawler.processor;

import com.crawler.element.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hxb on 2018/4/8.
 */
public class ProcessorManager {


    private ConcurrentHashMap<String, Processor> clsProcessorMap = new ConcurrentHashMap<>();


    public ProcessorManager() {

    }

    public void registerProcessor(String clsName, Processor processor) {
        clsProcessorMap.put(clsName, processor);
    }

    public void setupProcessors() {

        Processor<User> userPrintProcessor = new UserPrintProcessor();
        registerProcessor("User", userPrintProcessor);

        UserToDiskProcessor userToDiskProcessor = new UserToDiskProcessor();
        userPrintProcessor.next = userToDiskProcessor;


    }

    public void process(Object obj) {
        if (clsProcessorMap.containsKey(obj.getClass().getSimpleName())) {
            Processor processor = clsProcessorMap.get(obj.getClass().getSimpleName());
            processor.process(obj);
        }
    }
}

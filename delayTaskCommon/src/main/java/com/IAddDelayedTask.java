package com;
import	java.util.concurrent.TimeUnit;

/**
 * 添加任务接口
 */
public interface IAddDelayedTask {

    void addDelayedTask(String topic, String key, long times, TimeUnit timeUnit);
}

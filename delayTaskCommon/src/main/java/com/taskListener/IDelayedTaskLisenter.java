package com.taskListener;

import com.IAddDelayedTask;

/**
 * 任务监听器接口
 */
public interface IDelayedTaskLisenter {

    boolean execute(String topic, String key, IAddDelayedTask addDelayedTask);
}

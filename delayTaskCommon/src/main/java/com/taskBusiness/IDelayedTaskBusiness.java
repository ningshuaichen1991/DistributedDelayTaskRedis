package com.taskBusiness;

import com.IDelayedTask;

/**
 * 任务监听器接口
 */
public interface IDelayedTaskBusiness {

    boolean execute(String topic, String key, IDelayedTask addDelayedTask);
}

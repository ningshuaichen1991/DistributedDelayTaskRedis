package com.passiveMonitor;

import com.IAddDelayedTask;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 添加被动监听的延时任务
 */
@Component
public class AddMonitorDelayedTask implements IAddDelayedTask {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void addDelayedTask(String topic, String key, long times, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().setIfAbsent(topic+"_"+key,key,times, timeUnit);
    }
}

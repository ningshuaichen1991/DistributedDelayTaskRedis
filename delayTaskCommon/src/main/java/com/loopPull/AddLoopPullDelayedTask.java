package com.loopPull;
import	java.time.LocalDateTime;

import com.IAddDelayedTask;
import com.enums.BusinessTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 添加循环拉取的任务数据
 */
@Component
@Slf4j
public class AddLoopPullDelayedTask implements IAddDelayedTask {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private LoopPullDelayedTaskListener loopPullDelayedTaskListener;


    @Override
    public void addDelayedTask(String topic, String key, long times, TimeUnit timeUnit) {
        BoundZSetOperations<String,String> zset = stringRedisTemplate.boundZSetOps(topic);
        LocalDateTime now = LocalDateTime.now();
        if(timeUnit==TimeUnit.SECONDS){//秒
            zset.add(key,now.plusSeconds(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }else if(timeUnit==TimeUnit.MINUTES){//分钟
            zset.add(key,now.plusMinutes(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }else if(timeUnit==TimeUnit.HOURS){//小时
            zset.add(key,now.plusHours(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }else if(timeUnit==TimeUnit.DAYS){//天
            zset.add(key,now.plusDays(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        Thread t = loopPullDelayedTaskListener.getLoopPullDelayedTaskThread(BusinessTypeEnum.getByValue(topic));//获取当前的topic线程如果waiting的状态则唤醒
        if(t.getState()== Thread.State.WAITING){
            LockSupport.unpark(t);
            log.info("topic：{} 线程已经从waiting中重新唤醒",topic);
        }
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        long a = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println(a);
        System.out.println((double) (a));
    }
}